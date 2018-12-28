(ns herder.core
  (:gen-class)
  (:require
   [system.repl :refer [start set-init!]]
   [herder.systems :refer [prod-system]]))

(defn -main
  "Start a production system."
  [& args]
  (set-init! #'prod-system)
  (start))
