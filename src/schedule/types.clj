(ns schedule.types
	(:import
		[org.optaplanner.core.api.domain.solution PlanningSolution]
		[org.optaplanner.core.api.domain.solution PlanningEntityCollectionProperty]
	))

(gen-class 
	:name ^{PlanningSolution {}} schedule.types.Solution
	:methods [
		[getTestItems [] Object]
	])

(defn ^{PlanningEntityCollectionProperty {}}
		-getTestItems [this]
		nil)
