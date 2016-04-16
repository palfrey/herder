(in-ns 'herder.solver.types)

(gen-class
 :name ^{PlanningEntity {}} herder.solver.types.Event
 :prefix "event-"
 :init init
 :state state
 :constructors {[] []
                [java.util.UUID] []}
 :methods [[^{PlanningVariable {"valueRangeProviderRefs" ["slotRange"] "nullable" true}} getSlot [] Object]
           [setSlot [Object] void]
           [getId [] java.util.UUID]
           [getPeople [] java.util.List]
           [setPeople [java.util.List] void]
           [getName [] String]
           [setName [String] void]])

(defn- event-init
  ([] (event-init (java.util.UUID/randomUUID)))
  ([uuid] [[] (ref {:id uuid
                    :people []
                    :name ""})]))

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

(defn- event-getName [this]
  (getValue this :name))

(defn- event-setName [this item]
  (setValue this :name item))
