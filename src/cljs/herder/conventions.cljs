(ns herder.conventions
  (:require
   [reagent.core :as r]
   [cljsjs.jquery]
   [cljsjs.moment]
   [cljsjs.jquery-daterange-picker]
   [herder.helpers :as h]))

(defn get-convention [& {:keys [refresh]}]
  (h/get-data :conventions "/api/convention" :refresh refresh))

(defn daterange [val key]
  (r/create-class
   {:reagent-render
    (fn []
      [:input {:type "text"
               :name "daterange"
               :class "form-control"
               :value
               (let [value (key @val)]
                 (if (nil? value)
                   ""
                   (str (-> value first .toDateString) " to " (-> value second .toDateString))))}])
    :component-did-mount #(->
                           (.dateRangePicker (js/$ (r/dom-node %)))
                           (.bind "datepicker-change"
                                  (fn [event obj]
                                    (swap! val assoc key
                                           [(-> obj .-date1) (-> obj .-date2)]))))}))

(defn ^:export conventions-component []
  (let [val (r/atom {:name "" :date nil})
        df (fn [date] (-> date js/moment (.format "YYYY-MM-DD")))]
    (fn []
      [:div {:class "container-fluid"}
       [:h1 "Conventions"]
       [:ul
        (for [{:keys [id name from to]} (get-convention)]
          ^{:key id} [:li
                      [:a {:href (str "/convention/" id)} name]
                      (str " " (df from) " - " (df to) " ")
                      [:button {:type "button"
                                :class "btn btn-danger"
                                :on-click #(DELETE (str "/api/convention/" id)
                                             {:handler
                                              (fn [resp] (get-convention :refresh true))})}
                       (str "Delete " name)]])]
       [:hr]

       [:div {:class "form-inline"}
        [:div {:class "form-group"}
         [:label {:for "conventionName"
                  :style {:padding-left "5px"
                          :padding-right "5px"}} "Convention name"]
         [:input {:id "conventionName"
                  :name "conventionName"
                  :type "text"
                  :placeholder "Convention name"
                  :class "form-control input-md"
                  :value (:name @val)
                  :on-change #(swap! val assoc :name (-> % .-target .-value))}]]

        [:div {:class "form-group"}
         [:label {:for "daterange"
                  :style {:padding-left "5px"
                          :padding-right "5px"}} "Dates"]
         [daterange val :date]]
        [:button {:type "button"
                  :class "btn btn-primary"
                  :style {:margin-left "5px"}

                  :on-click #(do
                               (.log js/console (pr-str @val))
                               (POST "/api/convention"
                                 {:params
                                  {:conventionName (:name @val)
                                   :from (-> @val :date first js/moment (.format "YYYY-MM-DD"))
                                   :to (-> @val :date second js/moment (.format "YYYY-MM-DD"))}
                                  :format :json
                                  :handler (fn [resp] (do
                                                        (reset! val {:name "" :date nil})
                                                        (get-convention :refresh true)))}))}
         "Create a new convention"]]])))
