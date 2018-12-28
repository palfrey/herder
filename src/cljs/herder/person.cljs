(ns herder.person
  (:require
   [herder.helpers :refer [convention-header nav!]]
   [herder.getter :refer [get-data person-url]]
   [herder.state :refer [state]]
   [reagent.core :as r]
   [cljs-time.periodic :as periodic]
   [cljs-time.core :as time]
   [cljs-time.format :as tf]
   [cljs-time.coerce :as coerce]
   [ajax.core :refer [PATCH DELETE]]))

(defn set-person-name [val]
  (js/console.log (pr-str val))
  (PATCH (person-url (:id @state) (:person_id @state))
    {:params {:name val}
     :format :json}))

(defn set-person-availability [date what]
  (js/console.log (pr-str val))
  (PATCH (person-url (:id @state) (:person_id @state))
    {:params {:available-date date :available-status what}
     :format :json}))

(def time-formatter (tf/formatters :date-time-no-ms (time/default-time-zone)))

(defn ^:export component []
  (let [person (get-data [:person (:id @state) (:person_id @state)])
        non-availability (map #(coerce/to-long (tf/parse time-formatter %)) (:non-availability person))
        convention (get-data [:convention (:id @state)])]
    [:div {:class "container-fluid"}
     [convention-header :persons]
     [:h2 "Person"]
     [:label {:for "name"} "Name "]
     [:input {:id "name"
              :type "text"
              :value (:name person)
              :on-change #(set-person-name (-> % .-target .-value))}]
     [:hr]
     [:h4 "Availability"]
     (if (not= convention {})
       (let [first-day (tf/parse time-formatter (:from convention))
             last-day (tf/parse time-formatter (:to convention))]
         (into [:p]
               (for [date (periodic/periodic-seq first-day (time/days 1))
                     :while (or (time/before? date last-day) (time/equal? date last-day))
                     :let [format-date (tf/unparse (tf/formatters :year-month-day) (time/to-default-time-zone date))]]
                 [:span
                  [:input {:type "checkbox" :checked (= -1 (.indexOf non-availability (coerce/to-long date)))
                           :on-change #(set-person-availability format-date (-> % .-target .-checked))}]
                  format-date " "]))))
     [:hr]
     [:h4 "Attending"]
     (into [:ul]
           (for [event_id (:events person)
                 :let [event (get-data [:event (:id @state) event_id])]]
             ^{:key event_id}
             [:li [:a {:href (str "#/events/" event_id)} (:name event)]]))
     [:hr]
     [:button {:type "button"
               :class "btn btn-danger"
               :on-click #(DELETE (person-url (:id @state) (:person_id @state))
                            {:handler
                             (fn [resp]
                               (nav! "/persons"))})}
      "Delete this person"]]))
