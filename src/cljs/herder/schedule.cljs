(ns herder.schedule
  (:require
   [herder.helpers :refer [convention-header to-date]]
   [herder.state :refer [state]]
   [herder.getter :refer [get-data get-mapped-data]]))

(defn ^:export component []
  (let [schedule (get-data [:schedule (:id @state)])
        slots (get-mapped-data [:slots (:id @state)])]
    [:div {:class "container-fluid"}
     [convention-header :schedule]
     [:h2 "Schedule"]
     (into [:ul]
           (for [{:keys [id date event_id slot_id]} (sort-by :date schedule)
                 :let [event (get-data [:event (:id @state) event_id])
                       slot (get slots slot_id)]]
             ^{:key id} [:li [:a {:href (str "#/events/" event_id)} (:name event)] " " (to-date date) " " (:start slot)]))]))
