(ns schedule.html
  (:use [clostache.parser]))

(defn index [db]
  (render-resource "templates/index.mustache"
                   {:conventions (get db :conventions)}))