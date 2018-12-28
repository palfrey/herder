(ns herder.web.schedule
  (:require
   [ring.util.response :refer [response]]
   [korma.core :as d]
   [herder.web.db :as db]
   [compojure.core :refer [GET context]]
   [herder.uuid :refer [to-uuid]]
   [system.repl :refer [system]]))

(defn get-schedule [{{:keys [id]} :params}]
  (let [schedules (d/select db/schedule (d/where {:convention_id (to-uuid id)}))]
    (response schedules)))

(defn get-schedule-issues [{{:keys [id]} :params}]
  (let [schedule-issues (d/select db/schedule-issues (d/where {:convention_id (to-uuid id)}))
        schedule-issues-events (d/select db/schedule-issues-events (d/where {:convention_id (to-uuid id)}))
        add-events (fn [issue] (assoc issue :events (map :event_id (filter #(= (:id issue) (:schedule-issue_id %)) schedule-issues-events))))
        schedule-issues (map add-events schedule-issues)]
    (response schedule-issues)))

(defn get-status [{{:keys [id]} :params}]
  (let [tosolve @(-> system :solver :tosolve)]
    (response {:solved (not (.contains tosolve (to-uuid id)))})))

(def schedule-context
  (context "/schedule" []
    (GET "/" [] get-schedule)
    (GET "/issues" [] get-schedule-issues)
    (GET "/status" [] get-status)))
