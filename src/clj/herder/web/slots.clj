(ns herder.web.slots
  (:require
   [bouncer.core :as b]
   [bouncer.validators :as v]
   [clj-uuid :as uuid]
   [clj-time.core :as t]
   [clj-time.format :as f]
   [korma.core :as d]
   [herder.web.db :as db]
   [ring.util.response :refer [response status]]
   [compojure.core :refer [GET POST PUT context]]))

(def time-format (f/formatter "hh:mm"))

(defn validate-new-slot [params]
  (first
   (b/validate
    params
    :start [v/required [v/datetime time-format :message "start must be a valid time"]]
    :end [v/required [v/datetime time-format :message "end must be a valid time"]])))

(defn time->minutes [time]
  (let [when (f/parse time-format time)]
    (+ (t/minute when) (* 60 (t/hour when)))))

(defn new-slot! [{:keys [:params]}]
  (if-let [errors (validate-new-slot params)]
    (-> (response {:errors errors})
        (status 400))
    (let [id (str (uuid/v1))]
      (d/insert
       db/slots (d/values [{:id id
                            :start-minutes (-> params :start time->minutes)
                            :end-minutes (-> params :end time->minutes)
                            :convention_id (:id params)}]))
      (status (response {:id id}) 201))))

(defn minutes->time [minutes]
  (f/unparse time-format (t/plus (t/date-time 1970 1 1) (t/minutes minutes))))

(defn reformat-slot [slot]
  (->
   slot
   (#(assoc %
            :start (minutes->time (:start-minutes %))
            :end (minutes->time (:end-minutes %))))
   (#(dissoc % :start-minutes :end-minutes))))

(defn get-slot [{{:keys [id]} :params}]
  (let [slot (first (d/select db/slots (d/where {:id id})))]
    (if (nil? slot)
      (status (response (str "No such slot " id)) 404)
      (response (reformat-slot slot)))))

(defn get-slots [{{:keys [id]} :params}]
  (let [slots (d/select db/slots (d/where {:convention_id id}))]
    (response (map reformat-slot slots))))

(def uuid-regex #"[\w]{8}(-[\w]{4}){3}-[\w]{12}")

(def slot-context
  (context "/slot" []
    (GET "/" [] get-slots)
    (GET ["/:id" :id uuid-regex] [id] get-slot)
    (POST "/" [] new-slot!)))
