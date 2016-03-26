(ns herder.persons
  (:require
   [herder.helpers :refer [get-data state convention-url convention-header]]
   [ajax.core :refer [POST DELETE]]
   [reagent.core :as r]))

(defn persons-url []
  (str (convention-url) "/person"))

(defn get-persons [& {:keys [refresh]}]
  (get-data :persons (persons-url) :refresh refresh))

(defn ^:export component []
  (let [val (r/atom {})]
    (fn []
      [:div {:class "container-fluid"}
       [convention-header :persons]
       [:h2 "People"]
       [:ul
        (for [{:keys [id name]} (get-persons)]
          ^{:key id} [:li name " "
                      [:button {:type "button"
                                :class "btn btn-danger"
                                :on-click #(DELETE (str (persons-url) "/" id)
                                             {:handler
                                              (fn [resp] (get-persons :refresh true))})}
                       (str "Delete " name)]])]
       [:hr]
       [:form {:class "form-inline"}
        [:div {:class "form-group"}
         [:label {:for "name"} "Add Person with name"]
         [:input {:id "name"
                  :name "name"
                  :type "text"
                  :placeholder "Name"
                  :class "form-control input-md"
                  :value (:name @val)
                  :on-change #(swap! val assoc :name (-> % .-target .-value))}]
         [:button {:type "button"
                   :class "btn btn-primary"
                   :style {:margin-left "5px"}
                   :on-click #(do
                                (.log js/console (pr-str @val))
                                (POST (persons-url)
                                  {:params @val
                                   :format :json
                                   :handler (fn [resp] (do
                                                         (reset! val {:fromTime nil :toTime nil})
                                                         (get-persons :refresh true)))}))}
          "Create a new person"]]]])))
