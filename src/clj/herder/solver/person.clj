(ns herder.solver.person
	(:require
		[herder.solver.helpers :refer [getValue setValue]]))

(gen-class
 :name ^{PlanningEntity {}} herder.solver.person.Person
 :prefix "person-"
 :init init
 :state state
 :constructors {[] []
                [java.util.UUID] []}
 :methods [[getId [] java.util.UUID]
           [getName [] String]
           [setName [String] void]])

(defn- person-init
  ([] (person-init (java.util.UUID/randomUUID)))
  ([uuid] [[] (ref {:id uuid
                    :name ""})]))

(defn- person-getId [this]
  (getValue this :id))

(defn- person-getName [this]
  (getValue this :name))

(defn- person-setName [this item]
  (setValue this :name item))

(defn- person-equals [this b]
  (= (person-getId this) (person-getId b)))
(defn- person-hashCode [this]
  (.hashCode (person-getId this)))
