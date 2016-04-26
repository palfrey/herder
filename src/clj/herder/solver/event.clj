(ns herder.solver.types
    (:use [herder.solver.helpers])
    (:import [herder.solver.helpers EventDifficultyComparator]))

(gen-class
 :name ^{PlanningEntity {"difficultyComparatorClass" EventDifficultyComparator}} herder.solver.types.Event
 :prefix "event-"
 :init init
 :state state
 :constructors {[] []
                [java.util.UUID] []}
 :methods [[^{PlanningVariable {"valueRangeProviderRefs" ["slotRange"] "nullable" true}} getSlot [] Object]
           [setSlot [Object] void]
           [getPreferredSlots [] java.util.List]
           [setPreferredSlots [java.util.List] void]
           [getChainedEvent [] Object]
           [setChainedEvent [Object] void]
           [getEventDay [] int]
           [setEventDay [int] void]
           [getId [] java.util.UUID]
           [getExternalId [] java.util.UUID]
           [getPeople [] java.util.List]
           [setPeople [java.util.List] void]
           [getName [] String]
           [setName [String] void]])

(defn- event-init
  ([] (event-init (java.util.UUID/randomUUID)))
  ([uuid] [[] (ref {:id (java.util.UUID/randomUUID)
                    :external-id uuid
                    :people []
                    :name ""
                    :preferred-slots []})]))

(defn- event-getId [this]
  (getValue this :id))

(defn- event-getExternalId [this]
  (getValue this :external-id))

(defn- event-getSlot [this]
  (getValue this :slot))

(defn- event-setSlot [this item]
  (setValue this :slot item))

(defn- event-getPreferredSlots [this]
  (getValue this :preferred-slots))

(defn- event-setPreferredSlots [this item]
  (setValue this :preferred-slots item))

(defn- event-getChainedEvent [this]
  (getValue this :chained-event))

(defn- event-setChainedEvent [this item]
  (setValue this :chained-event item))

(defn- event-getEventDay [this]
  (getValue this :event-day))

(defn- event-setEventDay [this item]
  (setValue this :event-day item))

(defn- event-getPeople [this]
  (getValue this :people))

(defn- event-setPeople [this item]
  (setValue this :people item))

(defn- event-getName [this]
  (getValue this :name))

(defn- event-setName [this item]
  (setValue this :name item))
