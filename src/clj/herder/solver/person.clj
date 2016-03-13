(in-ns 'herder.solver.types)

(gen-class
 :name ^{PlanningEntity {}} herder.solver.types.Person
 :prefix "person-"
 :init init
 :state state
 :methods [[getId [] java.util.UUID]])

(defn- person-init []
  [[] (ref {:id (java.util.UUID/randomUUID)})])

(defn- person-getId [this]
  (getValue this :id))