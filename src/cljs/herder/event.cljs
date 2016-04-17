(ns herder.event
  (:require
   [herder.helpers :refer [convention-header nav!]]
   [herder.getter :refer [get-data event-url]]
   [herder.state :refer [state]]
   [reagent.core :as r]
   [ajax.core :refer [PATCH DELETE]]))

(defn get-person [id]
  (merge {:id id}
         (get-data [:person (:id @state) id])))

(defn add-new [val]
  (js/console.log (pr-str @val))
  (PATCH (event-url (:id @state) (:event_id @state))
    {:params {:person (:person @val)}
     :format :json}))

(defn set-preferred-slot [val]
  (js/console.log (pr-str val))
  (PATCH (event-url (:id @state) (:event_id @state))
    {:params {:preferred_slot val}
     :format :json}))

(defn set-event-count [val]
  (js/console.log (pr-str val))
  (PATCH (event-url (:id @state) (:event_id @state))
    {:params {:event_count val}
     :format :json}))

(defn ^:export component []
  (let [val (r/atom {:person ""})]
    (fn []
      (let [event (get-data [:event (:id @state) (:event_id @state)])
            convention (get-data [:convention (:id @state)])
            days (+ 1 (/ (- (js/moment (:to convention)) (js/moment (:from convention))) 86400000))]
        [:div {:class "container-fluid"}
         [convention-header :events]
         [:h2 "Event: " (:name event)]
         [:hr]
         [:h4 "People"]
         (into [:ul]
               (for [{:keys [id name]} (map get-person (:persons event))]
                 ^{:key id} [:li name " "
                             [:button {:type "button"
                                       :class "btn btn-danger"
                                       :on-click #(DELETE (str (event-url (:id @state) (:event_id @state)) "/person/" id))}
                              (str "Remove " name)]]))
         [:form {:class "form-inline"
                 :on-submit #(do
                               (.preventDefault %)
                               (add-new val)
                               false)}
          [:div {:class "form-group"}
           [:label {:for "person"} "Add "]

           [:select {:id "person"
                     :value (:person @val)
                     :on-change #(swap! val assoc :person (-> % .-target .-value))}
            [:option {:value ""} " Select "]
            (for [{:keys [id name]} (get-data [:persons (:id @state)])]
              ^{:key id} [:option {:value id} name])]

           [:button {:type "button"
                     :class "btn btn-primary"
                     :style {:margin-left "5px"}
                     :disabled (= "" (:person @val))
                     :on-click #(add-new val)}
            "Add person"]]]
         [:hr]
         [:h3 "Preferred slot"]
         [:select {:id "preferred_slot"
                   :value (:preferred_slot_id event)
                   :on-change #(set-preferred-slot (-> % .-target .-value))}
          [:option {:value ""} "Any"]
          (for [{:keys [id start end]} (get-data [:slots (:id @state)])]
            ^{:key id} [:option {:value id} (str start "-" end)])]
         [:hr]
         [:h3 "Event count"]
         [:select {:id "event_count"
                   :value (:event_count event)
                   :on-change #(set-event-count (-> % .-target .-value))}
          [:option {:value "1"} "Single event"]
          (js/console.log "days" days)
          (for [value (range 2 (+ days 1))]
            ^{:key value} [:option {:value value} value])]
         [:hr]
         [:button {:type "button"
                   :class "btn btn-danger"
                   :on-click #(DELETE (event-url (:id @state) (:event_id @state))
                                {:handler
                                 (fn [resp]
                                   (nav! "/events"))})}
          "Delete this event"]]))))
