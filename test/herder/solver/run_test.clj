(ns herder.solver.run-test
  (:use
   [clojure.test]
   [herder.web.events :refer [event-type-map]])
  (:require
   [herder.solver.run :as r]
   [clj-uuid :as uuid]
   [clj-time.coerce :as c])
  (:import
   [herder.solver.person Person]
   [herder.solver.event Event EventType]))

(deftest CanGenerateMultipleSlotsCorrectly
  (let [preferred_slot_id (uuid/v1)
        conventionDate (-> "2012-01-01" c/from-string c/to-sql-date)
        events (apply concat
                      (r/gen-events [{:event_type (:one_day event-type-map)
                                      :event_count 2
                                      :id (uuid/v1)
                                      :name "foo"
                                      :preferred_slot_id preferred_slot_id}] []
                                    [{:id preferred_slot_id
                                      :start-minutes 0
                                      :end-minutes 20}]
                                    {:from conventionDate :to conventionDate} []))]
    (is (= (count events) 2))
    (is (not (empty? (-> events first .getPreferredSlots))))
    (is (empty? (-> events second .getPreferredSlots)))))

; events events-persons slots convention persons-non-available
