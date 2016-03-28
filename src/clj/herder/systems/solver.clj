(ns herder.systems.solver
  (:require
   [com.stuartsierra.component :as component]
   )
  (:import [java.util.concurrent Executors]))

(defrecord Solver []
  component/Lifecycle
  (start [component]
    (let [pool (Executors/newFixedThreadPool 1)]
      (assoc component :pool pool)))
  (stop [component]
    (.shutdownNow (:pool component))
    (assoc component :pool nil)))

(defn new-solver []
  (Solver.))
