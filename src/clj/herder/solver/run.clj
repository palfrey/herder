(ns herder.solver.run
  (:require
   [reloaded.repl :refer [system]]
   [herder.solver.schedule :refer [makeSolver makeSolverConfig setupSolution]]
   [herder.web.db :as db]
   [korma.core :as d]
   [korma.db :as kd]
   [clj-time.core :as t]
   [clj-time.coerce :as c]
   [clj-uuid :as uuid]
   [herder.web.notifications :as notifications])
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
      (try
        (do
          (.solve solver configured)
          (d/delete db/schedule (d/where {:convention_id id}))
          (doseq [event (.getEvents (.getBestSolution solver))
                  :let [slot (.getSlot event)]]
            (if (-> slot nil? not)
              (let [slottime (c/to-date-time (.getStart (.getSlot event)))
                    slotoffset (t/millis (.getOffset (t/default-time-zone) slottime)) ; needed to fix TZ fun
                    day (t/minus (t/date-time (t/year slottime) (t/month slottime) (t/day slottime)) slotoffset)
                    values {:id (uuid/v1)
                            :date (c/to-sql-date slottime)
                            :slot_id (:id (first (filter #(t/equal? slottime (t/plus day (t/minutes (:start-minutes %)))) slots)))
                            :event_id (.getId event)
                            :convention_id id}]
                (println "event" (.getId event) (.getStart (.getSlot event)))
                (println "values" values)
                (d/insert db/schedule (d/values values))
                (notifications/send-notification [:schedule (str id)])))))
        (catch Exception e
          (do
            (println "Failure in schedule")
            (println e)))
        (finally (do
                   (println "Solved")
                   (reset! (-> system :solver :solving) false)))))))

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
  (if (-> system :solver :watch deref nil?)
    (do
      (add-watch (-> system :solver :tosolve) :solve-watch solve-watch)
      (reset! (-> system :solver :watch) :solve-watch)))
  (swap! (-> system :solver :tosolve) conj id))

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
