(ns schedule.solver.core-test
  (:use
   [schedule.solver.types]
   [schedule.solver.schedule]
   [clojure.test])
  (:require
   [clj-time.core :as t])
  (:import
   [schedule.solver.types ScheduleSolution Event Slot Person]
   [org.optaplanner.core.config.solver SolverConfig]
   [org.optaplanner.core.api.solver Solver]
   [org.joda.time Interval]))

(defn isClass [ofClass thing]
  (.isAssignableFrom ofClass (class thing)))

(testing "Can make solver config"
  (is (isClass SolverConfig (makeSolverConfig))))

(testing "Can make solver"
  (is (isClass Solver (-> (makeSolverConfig) (makeSolver)))))

(defn solve [config]
  (let [solver (-> (makeSolverConfig) (makeSolver))
        configured (setupSolution config)]
    (.solve solver configured)
    solver))

(def defaultConfig
  {:firstDay (t/date-time 2015 7 6)
   :lastDay (t/date-time 2015 7 7)
   :slots [[10 (t/hours 4)]
           [14 (t/hours 4)]]
   :events []})

(defn getSolution [events]
  (->
   (assoc defaultConfig :events events)
   (solve)
   (.getBestSolution)))

(defmacro n-of [func count items]
  (list 'map func items))

(testing "Can solve"
  (is (isClass Solver (solve defaultConfig))))
(testing "Can get best solution"
  (is (isClass ScheduleSolution (getSolution []))))

(testing "Slot generation works"
  (is (n-of (partial isClass Slot) 1 (genSlots [[10 (t/hours 4)]])))) ; one-of
(testing "Slot generation works with default"
  (is (n-of (partial isClass Slot) 2 (genSlots (:slots defaultConfig))))) ; two-of
(testing "Config works"
  (is (n-of (partial isClass Slot) 2 (-> (setupSolution defaultConfig) (.getSlots))))) ; two-of
(testing "Slot range is derived from the slots"
  (is (n-of (partial isClass org.joda.time.Interval) 4 (-> (setupSolution defaultConfig) (.getSlotRange))))) ; four-of

(testing "Solution has good score"
  (is (= "0hard/0soft"
         (->
          (getSolution [])
          (.getScore)
          (.toString)))))

(testing "Events have distinct slots"
  (is (->>
       (getSolution [(Event.) (Event.)])
       (.getEvents)
       (map #(.getSlot %))
       distinct?)))

(let [alpha (Person.)
      beta (Person.)
      eventWithPerson (fn [person]
                        (doto (Event.)
                          (.setPeople [person])))]

  (testing "Events have distinct slots with full schedule"
    (is (->>
         (getSolution (repeatedly 4 #(eventWithPerson alpha)))
         (.getEvents)
         (map #(.getSlot %))
         distinct?)))

  (testing "Events have distinct slots with overly full schedule"
    (->> (getSolution
          (concat
           (repeatedly 4 #(eventWithPerson alpha))
           (repeatedly 4 #(eventWithPerson beta))))
         (.getEvents)
         (map #(vector (.getSlot %) (-> (.getPeople %) first)))
         distinct?)))
