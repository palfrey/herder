(ns schedule.core-test
  (:use
  	[midje.sweet]
   	[schedule.types]
  )
  (:require [schedule.core :refer :all])
  (:import  [schedule.types ScheduleSolution Event])
)

(fact "Can make solver config" (makeSolverConfig) => anything) ; doesn't throw exception

(fact "Can make solver" (-> (makeSolverConfig) (makeSolver)) => anything) ; doesn't throw exception

(fact "Can solve" (-> (makeSolverConfig) (makeSolver) (#(.solve % (ScheduleSolution.)))) => nil)