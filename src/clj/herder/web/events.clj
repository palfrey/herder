(ns herder.web.events
  (:require
   [bouncer.core :as b]
   [bouncer.validators :as v]
   [clj-uuid :as uuid]
   [korma.core :as d]
   [herder.web.db :as db]
   [ring.util.response :refer [response status]]
   [compojure.core :refer [GET POST PATCH DELETE context]))

(defn validate-new-event [params]
  (first
   (b/validate
    params
    :name [[v/required :message "Must have a name"]])))

(defn new-event! [{:keys [:params]}]
  (if-let [errors (validate-new-event params)]
    (-> (response {:errors errors})
        (status 400))
    (let [id (str (uuid/v1))
          persons (get params :persons [])
          persons (if (vector? persons) persons [persons])]
      (d/insert
       db/events (d/values [{:id id
                             :name (:name params)
                             :convention_id (:id params)}]))
      (doseq [person persons]
        (d/insert
         db/events-persons (d/values [{:convention_id (:id params)
                                       :event_id id
                                       :person_id person}])))
      (status (response {:id id}) 201))))

(defn- retrieve-event [id]
  (first (d/select db/events (d/where {:id id}))))

(defn- get-person-ids [id]
  (map :person_id (d/select db/events-persons (d/where {:event_id id}))))

(defn get-event [{{:keys [id]} :params}]
  (let [event (retrieve-event id)]
    (if (nil? event)
      (status (response (str "No such event " id)) 404)
      (response (assoc event :persons (get-person-ids id))))))

(defn get-events [{{:keys [id]} :params}]
  (let [events (d/select db/events (d/where {:convention_id id}))]
    (response events)))

(defn delete-event [{{:keys [id]} :params}]
  (let [event (d/delete db/events (d/where {:id id}))]
    (status (response {}) (if (> event 0) 200 404))))

(defn delete-event-person [{{:keys [id person_id]} :params}]
  (let [event (d/delete db/events-persons (d/where {:event_id id :person_id person_id}))]
    (status (response {}) (if (> event 0) 200 404))))

(defn patch-event [{{:keys [id person]} :params}]
  (let [event (retrieve-event id)]
    (if (nil? event)
      (status (response (str "No such event " id)) 404)
      (let [persons (get-person-ids id)
            return (assoc event :persons persons)]
        (if (not (.contains persons person))
          (do
            (d/insert
             db/events-persons (d/values [{:convention_id (:convention_id event)
                                           :event_id id
                                           :person_id person}]))
            (response (update return :persons #(conj % person))))
          (response return))))))

(def uuid-regex #"[\w]{8}(-[\w]{4}){3}-[\w]{12}")

(def event-context
  (context "/event" []
    (GET "/" [] get-events)
    (context "/:id" [id]
      (GET ["/"] [id] get-event)
      (PATCH ["/"] [id] patch-event)
      (DELETE ["/"] [id] delete-event)
      (DELETE ["/person/:person_id" :person_id uuid-regex] [person_id] delete-event-person))
    (POST "/" [] new-event!)))
