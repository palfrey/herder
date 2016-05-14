(ns herder.uuid
  (:import
    [java.util UUID]))

(defn to-uuid [input]
  (cond
    (instance? UUID input) input
    (nil? input) input
    :else (UUID/fromString input)))
