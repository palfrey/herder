(in-ns 'schedule.solver.types)

(gen-class
 :name schedule.solver.types.Slot
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