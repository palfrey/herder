(ns herder.core
  (:require
   [reagent.core :as r]
   [clojure.string :as str]
   [herder.conventions]))

(defn set-html! [dom content]
  (set! (. dom -innerHTML) content))

(defn ->js [var-name]
  (-> var-name
      (str/replace "/" ".")
      (str/replace "-" "_")))

(defn ^:export run [component title-text]
  (let [to-render (js/eval (->js component))]
    (if (nil? to-render) (throw (js/Error. (str "Can't find " component))))
    (r/render [to-render]
              (js/document.getElementById "app")))
  (let [title (.item (js/document.getElementsByTagName "title") 0)]
    (set-html! title title-text)))
