(ns herder.convention
  (:require
   [reagent.core :as r]
   [herder.helpers :refer [convention-header nav!]]
   [herder.getter :refer [get-data convention-url]]
   [herder.state :refer [state]]
   [herder.conventions :refer [daterange]]
   [ajax.core :refer [PATCH DELETE]]))

(defn set-convention-value [val]
  (js/console.log (pr-str val))
  (PATCH (convention-url (:id @state))
    {:params val
     :format :json}))

(defn set-convention-name [val]
  (set-convention-value {:name val}))

(defn set-convention-date [from to]
  (set-convention-value {:from from :to to}))

(defn get-convention-dates []
  (let [convention (get-data [:convention (:id @state)])]
    [(-> (:from convention) js/moment .toDate)
     (-> (:to convention) js/moment .toDate)]))

(defn ^:export component []
  (let [convention (get-data [:convention (:id @state)])]
    [:div {:class "container-fluid"}
     [convention-header :convention]
     [:h2 "Convention"]
     [:label {:for "conventionName"} "Convention name"]
     [:input {:id "conventionName"
              :name "conventionName"
              :type "text"
              :placeholder "Convention name"
              :class "form-control input-md"
              :value (:name convention)
              :on-change #(set-convention-name (-> % .-target .-value))}]
     [:label {:for "daterange"} "Dates"]
     (if (not= convention {})
       [daterange get-convention-dates
        #(do
           (js/console.log "date" (pr-str %))
           (set-convention-date
            (-> % first js/moment (.format "YYYY-MM-DD"))
            (-> % second js/moment (.format "YYYY-MM-DD"))))])]))
