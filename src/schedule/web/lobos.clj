(ns schedule.web.lobos
  (:refer-clojure :exclude [alter drop
                            bigint boolean char double float time])
  (:use (lobos core schema)))

  ;; Define a UUID column type (for H2)
(defn uuid [table name & options]
  (apply column table name (data-type :uuid) options))

(defn add-conventions-table []
  (create
   (table :conventions
          (uuid :id :primary-key)
          (varchar :name 100)
          (date :from)
          (date :to)
          (check :name (> (length :name) 1)))))
