(ns herder.web.lobos
  (:refer-clojure :exclude [alter drop
                            bigint boolean char double float time])
  (:use (lobos core schema [migration :only [defmigration]])))

  ;; Define a UUID column type (for H2)
(defn uuid [table name & options]
  (apply column table name (data-type :uuid) options))

(defn surrogate-key [table]
  (uuid table :id :primary-key))

(defn refer-to [table ptable]
  (let [cname (-> (->> ptable name butlast (apply str))
                  (str "_id")
                  keyword)]
    (uuid table cname [:refer ptable :id :on-delete :set-null])))

(defmacro tbl [name & elements]
  `(-> (table ~name
              (surrogate-key))
       ~@elements))

(defmigration add-conventions-table
  (up
   (create
    (tbl :conventions
         (varchar :name 100)
         (date :from)
         (date :to)
         (check :name (> (length :name) 1)))))
  (down
   (drop (table :conventions))))

(defmigration add-slots-table
  (up
   (create
    (tbl :slots
         (integer :start-minutes)
         (integer :end-minutes)
         (refer-to :conventions))))
  (down
   (drop (table :slots))))

(defmigration add-persons-table
  (up
   (create
    (tbl :persons
         (varchar :name 100)
         (refer-to :conventions))))
  (down
   (drop (table :persons))))

(defmigration add-events-table
  (up
   (create
    (tbl :events
         (varchar :name 100)
         (refer-to :conventions)))
   (create
    (table :events-persons
           (refer-to :events)
           (refer-to :persons))))
  (down
   (drop (table :events))
   (drop (table :events-persons))))

(defn make-tables []
  (add-conventions-table)
  (add-slots-table)
  (add-persons-table)
  (add-events-table))
