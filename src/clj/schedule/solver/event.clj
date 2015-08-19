(in-ns 'schedule.solver.types)

(gen-class 
 :name ^{PlanningEntity {}} schedule.solver.types.Event
 :prefix "event-"
 :init init
 :state state
 :methods [[^{PlanningVariable {"valueRangeProviderRefs" ["slotRange"]}} getSlot [] Object]
           [setSlot [Object] void]
           [getId [] java.util.UUID]
           [getPeople [] java.util.List]
           [setPeople [java.util.List] void]])

(defn- event-init []
  [[] (ref {:id (java.util.UUID/randomUUID) :people []})])

(defn- event-getId [this]
  (getValue this :id))

(defn- event-getSlot [this]
  (getValue this :slot))

(defn- event-setSlot [this item]
  (setValue this :slot item))

(defn- event-getPeople [this]
  (getValue this :people))

(defn- event-setPeople [this item]
  (setValue this :people item))