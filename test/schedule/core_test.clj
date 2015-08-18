(ns schedule.core-test
  (:use
   [midje.sweet]
   [schedule.types]
   [schedule.core])
  (:require
   [clj-time.core :as t])
  (:import 
   [schedule.types ScheduleSolution Event Slot]
   [org.optaplanner.core.config.solver SolverConfig]
   [org.optaplanner.core.api.solver Solver]
   [org.joda.time Interval]))

(defn isClass [ofClass]
  (chatty-checker [thing]
    (.isAssignableFrom ofClass (class thing))))

(fact "Can make solver config"
      (makeSolverConfig) => (isClass SolverConfig))

(fact "Can make solver" 
      (-> (makeSolverConfig) (makeSolver)) => (isClass Solver))

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
   :events [(Event.) (Event.)]})

(defn getSolution []
  (-> (solve defaultConfig) (.getBestSolution)))

(fact "Can solve"
      (solve defaultConfig) => (isClass Solver))
(fact "Can get best solution"
      (getSolution) => (isClass ScheduleSolution))

(fact "Slot generation works"
      (genSlots [[10 (t/hours 4)]]) => (one-of (isClass Slot)))
(fact "Slot generation works with default"
      (genSlots (:slots defaultConfig)) => (two-of (isClass Slot)))
(fact "Config works"
      (-> (setupSolution defaultConfig) (.getSlots)) => (two-of (isClass Slot)))
(fact "Slot range is derived from the slots"
      (-> (setupSolution defaultConfig) (.getSlotRange)) => (four-of (isClass org.joda.time.Interval)))

(fact "Events have distinct slots"
      (->> (getSolution) (.getEvents) (map #(.getSlot %)))
      => (chatty-checker [items] (apply distinct? items)))

(fact "Solution has good score"
      (-> (getSolution) (.getScore) (.toString)) => "0hard/0soft")
