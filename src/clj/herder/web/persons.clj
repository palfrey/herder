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
   [herder.web.solve :refer [solve]]))

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
       db/persons (d/values [{:id id
                              :name (:name params)
                              :convention_id conv_id}]))
      (notifications/send-notification [:persons conv_id])
      (solve conv_id)
      (status (response {:id id}) 201))))

(defn get-person [{{:keys [id]} :params}]
  (let [person (first (d/select db/persons (d/where {:id id})))]
    (if (nil? person)
      (status (response (str "No such person " id)) 404)
      (response person))))

(defn get-persons [{{:keys [id]} :params}]
  (let [persons (d/select db/persons (d/where {:convention_id id}) (d/order :name))]
    (response persons)))

(defn delete-person [{{:keys [id]} :params}]
  (let [person (first (d/select db/persons (d/where {:id id})))]
    (if (-> person nil? not)
      (do
        (d/delete db/persons (d/where {:id id}))
        (notifications/send-notification [:persons (str (:convention_id person))])
        (solve (:convention_id person))
        (status (response {}) 200))
      (status (response {}) 404))))

(defn patch-person [{{:keys [id name]} :params}]
  (let [person (first (d/select db/persons (d/where {:id id})))]
    (if (-> person nil? not)
      (do
        (d/update db/persons (d/set-fields {:name name}) (d/where {:id id}))
        (notifications/send-notification [:person (str (:convention_id person)) id])
        (notifications/send-notification [:persons (str (:convention_id person))])
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
