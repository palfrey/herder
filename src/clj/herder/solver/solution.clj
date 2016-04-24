(ns herder.solver.types)

(gen-class
 :name ^{PlanningSolution {}} herder.solver.types.HerderSolution
 :extends herder.solver.HardSoftSolution
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

(defn- solution-setEvents [this events]
  (setValue this :events events))

(defn- solution-getProblemFacts [this]
  (getValue this :events))

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
