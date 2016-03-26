(ns herder.web.events
  (:require
   [bouncer.core :as b]
   [bouncer.validators :as v]
   [clj-uuid :as uuid]
   [korma.core :as d]
   [herder.web.db :as db]
   [ring.util.response :refer [response status]]
   [compojure.core :refer [GET POST PUT context]]))

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
         db/events-persons (d/values [{:event_id id
                                       :person_id person}])))
      (status (response {:id id}) 201))))

(defn get-event [{{:keys [id]} :params}]
  (let [event (first (d/select db/events (d/where {:id id})))]
    (if (nil? event)
      (status (response (str "No such event " id)) 404)
      (response (->
                 event
                 (#(assoc %
                          :persons (map :person_id (d/select db/events-persons (d/where {:event_id id}))))))))))

(defn get-events [{{:keys [id]} :params}]
  (let [events (d/select db/events (d/where {:convention_id id}))]
    (response events)))

(def uuid-regex #"[\w]{8}(-[\w]{4}){3}-[\w]{12}")

(def event-context
  (context "/event" []
    (GET "/" [] get-events)
    (GET ["/:id" :id uuid-regex] [id] get-event)
    (POST "/" [] new-event!)))
