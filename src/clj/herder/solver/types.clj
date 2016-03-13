(ns herder.solver.types
  (:require
   [clj-time.periodic :as p]
   [clj-time.core :as t])
  (:import
   [org.optaplanner.core.api.domain.entity PlanningEntity]
   [org.optaplanner.core.api.domain.variable PlanningVariable]
   [org.optaplanner.core.api.score.buildin.hardsoft HardSoftScore]
   [org.optaplanner.core.api.domain.solution PlanningSolution PlanningEntityCollectionProperty]
   [org.optaplanner.core.api.domain.valuerange ValueRangeProvider]
   [org.joda.time ReadablePeriod DateTime Interval]
   [java.util ArrayList]
   [herder.solver HardSoftSolution]))

(defn- getValue [this k]
  (let [state (.state this)] (.get @state k)))

(defn- setValue [this k v]
  (let [state (.state this)]
    (dosync (alter state assoc k v)) v))

(load "solution")
(load "event")
(load "slot")
(load "person")