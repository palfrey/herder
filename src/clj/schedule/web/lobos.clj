(ns schedule.web.lobos
  (:refer-clojure :exclude [alter drop
                            bigint boolean char double float time])
  (:use (lobos core schema)))

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

(defn add-conventions-table []
  (create
   (tbl :conventions
        (varchar :name 100)
        (date :from)
        (date :to)
        (check :name (> (length :name) 1)))))

(defn add-slots-table []
  (create
   (tbl :slots
        (integer :start-minutes)
        (integer :end-minutes)
        (refer-to :conventions))))

(defn make-tables []
  (add-conventions-table)
  (add-slots-table))
