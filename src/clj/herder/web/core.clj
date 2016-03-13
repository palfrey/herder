(ns herder.web.core
  (:gen-class)
  (:require 
   [reloaded.repl :refer [system init start stop go reset]]
   [herder.web.systems :refer [prod-system]]))

(defn -main
  "Start a production system."
  [& args]
  (reloaded.repl/set-init! prod-system)
  (go))
