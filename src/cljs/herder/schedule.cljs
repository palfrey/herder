(ns herder.schedule
  (:require
   [herder.helpers :refer [convention-header to-date]]
   [herder.state :refer [state]]
   [herder.getter :refer [get-data get-mapped-data]]))

(defn ^:export component []
  (let [schedule (get-data [:schedule (:id @state)])
        slots (get-mapped-data [:slots (:id @state)])
        schedule-issues (get-data [:schedule-issues (:id @state)])]
    [:div {:class "container-fluid"}
     [convention-header :schedule]
     [:h2 "Schedule"]
     (into [:ul]
           (for [{:keys [id date event_id slot_id]} (sort-by :date schedule)
                 :let [event (get-data [:event (:id @state) event_id])
                       slot (get slots slot_id)]]
             ^{:key id} [:li [:a {:href (str "#/events/" event_id)} (:name event)] " " (to-date date) " " (:start slot) "-" (:end slot)]))
     (if (-> schedule-issues empty? not)
       [:div
        [:h3 "Issues"]
        (into [:ul]
              (for [{:keys [id issue score level events]} (sort-by :level schedule-issues)]
                ^{:key id}
                [:li (if (= level 1) "soft" "hard") " " score ": " issue
                 (into [:ul]
                       (for [event_id events
                             :let [event (get-data [:event (:id @state) event_id])]]
                         [:li [:a {:href (str "#/events/" event_id)} (:name event)]]))]))])]))
