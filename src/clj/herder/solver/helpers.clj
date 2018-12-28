(ns herder.solver.helpers)

(defn getValue [this k]
  (let [state (.state this)]
    (get @state k)))

(defn setValue [this k v]
  (let [state (.state this)]
    (dosync (alter state assoc k v))
    v))
