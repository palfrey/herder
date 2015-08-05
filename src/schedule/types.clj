(ns schedule.types
	(:import
		[org.optaplanner.core.api.domain.solution PlanningSolution]
		[org.optaplanner.core.api.domain.solution PlanningEntityCollectionProperty]
	    [org.optaplanner.core.api.domain.entity PlanningEntity]
	    [org.optaplanner.core.api.domain.variable PlanningVariable]
	    [org.optaplanner.core.api.domain.valuerange ValueRangeProvider]
		[java.util ArrayList]
	))

(gen-class 
	:name ^{PlanningSolution {}} schedule.types.Solution
	:methods [
		[^{PlanningEntityCollectionProperty {}} getTestItems [] java.util.ArrayList]
		[^{ValueRangeProvider {"id" "slotRange"}} getSlotRange [] java.util.ArrayList]
	]
	:prefix "solution-")

(defn solution-getTestItems [this]
	(ArrayList.))

(gen-class 
	:name ^{PlanningEntity {}} schedule.types.Event
	:prefix "event-"
	:methods [
		[^{PlanningVariable {"valueRangeProviderRefs" ["slotRange"]}} getSlot [] Object]
		[setSlot [Object] void]
	]
)

(defn event-getSlot [this]
	nil)