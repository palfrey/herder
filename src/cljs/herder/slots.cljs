(ns herder.slots
  (:require
   [herder.helpers :refer [get-data state convention-url convention-header]]
   [ajax.core :refer [POST DELETE]]
   [reagent.core :as r]
   [cljsjs.jquery-timepicker]))

(defn slots-url []
  (str (convention-url) "/slot"))

(defn get-slots [& {:keys [refresh]}]
  (get-data [(:id @state) :slots] (slots-url) :refresh refresh))

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
                           (.timepicker (js/$ (r/dom-node %)) (js-obj "timeFormat" "H:i"))
                           (.bind "changeTime"
                                  (fn [event obj]
                                    (swap! val assoc key
                                           (-> event .-target .-value)))))}))

(defn ^:export component []
  (let [val (r/atom {:fromTime nil :toTime nil})]
    (fn []
      [:div {:class "container-fluid"}
       [convention-header :slots]
       [:h2 "Slots"]
       [:ul
        (for [{:keys [id start end]} (get-slots)]
          ^{:key id} [:li start " to " end " "
                      [:button {:type "button"
                                :class "btn btn-danger"
                                :on-click #(DELETE (str (slots-url) "/" id))}
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
                                   :handler (fn [resp]
                                              (reset! val {:fromTime nil :toTime nil}))}))}
          "Create a new slot"]]]])))
