(ns herder.web.conventions
  (:require
   [bouncer.core :as b]
   [bouncer.validators :as v]
   [clj-uuid :as uuid]
   [clj-time.format :as f]
   [clj-time.coerce :as c]
   [korma.core :as d]
   [ring.util.response :refer [response status]]
   [compojure.core :refer [defroutes GET POST PATCH DELETE context]]

   [herder.web.db :as db]
   [herder.web.slots :as slots]
   [herder.web.persons :as persons]
   [herder.web.events :as events]
   [herder.web.schedule :as schedule]
   [herder.web.notifications :as notifications]
   [herder.web.solve :refer [solve]]
   [herder.uuid :refer [to-uuid]]
   [herder.web.dates :refer [to-sql-date date-format]]))

(defn validate-new-convention [params]
  (first
   (b/validate
    params
    :conventionName [[v/required :message "Need a name for the convention"]]
    :from [v/required [v/datetime date-format]]
    :to [v/required [v/datetime date-format]])))

(defn save-new-convention! [{:keys [:params]}]
  (if-let [errors (validate-new-convention params)]
    (-> (response {:errors errors})
        (status 400))
    (let [id (uuid/v1)]
      (d/insert
       db/conventions
       (d/values [{:id id
                   :name (:conventionName params)
                   :from (c/to-sql-date (f/parse date-format (:from params)))
                   :to (c/to-sql-date (f/parse date-format (:to params)))}]))
      (notifications/send-notification [:conventions])
      (status (response {:id id}) 201))))

(defn get-convention [{{:keys [id]} :params}]
  (let [convention (d/select db/conventions (d/where {:id (to-uuid id)}))]
    (response (first convention))))

(defn delete-convention [{{:keys [id]} :params}]
  (let [convention (d/delete db/conventions (d/where {:id (to-uuid id)}))]
    (notifications/send-notification [:conventions])
    (status (response {}) (if (> convention 0) 200 404))))

(defn patch-convention [{{:keys [id name from to] :as params} :params}]
  (let [convention (first (d/select db/conventions (d/where {:id (to-uuid id)})))]
    (if (-> convention nil? not)
      (do
        (if (-> name nil? not)
          (d/update db/conventions (d/set-fields {:name name}) (d/where {:id (to-uuid id)})))
        (if (-> from nil? not)
          (do
            (d/update db/conventions (d/set-fields {:from from :to to}) (d/where {:id (to-uuid id)}))
            (solve id)))
        (notifications/send-notification [:convention id])
        (status (response {}) 200))
      (status (response (str "No such convention " id)) 404))))

(defn list-conventions [params]
  (let [conventions (d/select db/conventions)]
    (response conventions)))

(defroutes convention-routes
  (context "/convention" []
    (GET "/" [] list-conventions)
    (POST "/" [] save-new-convention!)
	      ;(context ["/:id" :id uuid-regex] [id]) - FIXME
    (context "/:id" [id]
      (GET "/" [id] get-convention)
      (PATCH "/" [id] patch-convention)
      (DELETE "/" [id] delete-convention)
      slots/slot-context
      persons/person-context
      events/event-context
      schedule/schedule-context)))
