(ns herder.event
  (:require
   [herder.helpers :refer [get-data state convention-url convention-header]]
   [reagent.core :as r]
   [herder.persons :refer [get-persons person-url]]
   [ajax.core :refer [PATCH DELETE]]))

(defn event-url []
  (str (convention-url) "/event/" (:event_id @state)))

(defn get-event [& {:keys [refresh]}]
  (get-data (keyword (str "event_" (:event_id @state))) (event-url) :refresh refresh))

(defn get-person [id]
  (merge {:id id}
         (get-data (keyword (str "person_" id)) (person-url id))))

(defn add-new [val]
  (js/console.log (pr-str @val))
  (PATCH (event-url)
    {:params {:person (:person @val)}
     :format :json
     :handler
     (fn [resp]
       (get-event :refresh true))}))

(defn ^:export component []
  (let [val (r/atom {:person ""})]
    (fn []
      [:div {:class "container-fluid"}
       [convention-header :events]
       [:h2 "Event: " (:name (get-event))]
       [:hr]
       [:h4 "People"]
       (into [:ul]
             (for [{:keys [id name]} (map get-person (:persons (get-event)))]
               ^{:key id} [:li name " "
                           [:button {:type "button"
                                     :class "btn btn-danger"
                                     :on-click #(DELETE (str (event-url) "/person/" id)
                                                  {:handler
                                                   (fn [resp] (get-event :refresh true))})}
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
          (for [{:keys [id name]} (get-persons)]
            ^{:key id} [:option {:value id} name])]

         [:button {:type "button"
                   :class "btn btn-primary"
                   :style {:margin-left "5px"}
                   :disabled (= "" (:person @val))
                   :on-click #(add-new val)}
          "Add person"]]]])))
