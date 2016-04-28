(ns herder.solver.run
  (:require
   [reloaded.repl :refer [system]]
   [herder.solver.schedule :refer [makeSolver makeSolverConfig setupSolution]]
   [herder.web.db :as db]
   [korma.core :as d]
   [korma.db :as kd]
   [clj-time.core :as t]
   [clj-time.coerce :as c]
   [clj-time.periodic :as p]
   [clj-uuid :as uuid]
   [herder.web.notifications :as notifications])
  (:import [herder.solver.types Event Person]))

(defn- gen-events [events events-persons slots convention persons-non-available]
  (let
   [firstDay (c/from-sql-date (:from convention))
    lastDay (c/from-sql-date (:to convention))]
    (mapv
     (fn [ev]
       (let [event-persons (map :person_id (filterv #(= (% :event_id) (:id ev)) events-persons))
             people (map #(Person. %) event-persons)
             slot (if (-> ev :preferred_slot_id nil? not) (first (filter #(= (:id %) (:preferred_slot_id ev)) slots)) nil)
             add-day #(t/plus % (t/days 1))
             non-availability (map #(-> % :date c/from-sql-date .toDateMidnight add-day .toLocalDate) (filter #(.contains event-persons (:person_id %)) persons-non-available))]
         (println "ev" ev)
         (println "na" non-availability)
         (loop [new-events [] previous nil count 1]
           (let [new-event
                 (doto
                  (Event. (:id ev))
                   (.setName (:name ev))
                   (.setPreferredSlots
                    (if (-> slot nil? not)
                      (for [day (p/periodic-seq firstDay (t/days 1))
                            :while (or (t/equal? lastDay day) (t/after? lastDay day))]
                        (let [beginSlot (t/plus day (t/minutes (:start-minutes slot)))
                              endSlot (t/plus day (t/minutes (:end-minutes slot)))]
                          (t/interval beginSlot endSlot))) []))
                   (.setPeople people)
                   (.setChainedEvent previous)
                   (.setEventDay count)
                   (.setDependantEventCount (- (:event_count ev) count))
                   (.setNotAvailableDays non-availability))]
             (if (= count (:event_count ev))
               (conj new-events new-event)
               (recur (conj new-events new-event) new-event (+ 1 count)))))))
     events)))

(defn get-constraints [solution solver]
  (let [scoreDirectorFactory (.getScoreDirectorFactory solver)
        scoreDirector (doto
                       (.buildScoreDirector scoreDirectorFactory)
                        (.setWorkingSolution solution)
                        (.calculateScore))]
    (.getConstraintMatchTotals scoreDirector)))

(defn solve [db id]
  (println "Begin")
  (kd/with-db (:connection db)
    (let [convention (first (d/select db/conventions (d/where {:id id})))
          slots (d/select db/slots (d/where {:convention_id id}))
          events (d/select db/events (d/where {:convention_id id}))
          events-persons (d/select db/events-persons (d/where {:convention_id id}))
          persons (d/select db/persons (d/where {:convention_id id}))
          persons-non-available (d/select db/person-non-availability (d/where {:convention_id id}))
          config {:firstDay (c/from-sql-date (:from convention))
                  :lastDay (c/from-sql-date (:to convention))
                  :slots
                  (mapv #(vector (t/minutes (:start-minutes %))
                                 (t/minutes (- (:end-minutes %) (:start-minutes %)))) slots)
                  :events (apply concat (gen-events events events-persons slots convention persons-non-available))}
          solver (-> (makeSolverConfig) (makeSolver))
          configured (setupSolution config)]
      ;(println "id" id)
      ;(println "conv" convention)
      ;(println "slots" slots)
      ;(println "events" events)
      ;(println "events-persons" events-persons)
      ;(println "persons" persons)
      (println "config" config)
      (try
        (do
          (.solve solver configured)
          (d/delete db/schedule (d/where {:convention_id id}))
          (d/delete db/schedule-issues (d/where {:convention_id id}))
          (d/delete db/schedule-issues-events (d/where {:convention_id id}))
          (let [solution (.getBestSolution solver)]
            (doseq [event (.getEvents solution)
                    :let [slot (.getSlot event)]]
              (if (-> slot nil? not)
                (let [slottime (c/to-date-time (.getStart (.getSlot event)))
                      slotoffset (t/millis (.getOffset (t/default-time-zone) slottime)) ; needed to fix TZ fun
                      day (t/minus (t/date-time (t/year slottime) (t/month slottime) (t/day slottime)) slotoffset)
                      values {:id (uuid/v1)
                              :date (c/to-sql-date slottime)
                              :slot_id (:id (first (filter #(t/equal? slottime (t/plus day (t/minutes (:start-minutes %)))) slots)))
                              :event_id (.getExternalId event)
                              :convention_id id
                              :event_day (.getEventDay event)}]
                  ;(println "event" (.getId event) (.getStart (.getSlot event)) (.getPreferredSlots event))
                  (println "values" values)
                  (println "chained" event (.getChainedEvent event))
                  (println "")
                  (d/insert db/schedule (d/values values)))))
            (notifications/send-notification [:schedule (str id)])
            (println "score" (-> solution .getScore .toString))
            (println "constraints")
            (doseq [constraint (get-constraints solution solver)]
              (doseq [match (.getConstraintMatchSet constraint)
                      :let [match-id (uuid/v1)
                            values {:id match-id
                                    :score (.getWeight match)
                                    :level (.getScoreLevel match)
                                    :issue (.getConstraintName match)
                                    :convention_id id}]]
                (println "match" values)
                (d/insert db/schedule-issues (d/values values))
                (doseq [event (.getJustificationList match)
                        :let [values {:id (uuid/v1)
                                      :schedule-issue_id match-id
                                      :event_id (.getExternalId event)
                                      :convention_id id}]]
                  (d/insert db/schedule-issues-events (d/values values))
                  (println "event" values))))
            (notifications/send-notification [:schedule-issues (str id)])))
        (catch Exception e
          (do
            (println "Failure in schedule")
            (println e)))
        (finally (do
                   (println "Solved")
                   (if (-> system :solver nil? not)
                     (reset! (-> system :solver :solving) false))))))))

(defn run-solver [id]
  (let [solv (:solver system)
        db (:db solv)
        pool (:pool solv)
        tasks [#(try
                  (solve db id)
                  (catch Throwable e
                    (println e)))]
        futures (.invokeAll pool tasks)]))

(defn solve-watch [key atom old-state new-state]
  (println "Need to solve" new-state (-> system :solver :solving deref))
  (if (and (-> system :solver :solving deref not) ; nothing currently going
           (-> new-state empty? not)) ; something to solve
    (do (reset! (-> system :solver :solving) true) ; mark as solving
        (let [item (first new-state)]
          (run-solver item)
          (swap! (-> system :solver :tosolve) disj item)))))

(defn needs-solve [id]
  (if (-> system :solver nil? not) ; if we have solver available. Not true in tests
    (do
      (if (-> system :solver :watch deref nil?)
        (do
          (add-watch (-> system :solver :tosolve) :solve-watch solve-watch)
          (reset! (-> system :solver :watch) :solve-watch)))
      (swap! (-> system :solver :tosolve) conj id))))

(if-let [sys-db (-> system :db :connection)]
  (kd/with-db sys-db
    (doseq [conv (d/select db/conventions)]
      (needs-solve (:id conv)))))

(def defaultConfig
  {:firstDay (t/date-time 2015 7 6)
   :lastDay (t/date-time 2015 7 7)
   :slots [[10 (t/hours 4)]
           [14 (t/hours 4)]]
   :events []})
