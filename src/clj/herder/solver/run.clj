(ns herder.solver.run
  (:require
   [reloaded.repl :refer [system]]
   [herder.solver.schedule :refer [makeSolver makeSolverConfig setupSolution]]
   [herder.web.db :as db]
   [korma.core :as d]
   [korma.db :as kd]
   [clj-time.core :as t]
   [clj-time.coerce :as c]
   [clj-uuid :as uuid])
  (:import [herder.solver.types Event Person]))

(defn- gen-events [events events-persons]
  (mapv
   (fn [ev]
     (let [people (map #(Person. (:person_id %)) (filterv #(= (% :event_id) (:id ev)) events-persons))]
       (doto
        (Event. (:id ev))
         (.setName (:name ev))
         (.setPeople people))))
   events))

(defn solve [db id]
  (println "Begin")
  (kd/with-db (:connection db)
    (let [convention (first (d/select db/conventions (d/where {:id id})))
          slots (d/select db/slots (d/where {:convention_id id}))
          events (d/select db/events (d/where {:convention_id id}))
          events-persons (d/select db/events-persons (d/where {:convention_id id}))
          persons (d/select db/persons (d/where {:convention_id id}))
          config {:firstDay (c/from-sql-date (:from convention))
                  :lastDay (c/from-sql-date (:to convention))
                  :slots
                  (mapv #(vector (t/minutes (:start-minutes %))
                                 (t/minutes (- (:end-minutes %) (:start-minutes %)))) slots)
                  :events (gen-events events events-persons)}
          solver (-> (makeSolverConfig) (makeSolver))
          configured (setupSolution config)]
      ;(println "id" id)
      ;(println "conv" convention)
      ;(println "slots" slots)
      ;(println "events" events)
      ;(println "events-persons" events-persons)
      ;(println "persons" persons)
      (println "config" config)
      (.solve solver configured)
      (d/delete db/schedule (d/where {:convention_id id}))
      (doseq [event (.getEvents (.getBestSolution solver))
              :let [slottime (.getStart (.getSlot event))
                    day (t/date-time (t/year slottime) (t/month slottime) (t/day slottime))
                    values {:id (uuid/v1)
                            :date (c/to-sql-date slottime)
                            :slot_id (:id (first (filter #(t/equal? slottime (t/plus day (t/minutes (:start-minutes %)))) slots)))
                            :event_id (.getId event)
                            :convention_id id}]]
        (println "event" (.getId event) (.getStart (.getSlot event)))
        (println "values" values)
        (d/insert db/schedule (d/values values))))))

(defn run-solver [id]
  (let [solv (:solver system)
        db (:db solv)
        pool (:pool solv)
        tasks [#(try
                  (solve db id)
                  (catch Throwable e
                    (println e)))]
        futures (.invokeAll pool tasks)]
    (for [ftr futures]
      (.get ftr))))

(def defaultConfig
  {:firstDay (t/date-time 2015 7 6)
   :lastDay (t/date-time 2015 7 7)
   :slots [[10 (t/hours 4)]
           [14 (t/hours 4)]]
   :events []})
