(ns herder.core
  (:refer-clojure :exclude [set])
  (:require
   [reagent.core :as r]
   [clojure.string :as str]
   [herder.conventions]
   [herder.slots]
   [herder.persons]
   [herder.helpers :refer [state]]
   [secretary.core :as secretary :refer-macros [defroute]]
   [goog.events :as events]
   [goog.history.EventType :as HistoryEventType])
  (:import goog.History))

(defn convention-spa-routing []
  (secretary/set-config! :prefix "#")

  (defroute "/" []
    (swap! state assoc :component "herder.slots.component"))

  (defroute "/persons" []
    (swap! state assoc :component "herder.persons.component")))

(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
     HistoryEventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

(defn set-html! [dom content]
  (set! (. dom -innerHTML) content))

(defn ->js [var-name]
  (-> var-name
      (str/replace "/" ".")
      (str/replace "-" "_")))

(defn ^:export set [key value]
  (swap! state assoc (keyword key) value))

(defn page []
  [(-> (get @state :component "herder.slots.component") ->js js/eval)])

(defn mount-root []
  (.log js/console (pr-str @state))
  (r/render [#'page]
            (js/document.getElementById "app"))
  (let [title (.item (js/document.getElementsByTagName "title") 0)]
    (set-html! title (:title @state))))

(defn ^:export run []
  (if (contains? @state :id)
    (do
      (convention-spa-routing)
      (hook-browser-navigation!)))
  (mount-root))
