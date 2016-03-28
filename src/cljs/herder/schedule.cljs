(ns herder.schedule
  (:require
   [herder.helpers :refer [get-data state convention-url convention-header]]
   [reagent.core :as r]))

(defn schedule-url []
  (str (convention-url) "/schedule"))

(defn get-schedule [& {:keys [refresh]}]
  (get-data :schedule (schedule-url) :refresh refresh))

(defn ^:export component []
  (let [schedule (get-schedule)]
    [:div {:class "container-fluid"}
     [convention-header :schedule]
     [:h2 "Schedule"]
     (into [:ul]
           (for [{:keys [id name]} schedule]
             ^{:key id} [:li name " "]))]))
