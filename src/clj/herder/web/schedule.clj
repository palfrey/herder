(ns herder.web.schedule
  (:require
   [ring.util.response :refer [response]]
   [korma.core :as d]
   [herder.web.db :as db]
   [compojure.core :refer [GET context]]))

(defn get-schedule [{{:keys [id]} :params}]
  (let [schedules (d/select db/schedule (d/where {:id id}))]
    (response schedules)))

(def schedule-context
  (context "/schedule" []
    (GET "/" [] get-schedule)))
