(ns herder.helpers
  (:require
   [reagent.core :as r]
   [ajax.core :refer [GET]]
   [clojure.walk :refer [keywordize-keys]])
  (:import goog.History))

(defonce state (r/atom {}))

(defonce history
  (History.))

(defn nav! [token]
  (.setToken history token))

(defn to-date [tstamp]
  (-> tstamp js/moment (.format "YYYY-MM-DD")))

(defn key-handler [key data]
  (.log js/console (str key) "Response" (pr-str data))
  (swap! state assoc key (keywordize-keys data)))

(defn get-data [key url & {:keys [refresh] :or {refresh false}}]
  (if (and (not refresh) (contains? @state key))
    (key @state)
    (do
      (GET url {:handler (partial key-handler key)})
      {})))

(defn convention-url []
  (str "/api/convention/" (:id @state)))

(defn get-convention [& {:keys [refresh]}]
  (get-data :convention (convention-url) :refresh refresh))

(defn convention-header [active]
  (let [convention (get-convention)]
    [:nav {:class "navbar navbar-nav navbar-full navbar-dark bg-inverse"}
     [:a {:class "navbar-brand"} (:name convention) " (" (to-date (:from convention)) " - " (to-date (:to convention)) ")"]
     [:ul {:class "nav navbar-nav"}
      [:li {:class (str "nav-item " (if (= active :slots) "active" ""))}
       [:a {:class "nav-link" :href "#/"} "Slots"]]
      [:li {:class (str "nav-item " (if (= active :persons) "active" ""))}
       [:a {:class "nav-link" :href "#/persons"} "People"]]
      [:li {:class (str "nav-item " (if (= active :events) "active" ""))}
       [:a {:class "nav-link" :href "#/events"} "Events"]]
      [:li {:class "nav-item"}
       [:a {:class "nav-link" :href "/"} "Goto convention list"]]]]))
