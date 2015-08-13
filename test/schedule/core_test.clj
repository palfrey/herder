(ns schedule.core-test
  (:use
   [midje.sweet]
   [schedule.types])
  (:require [schedule.core :refer :all])
  (:import  [schedule.types ScheduleSolution Event]
            [org.optaplanner.core.config.solver SolverConfig]
            [org.optaplanner.core.api.solver Solver]))

(defn isClass [ofClass]
  (chatty-checker [thing]
    (.isAssignableFrom ofClass (class thing))))

(fact "Can make solver config" (makeSolverConfig) => (isClass SolverConfig))

(fact "Can make solver" (-> (makeSolverConfig) (makeSolver)) => (isClass Solver))

(defn solve []
  (let [solver (-> (makeSolverConfig) (makeSolver))]
    (.solve solver (ScheduleSolution.))
    solver))

(fact "Can solve" (solve) => (isClass Solver))

(fact "Can get best solution" (-> (solve) (.getBestSolution)) => (isClass ScheduleSolution))