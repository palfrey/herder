(ns schedule.types
  (:import
   [org.optaplanner.core.api.domain.solution PlanningSolution Solution]
   [org.optaplanner.core.api.domain.solution PlanningEntityCollectionProperty]
   [org.optaplanner.core.api.domain.entity PlanningEntity]
   [org.optaplanner.core.api.domain.variable PlanningVariable]
   [org.optaplanner.core.api.domain.valuerange ValueRangeProvider]
   [org.optaplanner.core.api.score.buildin.hardsoft HardSoftScore]
   [schedule HardSoftSolution]
   [java.util ArrayList]))

(gen-class 
 :name ^{PlanningSolution {}} schedule.types.ScheduleSolution
 :extends schedule.HardSoftSolution
 :methods [[^{PlanningEntityCollectionProperty {}} getTestItems [] java.util.ArrayList]
           [^{ValueRangeProvider {"id" "slotRange"}} getSlotRange [] java.util.ArrayList]]
 :prefix "solution-")

(defn solution-getTestItems [this]
  (ArrayList.))

(defn solution-getProblemFacts [this]
  (ArrayList.))

(defn solution-setScore [this score])

(defn solution-getSlotRange [this]
  (ArrayList.))

(defn solution-getScore	[this]
  (HardSoftScore/parseScore "0hard/0soft"))

(gen-class 
 :name ^{PlanningEntity {}} schedule.types.Event
 :prefix "event-"
 :methods [[^{PlanningVariable {"valueRangeProviderRefs" ["slotRange"]}} getSlot [] Object]
           [setSlot [Object] void]])

(defn event-getSlot [this]
  nil)