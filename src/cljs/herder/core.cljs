(ns herder.core
  (:refer-clojure :exclude [set])
  (:require
   [reagent.core :as r]
   [clojure.string :as str]
   [herder.conventions]
   [herder.slots]
   [herder.persons]
   [herder.helpers :refer [state]]))

(defn set-html! [dom content]
  (set! (. dom -innerHTML) content))

(defn ->js [var-name]
  (-> var-name
      (str/replace "/" ".")
      (str/replace "-" "_")))

(defn ^:export set [key value]
  (swap! state assoc (keyword key) value))

(defn ^:export run []
  (.log js/console (pr-str @state))
  (let [component (:component @state)
        to-render (js/eval (->js component))]
    (if (nil? to-render) (throw (js/Error. (str "Can't find " component))))
    (r/render [to-render]
              (js/document.getElementById "app")))
  (let [title (.item (js/document.getElementsByTagName "title") 0)]
    (set-html! title (:title @state))))
