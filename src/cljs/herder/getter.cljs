(ns herder.getter
  (:require
   [ajax.core :refer [GET]]
   [clojure.walk :refer [keywordize-keys]]
   [herder.state :refer [state]]))

(defn key-handler [key data]
  (js/console.log "Response" (str key) (pr-str data))
  (swap! state assoc key (keywordize-keys data)))

(defn get-raw-data [key url & {:keys [refresh] :or {refresh false}}]
  (if (and (not refresh) (contains? @state key))
    (get @state key)
    (do
      (GET url {:handler (partial key-handler key)})
      {})))

(defmulti get-data (fn [key] (first key)))

(defmethod get-data :conventions [[_] & {:keys [refresh] :or {refresh false}}]
  (get-raw-data [:conventions] "/api/convention" :refresh refresh))

(defn convention-url [id]
  (str "/api/convention/" id))

(defmethod get-data :convention [[_ id] & {:keys [refresh] :or {refresh false}}]
  (get-raw-data [id :convention] (convention-url id) :refresh refresh))

(defn slots-url [id]
  (str (convention-url id) "/slot"))

(defmethod get-data :slots [[_ id] & {:keys [refresh] :or {refresh false}}]
  (get-raw-data [id :slots] (slots-url id) :refresh refresh))

(defn persons-url [id]
  (str (convention-url id) "/person"))

(defmethod get-data :persons [[_ id] & {:keys [refresh] :or {refresh false}}]
  (get-raw-data [id :persons] (persons-url id) :refresh refresh))

(defn person-url [conv_id person_id]
  (str (persons-url conv_id) "/" person_id))

(defmethod get-data :person [[_ conv_id person_id] & {:keys [refresh] :or {refresh false}}]
  (get-raw-data [conv_id :person person_id] (person-url conv_id person_id) :refresh refresh))

(defn events-url [id]
  (str (convention-url id) "/event"))

(defmethod get-data :events [[_ id] & {:keys [refresh] :or {refresh false}}]
  (get-raw-data [id :events] (events-url id) :refresh refresh))

(defn event-url [conv_id event_id]
  (str (convention-url conv_id) "/event/" event_id))

(defmethod get-data :event [[_ conv_id event_id] & {:keys [refresh] :or {refresh false}}]
  (get-raw-data [conv_id :event event_id] (event-url conv_id event_id) :refresh refresh))

(defn schedule-url [id]
  (str (convention-url id) "/schedule"))

(defmethod get-data :schedule [[_ id] & {:keys [refresh] :or {refresh false}}]
  (get-raw-data [id :schedule] (schedule-url id) :refresh refresh))

(defmethod get-data :default [params]
  (js/console.log "get-data: Don't know key" (pr-str params)))

(defn get-mapped-data [& args]
  (apply hash-map (apply concat (mapv #(vector (:id %) %) (apply get-data args)))))

(defn parse-ws [{[kind data] :event send :send-fn :as stuff}]
  (js/console.log "event" (pr-str kind) (pr-str data))
  (if (and (= kind :chsk/state) (:first-open? data))
    (send
     [::page (select-keys @state [:component :id])]))
  (if (= kind :chsk/recv)
    (let [[type data] data]
      (if (= type :herder.web.notifications/cache-invalidate)
        (doseq [key data]
          (js/console.log "delete" (pr-str key))
          (get-data key :refresh true))
        (js/console.log "something else" type)))))
