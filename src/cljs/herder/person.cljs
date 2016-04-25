(ns herder.person
  (:require
   [herder.helpers :refer [convention-header nav!]]
   [herder.getter :refer [get-data person-url]]
   [herder.state :refer [state]]
   [reagent.core :as r]
   [ajax.core :refer [PATCH DELETE]]))

(defn set-person-name [val]
  (js/console.log (pr-str val))
  (PATCH (person-url (:id @state) (:person_id @state))
    {:params {:name val}
     :format :json}))

(defn ^:export component []
  (let [person (get-data [:person (:id @state) (:person_id @state)])]
    [:div {:class "container-fluid"}
     [convention-header :persons]
     [:h2 "Person"]
     [:label {:for "name"} "Name "]
     [:input {:id "name"
              :type "text"
              :value (:name person)
              :on-change #(set-person-name (-> % .-target .-value))}]
     [:hr]
     [:h4 "Attending"]
     [:ul
      (for [event_id (:events person)
            :let [event (get-data [:event (:id @state) event_id])]]
        ^{:key event_id}
        [:li [:a {:href (str "#/events/" event_id)} (:name event)]])]
     [:hr]
     [:button {:type "button"
               :class "btn btn-danger"
               :on-click #(DELETE (person-url (:id @state) (:person_id @state))
                            {:handler
                             (fn [resp]
                               (nav! "/persons"))})}
      "Delete this person"]]))
