(ns herder.persons
  (:require
   [herder.helpers :refer [convention-header]]
   [herder.getter :refer [get-data person-url persons-url]]
   [herder.state :refer [state]]
   [ajax.core :refer [POST DELETE]]
   [reagent.core :as r]))

(defn create-new [val]
  (POST (persons-url (:id @state))
    {:params @val
     :format :json
     :handler
     (fn [resp]
       (reset! val {}))}))

(defn ^:export component []
  (let [val (r/atom {})]
    (fn []
      [:div {:class "container-fluid"}
       [convention-header :persons]
       [:h2 "People"]
       [:ul
        (for [{:keys [id name]} (get-data [:persons (:id @state)])]
          ^{:key id} [:li [:a {:href (str "#/person/" id)} name " "]
                      [:button {:type "button"
                                :class "btn btn-danger"
                                :on-click #(DELETE (person-url (:id @state) id))}
                       (str "Delete " name)]])]
       [:hr]
       [:form {:class "form-inline"
               :on-submit #(do
                             (.preventDefault %)
                             (create-new val)
                             false)}
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
                   :on-click #(create-new val)}
          "Create a new person"]]]])))
