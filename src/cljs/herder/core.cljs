(ns herder.core
  (:require [reagent.core :as r]))

(defn hello-component []
  [:div {:class "container-fluid"}
   [:p "Hello, names!"]])

(defn ^:export run []
  (r/render [hello-component]
            (js/document.getElementById "app")))
