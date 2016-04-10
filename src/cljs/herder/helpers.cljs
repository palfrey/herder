(ns herder.helpers
  (:require
   [herder.state :refer [state]]
   [herder.getter :refer [get-data]])
  (:import goog.History))

(defonce history
  (History.))

(defn nav! [token]
  (.setToken history token))

(defn to-date [tstamp]
  (-> tstamp js/moment (.format "YYYY-MM-DD")))

(defn convention-header [active]
  (let [convention (get-data [:convention (:id @state)])]
    [:nav {:class "navbar navbar-nav navbar-full navbar-dark bg-inverse"}
     [:a {:class "navbar-brand"} (:name convention) " (" (to-date (:from convention)) " - " (to-date (:to convention)) ")"]
     [:ul {:class "nav navbar-nav"}
      [:li {:class (str "nav-item " (if (= active :slots) "active" ""))}
       [:a {:class "nav-link" :href "#/"} "Slots"]]
      [:li {:class (str "nav-item " (if (= active :persons) "active" ""))}
       [:a {:class "nav-link" :href "#/persons"} "People"]]
      [:li {:class (str "nav-item " (if (= active :events) "active" ""))}
       [:a {:class "nav-link" :href "#/events"} "Events"]]
      [:li {:class (str "nav-item " (if (= active :schedule) "active" ""))}
       [:a {:class "nav-link" :href "#/schedule"} [:i "Schedule"]]]
      [:li {:class "nav-item"}
       [:a {:class "nav-link" :href "/"} "Goto convention list"]]]]))
