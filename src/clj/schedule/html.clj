(ns schedule.html
  (:use [clostache.parser]))

(defn index []
  (render-resource "templates/index.mustache"))