(in-ns 'herder.solver.types)

(gen-class
 :name ^{PlanningEntity {}} herder.solver.types.Person
 :prefix "person-"
 :init init
 :state state
 :methods [[getId [] java.util.UUID]])

(defn- person-init []
  [[] (ref {:id (java.util.UUID/randomUUID)
            :name ""})])

(defn- person-getId [this]
  (getValue this :id))

(defn- person-getName [this]
  (getValue this :name))

(defn- person-setName [this item]
  (setValue this :name item))
