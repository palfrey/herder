(ns herder.core
  (:refer-clojure :exclude [set])
  (:require
   [reagent.core :as r]
   [clojure.string :as str]
   [herder.conventions]
   [herder.convention]
   [herder.slots]
   [herder.persons]
   [herder.person]
   [herder.events]
   [herder.event]
   [herder.schedule]
   [herder.state :refer [state]]
   [herder.helpers :refer [history]]
   [herder.getter :refer [parse-ws]]
   [secretary.core :as secretary :refer-macros [defroute]]
   [goog.events :as events]
   [goog.history.EventType :as HistoryEventType]
   [system.components.sente :refer [new-channel-socket-client]]
   [com.stuartsierra.component :as component]))

(defn convention-spa-routing []
  (secretary/set-config! :prefix "#")

  (defroute "/" []
    (swap! state assoc :component "herder.convention.component"))

  (defroute "/slots" []
    (swap! state assoc :component "herder.slots.component"))

  (defroute "/persons" []
    (swap! state assoc :component "herder.persons.component"))

  (defroute "/person/:id" [id]
    (swap! state assoc :component "herder.person.component" :person_id id))

  (defroute "/events" []
    (swap! state assoc :component "herder.events.component"))

  (defroute "/events/:id" [id]
    (swap! state assoc :component "herder.event.component" :event_id id))

  (defroute "/schedule" []
    (swap! state assoc :component "herder.schedule.component")))

(defn hook-browser-navigation! []
  (doto history
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

(defonce channel-sockets (atom nil))

(defn ^:export mount-root []
  (if (-> @channel-sockets nil? not)
    (swap! channel-sockets component/stop))
  (reset! channel-sockets (new-channel-socket-client parse-ws "/chsk"))
  (swap! channel-sockets component/start)
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
