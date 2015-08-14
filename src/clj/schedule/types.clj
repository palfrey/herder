(ns schedule.types
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
   [schedule HardSoftSolution]))

(defn- getValue [this k]
  (let [state (.state this)] (.get @state k)))

(defn- setValue [this k v]
  (let [state (.state this)]
    (dosync (alter state assoc k v)) v))

(gen-class 
 :name ^{PlanningEntity {}} schedule.types.Event
 :prefix "event-"
 :init init
 :state state
 :methods [[^{PlanningVariable {"valueRangeProviderRefs" ["slotRange"]}} getSlot [] Object]
           [setSlot [Object] void]
           [getId [] java.util.UUID]])

(defn- event-init []
  [[] (ref {:id (java.util.UUID/randomUUID)})])

(defn- event-getId [this]
  (getValue this :id))

(defn- event-getSlot [this]
  (getValue this :slot))

(defn- event-setSlot [this item]
  (setValue this :slot item))

(gen-class
 :name schedule.types.Slot
 :prefix "slot-"
 :init init
 :state state
 :extends java.lang.Object
 :constructors {[java.lang.Integer org.joda.time.ReadablePeriod] []}
 :methods [[getSlotsForDay [org.joda.time.DateTime] org.joda.time.Interval]])

(defn- slot-init [start length]
  [[] (ref {:start start :length length})])

(defn- slot-getSlotsForDay [this day]
  (let [beginSlot (t/plus day (t/hours (getValue this :start)))
        endSlot (t/plus beginSlot (getValue this :length))]
    (t/interval beginSlot endSlot)))

(load "solution")