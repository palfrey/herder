(ns herder.web.lobos
  (:refer-clojure :exclude [alter drop
                            bigint boolean char double float time])
  (:require [lobos.migration :refer [defmigration Migration] :as mig])
  (:use (lobos core schema)))

  ;; Define a UUID column type (for H2)
(defn uuid [table name & options]
  (apply column table name (data-type :uuid) options))

(defn surrogate-key [table]
  (uuid table :id :primary-key))

(defn refer-to-with-cname
  [table ptable cname & args]
  (let [cname (-> cname
                  (str "_id")
                  keyword)]
    (uuid table cname (into [:refer ptable :id] args))))

(defn refer-to [table ptable & args]
  (apply refer-to-with-cname table ptable (->> ptable name butlast (apply str)) args))

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
         (check :name (> (length :name) 0)))))
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
           (refer-to :conventions :on-delete :cascade)
           (refer-to :events :on-delete :cascade)
           (refer-to :persons :on-delete :cascade)
           (primary-key [:event_id :person_id]))))
  (down
   (drop (table :events))
   (drop (table :events-persons))))

(defmigration add-schedule-table
  (up
   (create
    (tbl :schedule
         (date :date)
         (refer-to :conventions :on-delete :cascade)
         (refer-to :slots :on-delete :cascade)
         (refer-to :events :on-delete :cascade))))
  (down
   (drop (table :schedule))))

(defmigration add-schedule-issues-table
  (up
   (create
    (tbl :schedule-issues
         (varchar :issue 256)
         (integer :score)
         (integer :level)
         (refer-to :conventions :on-delete :cascade)))
   (create
    (tbl :schedule-issues-events
         (refer-to :conventions :on-delete :cascade)
         (refer-to :schedule-issues :on-delete :cascade)
         (refer-to :events :on-delete :cascade))))
  (down
   (drop (table :schedule-issues))
   (drop (table :schedule-issues-events))))

(defmigration add-preferred-slot-to-event
  (up
   (alter :add
          (table :events
                 (refer-to-with-cname :slots "preferred_slot" :on-delete :set-null))))
  (down))

(defmigration add-event-count-to-event
  (up
   (alter :add
          (table :events
                 (integer :event_count (default 1)))))
  (down))

(defmigration add-event-day-to-schedule
  (up
   (alter :add
          (table :schedule
                 (integer :event_day (default 1)))))
  (down))

; Persons are assumed to be available unless otherwise specified
(defmigration person-non-availability
  (up
   (create
    (table :person-non-availability
           (date :date)
           (refer-to :persons :on-delete :cascade)
           (refer-to :conventions)
           (primary-key [:date :person_id])))))

(defn call-migration [migration]
  (mig/up migration))

(defn make-tables []
  (call-migration add-conventions-table)
  (call-migration add-slots-table)
  (call-migration add-persons-table)
  (call-migration add-events-table)
  (call-migration add-schedule-table)
  (call-migration add-schedule-issues-table)
  (call-migration add-preferred-slot-to-event)
  (call-migration add-event-count-to-event)
  (call-migration add-event-day-to-schedule)
  (call-migration person-non-availability))
