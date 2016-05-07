(ns herder.web.events
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
   [clojure.set :refer [map-invert]]))

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
          conv_id (:id params)
          persons (get params :persons [])
          persons (if (vector? persons) persons [persons])]
      (d/insert
       db/events (d/values [{:id id
                             :name (:name params)
                             :convention_id conv_id}]))
      (doseq [person persons]
        (d/insert
         db/events-persons (d/values [{:convention_id conv_id
                                       :event_id id
                                       :person_id person}])))
      (notifications/send-notification [:events conv_id])
      (solve conv_id)
      (status (response {:id id}) 201))))

(defn- retrieve-event [id]
  (first (d/select db/events (d/where {:id id}))))

(defn- get-person-ids [id]
  (sort (map #(-> % :person_id str) (d/select db/events-persons (d/where {:event_id id})))))

(def event-type-map
  {:single 1
   :one_day 2
   :multiple_days 3})

(defn- set-event-fields [event]
  (assoc event
         :persons (get-person-ids (:id event))
         :event_type (get (map-invert event-type-map) (:event_type event))))

(defn get-event [{{:keys [id]} :params}]
  (let [event (retrieve-event id)]
    (if (nil? event)
      (status (response (str "No such event " id)) 404)
      (response (set-event-fields event)))))

(defn get-events [{{:keys [id]} :params}]
  (let [events (d/select db/events (d/where {:convention_id id}))]
    (response events)))

(defn delete-event [{{:keys [id]} :params}]
  (let [event (first (d/select db/events (d/where {:id id})))]
    (if (nil? event)
      (status (response {}) 404)
      (do
        (d/delete db/events (d/where {:id id}))
        (notifications/send-notification [:events (str (:convention_id event))])
        (solve (:convention_id event))
        (status (response {}) 200)))))

(defn delete-event-person [{{:keys [id person_id]} :params}]
  (let [event-person (first (d/select db/events-persons (d/where {:event_id id :person_id person_id})))]
    (if (nil? event-person)
      (status (response {}) 404)
      (do
        (d/delete db/events-persons (d/where {:event_id id :person_id person_id}))
        (notifications/send-notification [:event (str (:convention_id event-person)) id])
        (solve (:convention_id event-person))
        (status (response {}) 200)))))

(defn patch-event [{{:keys [id person preferred_slot event_count name type] :as params} :params}]
  (let [event (retrieve-event id)]
    (if (nil? event)
      (status (response (str "No such event " id)) 404)
      (let [conv_id (:convention_id event)
            preferred_slot (if (= "" preferred_slot) nil preferred_slot)]
        (if (contains? params :preferred_slot)
          (d/update db/events (d/set-fields {:preferred_slot_id preferred_slot}) (d/where {:id id})))
        (if (-> event_count nil? not)
          (d/update db/events (d/set-fields {:event_count event_count}) (d/where {:id id})))
        (if (-> name nil? not)
          (d/update db/events (d/set-fields {:name name}) (d/where {:id id})))
        (if (-> type nil? not)
          (do
            (println "event type" type (get event-type-map type))
            (d/update db/events (d/set-fields {:event_type (get event-type-map (keyword type))}) (d/where {:id id}))))

        (if (-> person nil? not)
          (let [persons (get-person-ids id)]
            (if (not (.contains persons person))
              (d/insert
               db/events-persons (d/values [{:convention_id conv_id
                                             :event_id id
                                             :person_id person}])))))
        (if (-> name nil?) ; don't solve for name, because it slows things down
          (solve conv_id))
        (notifications/send-notification [:event (str conv_id) id])
        (response (set-event-fields event))))))

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
