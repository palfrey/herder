(in-ns 'schedule.types)

(gen-class
 :name ^{PlanningEntity {}} schedule.types.Person
 :prefix "person-"
 :init init
 :state state
 :methods [[getId [] java.util.UUID]])

(defn- person-init []
  [[] (ref {:id (java.util.UUID/randomUUID)})])

(defn- person-getId [this]
  (getValue this :id))