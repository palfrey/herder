(in-ns 'schedule.types)

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