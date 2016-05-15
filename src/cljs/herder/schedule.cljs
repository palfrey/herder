(ns herder.schedule
  (:require
   [herder.helpers :refer [convention-header to-date]]
   [herder.state :refer [state]]
   [herder.getter :refer [get-data get-mapped-data]]
   [goog.string :as gstring]))

(def count-map
  {1 ""
   2 "Second"
   3 "Third"
   4 "Fourth"
   5 "Fifth"})

(defn make-link [item]
  (let [event (get-data [:event (:id @state) (:event_id item)])
        event_day (:event_day item)
        day-name (if (= 1 event_day) "" (gstring/format "(%s) " (get count-map (js/parseInt event_day))))]
    [[:a {:href (str "#/events/" (:id event))} (:name event)] " " day-name]))

(defn ^:export component []
  (let [schedule (get-data [:schedule (:id @state)])
        slots (get-mapped-data [:slots (:id @state)])
        schedule-issues (get-data [:schedule-issues (:id @state)])
        slot-width (quot 11 (count slots))
        days (-> (map :date schedule) distinct sort)
        status (get-data [:status (:id @state)])]
    [:div {:class "container-fluid"}
     [convention-header :schedule]
     [:h4 {:style {:float "right" :color "red"}} (if (:solved status) "" "updating...")]
     [:h2 "Schedule"]
     [:table.table
      [:thead
       [:tr
        (cons
         (with-meta [:th ""] {:key "corner-slot"})
         (for [slot (sort-by :start (vals slots))]
           (with-meta [:th (:start slot) "-" (:end slot)] {:key (:id slot)})))]]
      (into [:tbody]
            (for [day days]
              [:tr
               (cons
                (with-meta [:td (to-date day)] {:key day})
                (doall (for [slot_id (map :id (sort-by :start (vals slots)))
                             :let [items (filter #(and
                                                   (= (:date %) day)
                                                   (= (:slot_id %) slot_id))
                                                 schedule)
                                   links (mapv make-link items)
                                   links (cons (first links) (map #(cons "/ " %) (rest links)))
                                   link (into [:td] (apply concat links))]]
                         (with-meta link {:key (str slot_id "-" day)}))))]))]
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
