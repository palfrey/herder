(ns schedule.core-test
  (:use
   [midje.sweet]
   [schedule.types]
   [schedule.core])
  (:require
   [clj-time.core :as t])
  (:import  [schedule.types ScheduleSolution Event Slot]
            [org.optaplanner.core.config.solver SolverConfig]
            [org.optaplanner.core.api.solver Solver]))

(defn isClass [ofClass]
  (chatty-checker [thing]
    (.isAssignableFrom ofClass (class thing))))

(fact "Can make solver config" (makeSolverConfig) => (isClass SolverConfig))

(fact "Can make solver" (-> (makeSolverConfig) (makeSolver)) => (isClass Solver))

(defn solve [config]
  (let [solver (-> (makeSolverConfig) (makeSolver))]
    (.solve solver (setupSolution config))
    solver))

(def defaultConfig {:firstDay (t/date-time 2015 7 6)
                    :lastDay (t/date-time 2015 7 13)
                    :slots [[10 14] [14 18] [18 22]]})

(fact "Can solve" (solve defaultConfig) => (isClass Solver))

(fact "Can get best solution" (-> (solve defaultConfig) (.getBestSolution)) => (isClass ScheduleSolution))

(fact "Slot generation works" (genSlots [[10 (t/hours 4)]]) => (one-of (isClass Slot)))