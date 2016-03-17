(ns herder.core
  (:require [reagent.core :as r]))

(defn hello-component []
  [:p "Hello, name!"])

(defn ^:export run []
  (r/render [hello-component]
            (js/document.getElementById "app")))
