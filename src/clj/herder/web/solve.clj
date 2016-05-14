(ns herder.web.solve
  (:require
    [herder.uuid :refer [to-uuid]]))

(defn solve [conv_id]
  (require '(herder.solver run))
  (let [f (ns-resolve 'herder.solver.run 'needs-solve)]
    (f (to-uuid conv_id))))
