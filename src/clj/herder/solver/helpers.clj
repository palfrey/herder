(ns herder.solver.helpers)

(gen-class
 :name herder.solver.helpers.EventDifficultyComparator
 :extends herder.solver.ObjectComparator
 :prefix "eventDifficulty-")

(defn- eventDifficulty-compare [this a b]
  (->
   (Integer. (.getEventDay a))
   (.compareTo (Integer. (.getEventDay b)))))
