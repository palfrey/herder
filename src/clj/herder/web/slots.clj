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
    :start [[v/datetime time-format :message "start must be a valid time"]]
    :end [[v/datetime time-format :message "end must be a valid time"]])))

(defn minutes [time]
  (let [when (f/parse time-format time)]
    (+ (t/minute when) (* 60 (t/hour when)))))

(defn new-slot! [{:keys [:params]}]
  (if-let [errors (validate-new-slot params)]
    (-> (response {:errors errors})
        (status 400))
    (let [id (str (uuid/v1))]
      (d/insert
       db/slots (d/values [{:id id
                            :start-minutes (-> params :start minutes)
                            :end-minutes (-> params :end minutes)
                            :convention_id (:id params)}]))
      (status (response {:id id}) 201))))

(def slot-context
  (context "/slot" []
    (POST "/" [] new-slot!)))
