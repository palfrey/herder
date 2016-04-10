(ns herder.web.solve)

(defn solve [conv_id]
  (require '(herder.solver run))
  (let [f (ns-resolve 'herder.solver.run 'needs-solve)]
    (f conv_id)))
