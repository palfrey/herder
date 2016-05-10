(ns herder.web.persons
  (:require
   [bouncer.core :as b]
   [bouncer.validators :as v]
   [clj-uuid :as uuid]
   [korma.core :as d]
   [herder.web.db :as db]
   [ring.util.response :refer [response status]]
   [compojure.core :refer [GET POST PATCH DELETE context]]
   [herder.web.notifications :as notifications]
   [herder.web.solve :refer [solve]]
   [herder.web.dates :refer [to-sql-date]])
  (:import
   [java.util UUID]))

(defn validate-new-person [params]
  (first
   (b/validate
    params
    :name [[v/required :message "Must have a name"]])))

(defn new-person! [{:keys [:params]}]
  (if-let [errors (validate-new-person params)]
    (-> (response {:errors errors})
        (status 400))
    (let [id (str (uuid/v1))
          conv_id (:id params)]
      (d/insert
       db/persons (d/values [{:id (UUID/fromString id)
                              :name (:name params)
                              :convention_id (UUID/fromString conv_id)}]))
      (notifications/send-notification [:persons conv_id])
      (solve conv_id)
      (status (response {:id id}) 201))))

(defn- get-event-ids [id]
  (map #(-> % :event_id str) (d/select db/events-persons (d/where {:person_id (UUID/fromString id)}))))

(defn- get-person-non-availability [id]
  (map :date (d/select db/person-non-availability (d/where {:person_id (UUID/fromString id)}))))

(defn get-person [{{:keys [id]} :params}]
  (let [person (first (d/select db/persons (d/where {:id (UUID/fromString id)})))]
    (if (nil? person)
      (status (response (str "No such person " id)) 404)
      (response (assoc person
                       :events (get-event-ids id)
                       :non-availability (get-person-non-availability id))))))

(defn get-persons [{{:keys [id]} :params}]
  (let [persons (d/select db/persons (d/where {:convention_id (UUID/fromString id)}) (d/order :name))]
    (response persons)))

(defn delete-person [{{:keys [id]} :params}]
  (let [person (first (d/select db/persons (d/where {:id (UUID/fromString id)})))]
    (if (-> person nil? not)
      (do
        (d/delete db/persons (d/where {:id (UUID/fromString id)}))
        (notifications/send-notification [:persons (str (:convention_id person))])
        (solve (:convention_id person))
        (status (response {}) 200))
      (status (response {}) 404))))

(defn patch-person [{{:keys [id name available-date available-status] :as params} :params}]
  (let [person (first (d/select db/persons (d/where {:id (UUID/fromString id)})))]
    (if (-> person nil? not)
      (let [conv_id (:convention_id person)]
        (if (contains? params :name)
          (d/update db/persons (d/set-fields {:name name}) (d/where {:id (UUID/fromString id)})))
        (if (contains? params :available-date)
          (do
            (if available-status
              (d/delete db/person-non-availability (d/where {:person_id (UUID/fromString id) :date (to-sql-date available-date)}))
              (d/insert db/person-non-availability (d/values {:person_id (UUID/fromString id) :date (to-sql-date available-date) :convention_id conv_id})))
            (solve conv_id)))
        (notifications/send-notification [:person (str conv_id) id])
        (notifications/send-notification [:persons (str conv_id)])
        (status (response {}) 200))
      (status (response (str "No such person " id)) 404))))

(def uuid-regex #"[\w]{8}(-[\w]{4}){3}-[\w]{12}")

(def person-context
  (context "/person" []
    (GET "/" [] get-persons)
    (context "/:id" [id]
      (GET "/" [id] get-person)
      (PATCH "/" [id] patch-person)
      (DELETE "/" [id] delete-person))
    (POST "/" [] new-person!)))
