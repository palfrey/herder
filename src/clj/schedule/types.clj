(ns schedule.types
  (:require
   [clj-time.periodic :as p]
   [clj-time.core :as t])
  (:import
   [org.optaplanner.core.api.domain.solution PlanningSolution Solution]
   [org.optaplanner.core.api.domain.solution PlanningEntityCollectionProperty]
   [org.optaplanner.core.api.domain.entity PlanningEntity]
   [org.optaplanner.core.api.domain.variable PlanningVariable]
   [org.optaplanner.core.api.domain.valuerange ValueRangeProvider]
   [org.optaplanner.core.api.score.buildin.hardsoft HardSoftScore]

   [schedule HardSoftSolution]

   [java.util ArrayList]
   [org.joda.time ReadablePeriod DateTime Interval]))

(defn- getValue [this k]
  (let [state (.state this)] (.get @state k)))

(defn- setValue [this k v]
  (let [state (.state this)]
    (dosync (alter state assoc k v)) v))

(gen-class 
 :name ^{PlanningSolution {}} schedule.types.ScheduleSolution
 :extends schedule.HardSoftSolution
 :init init
 :state state
 :methods [[^{PlanningEntityCollectionProperty {}} getEvents [] java.util.List]
           [setEvents [java.util.List] void]
           [^{ValueRangeProvider {"id" "slotRange"}} getSlotRange [] java.util.List]
           [setFirstDay [org.joda.time.DateTime] void]
           [setLastDay [org.joda.time.DateTime] void]
           [getSlots [] java.util.List]
           [setSlots [java.util.List] void]]
 :prefix "solution-")

(defn- solution-init []
  [[] (ref {:events (ArrayList.)})])

(defn- solution-getEvents [this]
  (ArrayList. (getValue this :events)))

(defn- solution-getProblemFacts [this]
  (getValue this :events))

(defn- solution-getScore [this]
  (getValue this :score))

(defn- solution-setScore [this score]
  (setValue this :score score))

(defn- solution-getSlotRange [this]
  (let [firstDay (getValue this :firstDay)
        lastDay (getValue this :lastDay)]
    (apply concat (for [day (p/periodic-seq firstDay (t/days 1))
                        :while (or (t/equal? lastDay day) (t/after? lastDay day))]
                    (map #(.getSlotsForDay % day) (getValue this :slots))))))

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

(gen-class 
 :name ^{PlanningEntity {}} schedule.types.Event
 :prefix "event-"
 :init init
 :state state
 :methods [[^{PlanningVariable {"valueRangeProviderRefs" ["slotRange"]}} getSlot [] Object]
           [setSlot [Object] void]])

(defn- event-init []
  [[] (ref {})])

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