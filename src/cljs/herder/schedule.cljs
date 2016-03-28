(ns herder.schedule
  (:require
   [herder.helpers :refer [get-data get-mapped-data state convention-url convention-header to-date]]
   [herder.event :refer [get-event]]
   [herder.slots :refer [slots-url]]
   [reagent.core :as r]))

(defn schedule-url []
  (str (convention-url) "/schedule"))

(defn get-schedule [& {:keys [refresh]}]
  (get-data :schedule (schedule-url) :refresh refresh))

(defn ^:export component []
  (let [schedule (get-schedule)
        slots (get-mapped-data :slots (slots-url))]
    [:div {:class "container-fluid"}
     [convention-header :schedule]
     [:h2 "Schedule"]
     (into [:ul]
           (for [{:keys [id date event_id slot_id]} schedule
                 :let [event (get-event :event_id event_id)
                       slot (get slots slot_id)]]
             ^{:key id} [:li [:a {:href (str "#/events/" event_id)} (:name event)] " " (to-date date) " " (:start slot)]))]))
