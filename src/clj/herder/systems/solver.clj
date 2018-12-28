(ns herder.systems.solver
  (:require
   [com.stuartsierra.component :as component])
  (:import [java.util.concurrent Executors]))

(defrecord Solver []
  component/Lifecycle
  (start [component]
    (let [pool (Executors/newFixedThreadPool 1)
          tosolve (agent #{})
          solving (atom false)
          watch (atom nil)]
      (assoc component
             :pool pool
             :tosolve tosolve
             :solving solving
             :watch watch)))
  (stop [component]
    (if (-> component :pool nil? not)
      (.shutdownNow (:pool component)))
    (if (and (-> component :watch nil? not) (-> component :watch deref nil? not))
      (remove-watch (:tosolve component) (-> component :watch deref)))
    (dissoc component
            :pool
            :tosolve
            :solving
            :watch)))

(defn new-solver []
  (Solver.))
