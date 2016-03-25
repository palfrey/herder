(ns herder.slots
  (:require
   [herder.helpers :refer [get-data state to-date]]
   [ajax.core :refer [POST DELETE]]
   [reagent.core :as r]
   [cljsjs.jquery-timepicker]))

(defn convention-url []
  (str "/api/convention/" (:id @state)))

(defn get-convention [& {:keys [refresh]}]
  (get-data :info (convention-url) :refresh refresh))

(defn slots-url []
  (str (convention-url) "/slot"))

(defn get-slots [& {:keys [refresh]}]
  (get-data :slots (slots-url) :refresh refresh))

(defn timerange [val key initial]
  (r/create-class
   {:reagent-render
    (fn []
      [:input {:type "text"
               :name (name key)
               :class "form-control"
               :data-scroll-default initial
               :value
               (let [value (key @val)]
                 (if (nil? value)
                   ""
                   value))}])
    :component-did-mount #(->
                           (.timepicker (js/$ (r/dom-node %)) (js-obj "timeFormat" "h:i"))
                           (.bind "changeTime"
                                  (fn [event obj]
                                    (swap! val assoc key
                                           (-> event .-target .-value)))))}))

(defn ^:export component []
  (let [val (r/atom {:fromTime nil :toTime nil})]
    (fn []
      (let [convention (get-convention)]
        [:div {:class "container-fluid"}
         [:h1 "Convention: " (:name convention)]
         [:h4 "Runs between " (to-date (:from convention)) " and " (to-date (:to convention))]
         [:hr]
         [:h2 "Slots"]
         [:ul
          (for [{:keys [id start end]} (get-slots)]
            ^{:key id} [:li start " to " end " "
                        [:button {:type "button"
                                  :class "btn btn-danger"
                                  :on-click #(DELETE (str (slots-url) "/" id)
                                               {:handler
                                                (fn [resp] (get-slots :refresh true))})}
                         (str "Delete " start "-" end)]])]
         [:hr]
         [:form {:method "POST" :class "form-inline"}
          [:div {:class "form-group"}
           [:label {:for "fromTime"} "Add Slot: between"]
           [timerange val :fromTime "9:00am"]
           [:label {:for "toTime"} "and"]
           [timerange val :toTime "12:00pm"]
           [:button {:type "button"
                     :class "btn btn-primary"
                     :style {:margin-left "5px"}
                     :on-click #(do
                                  (.log js/console (pr-str @val))
                                  (POST (slots-url)
                                    {:params
                                     {:start (-> @val :fromTime)
                                      :end (-> @val :toTime)}
                                     :format :json
                                     :handler (fn [resp] (do
                                                           (reset! val {:fromTime nil :toTime nil})
                                                           (get-slots :refresh true)))}))}
            "Create a new slot"]]]]))))

;	$('input[name="fromTime"]').timepicker({});
;    $('input[name="toTime"]').timepicker({});
