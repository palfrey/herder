(ns herder.event
  (:require
   [herder.helpers :refer [convention-header nav!]]
   [herder.getter :refer [get-data get-mapped-data event-url]]
   [herder.state :refer [state]]
   [reagent.core :as r]
   [ajax.core :refer [PATCH DELETE]]
   [clojure.set :refer [difference]]))

(defn get-person [id]
  (merge {:id id}
         (get-data [:person (:id @state) id])))

(defn- patch-event [value]
  (js/console.log (pr-str value))
  (PATCH (event-url (:id @state) (:event_id @state))
    {:params value
     :format :json}))

(defn add-new [val]
  (patch-event {:person (:person @val)}))

(defn set-preferred-slot [val]
  (patch-event {:preferred_slot val}))

(defn set-event-count [val]
  (patch-event {:event_count val}))

(defn set-event-name [val]
  (patch-event {:name val}))

(defn set-event-type [val]
  (patch-event {:type val}))

(defn ^:export component []
  (let [val (r/atom {:person ""})]
    (fn []
      (let [event (get-data [:event (:id @state) (:event_id @state)])
            convention (get-data [:convention (:id @state)])
            slots (get-data [:slots (:id @state)])
            days (+ 1 (/ (- (js/moment (:to convention)) (js/moment (:from convention))) 86400000))
            persons (get-mapped-data [:persons (:id @state)])
            event_type (keyword (:event_type event))]
        [:div {:class "container-fluid"}
         [convention-header :events]
         [:h2 "Event"]
         [:label {:for "name"} "Name "]
         [:input {:id "name"
                  :type "text"
                  :value (:name event)
                  :on-change #(set-event-name (-> % .-target .-value))}]

         [:h4 "People"]
         [:div.row
          (into [:ul]
                (for [{:keys [id name]} (sort-by :name (map get-person (:persons event)))]
                  ^{:key id} [:div
                              [:div.col-md-2
                               [:a {:class "pull-xs-right"
                                    :style {:line-height "38px"}
                                    :href (str "#/person/" id)}
                                name " "]]
                              [:div.col-md-2
                               [:button {:type "button"
                                         :class "btn btn-danger"
                                         :on-click #(DELETE (str (event-url (:id @state) (:event_id @state)) "/person/" id))}
                                (str "Remove " name)]]]))]
         [:hr]
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
            (for [id (sort-by #(:name (get persons %)) (difference (set (keys persons)) (set (:persons event))))
                  :let [name (:name (get persons id))]]
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
          (for [{:keys [id start end]} slots]
            ^{:key id} [:option {:value id} (str start "-" end)])]
         [:hr]
         [:h3 "Event type"]
         (let [attrs {:type "radio"
                      :name "event_type"
                      :on-change #(let [type (-> % .-target .-value)]
                                    (set-event-type type)
                                    (if (= type "single")
                                      (set-event-count 1)
                                      (set-event-count 2)))}
               radio_attrs
               (fn [wanted]
                 (let [att (assoc attrs :value (name wanted))]
                   (if (= event_type wanted) (assoc att :checked true) att)))]
           (into [:div]
                 (concat
                  [[:input (radio_attrs :single)] " Single slot" [:br]]
                  (if (= (count slots) 0) []
                      (concat
                       [[:input (radio_attrs :one_day)] " Multiple slots, one day" [:br]]
                       (if (not= event_type :one_day) []
                           [[:select {:id "slot_count"
                                      :value (:event_count event)
                                      :on-change #(set-event-count (-> % .-target .-value))}
                             (for [value (range 2 (+ (count slots) 1))]
                               ^{:key value} [:option {:value value} value " slots"])] [:br]])))
                  (if (= days 0) []
                      (concat
                       [[:input (radio_attrs :multiple_days)] " One slot per day, multiple days" [:br]]
                       (if (not= event_type :multiple_days) []
                           [[:select {:id "day_count"
                                      :value (:event_count event)
                                      :on-change #(set-event-count (-> % .-target .-value))}
                             (for [value (range 2 (+ days 1))]
                               ^{:key value} [:option {:value value} value " days"])]]))))))
         [:hr]
         [:button {:type "button"
                   :class "btn btn-danger"
                   :on-click #(DELETE (event-url (:id @state) (:event_id @state))
                                {:handler
                                 (fn [resp]
                                   (nav! "/events"))})}
          "Delete this event"]]))))
