(ns herder.solver.solution
  (:require
   [herder.solver.helpers :refer [getValue setValue]]
   [clj-time.periodic :as p]
   [clj-time.core :as t])
  (:import
   [org.optaplanner.core.api.domain.solution PlanningSolution PlanningEntityCollectionProperty]
   [org.optaplanner.core.api.domain.solution.cloner PlanningCloneable]
   [org.optaplanner.core.api.domain.valuerange ValueRangeProvider]
   [java.util ArrayList]))

; Double gen-class for self-reference http://stackoverflow.com/a/29375133/320546
(gen-class
 :name ^{PlanningSolution {}} herder.solver.solution.HerderSolution
 :extends herder.solver.HardSoftSolution
 :implements [org.optaplanner.core.api.domain.solution.cloner.PlanningCloneable]
 :init init
 :constructors {[] []
                [clojure.lang.PersistentArrayMap] []})

(gen-class
 :name ^{PlanningSolution {}} herder.solver.solution.HerderSolution
 :extends herder.solver.HardSoftSolution
 :implements [org.optaplanner.core.api.domain.solution.cloner.PlanningCloneable]
 :init init
 :state state
 :constructors {[] []
                [clojure.lang.PersistentArrayMap] []}
 :methods [[^{PlanningEntityCollectionProperty {}} getEvents [] java.util.List]
           [setEvents [java.util.List] void]
           [^{ValueRangeProvider {"id" "slotRange"}} getSlotRange [] java.util.List]
           [setFirstDay [org.joda.time.DateTime] void]
           [setLastDay [org.joda.time.DateTime] void]
           [getSlots [] java.util.List]
           [setSlots [java.util.List] void]]
 :prefix "solution-")

(defn- solution-init
  ([] (solution-init {:events (ArrayList.)}))
  ([state] [[] (ref state)]))

(defn- solution-getEvents [this]
  (ArrayList. (getValue this :events)))

(defn- solution-setEvents [this events]
  (setValue this :events events))

(defn- solution-getProblemFacts [this]
  (ArrayList.))

(defn- solution-getScore [this]
  (getValue this :score))

(defn- solution-setScore [this score]
  (setValue this :score score))

(defn- solution-getSlotRange [this]
  (let [firstDay (getValue this :firstDay)
        lastDay (getValue this :lastDay)]
    (if (nil? firstDay)
      []
      (apply concat (for [day (p/periodic-seq firstDay (t/days 1))
                          :while (or (t/equal? lastDay day) (t/after? lastDay day))]
                      (map #(.getSlotsForDay % day) (getValue this :slots)))))))

(defn- solution-getFirstDay [this]
  (getValue this :firstDay))

(defn- solution-setFirstDay [this value]
  (setValue this :firstDay value))

(defn- solution-setLastDay [this value]
  (setValue this :lastDay value))

(defn- solution-getSlots [this]
  (getValue this :slots))

(defn- solution-setSlots [this value]
  (setValue this :slots value))

(defn- solution-planningClone [this]
  (let [events (map #(.clone %) (solution-getEvents this))]
    (doseq [event events] (.fixChainedEventRef event events))
    (doto
     (herder.solver.solution.HerderSolution. (deref (.state this)))
      (solution-setEvents events))))
