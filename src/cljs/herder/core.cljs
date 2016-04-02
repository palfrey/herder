(ns herder.core
  (:refer-clojure :exclude [set])
  (:require
   [reagent.core :as r]
   [clojure.string :as str]
   [herder.conventions]
   [herder.slots]
   [herder.persons]
   [herder.events]
   [herder.event]
   [herder.schedule]
   [herder.helpers :refer [state history]]
   [secretary.core :as secretary :refer-macros [defroute]]
   [goog.events :as events]
   [goog.history.EventType :as HistoryEventType]
   [system.components.sente :refer [new-channel-socket-client]]
   [com.stuartsierra.component :as component]))

(defn convention-spa-routing []
  (secretary/set-config! :prefix "#")

  (defroute "/" []
    (swap! state assoc :component "herder.slots.component"))

  (defroute "/persons" []
    (swap! state assoc :component "herder.persons.component"))

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

(defn parse-ws [{[kind data] :event send :send-fn :as stuff}]
  (js/console.log "event" (pr-str kind) (pr-str data))
  (if (and (= kind :chsk/state) (:first-open? data))
    (send
     [::page (select-keys @state [:component :id])])))

(defn ^:export mount-root []
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
