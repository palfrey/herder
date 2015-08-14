(ns schedule.core
  (:use [schedule.types])
  (:import
   [org.optaplanner.core.config.solver SolverConfig]
   [org.optaplanner.core.config.score.director ScoreDirectorFactoryConfig]
   [org.optaplanner.core.config.score.definition ScoreDefinitionType]
   [org.optaplanner.core.config.constructionheuristic ConstructionHeuristicPhaseConfig]
   [org.optaplanner.core.config.solver.termination TerminationConfig]

   [schedule.types ScheduleSolution Event Slot]))

(defn scoreDirectoryFactory []
  (doto
   (ScoreDirectorFactoryConfig.)
    (.setScoreDefinitionType ScoreDefinitionType/HARD_SOFT)
    (.setScoreDrlList ["schedule/solver/rules.drl"])))

(defn genPhaseSearch []
  [(ConstructionHeuristicPhaseConfig.)])

(defn genTerminationConfig []
  (doto
   (TerminationConfig.)
    (.setSecondsSpentLimit 10)))

(defn makeSolverConfig []
  (doto
   (SolverConfig.)
    (.setSolutionClass ScheduleSolution)
    (.setScoreDirectorFactoryConfig (scoreDirectoryFactory))
    (.setEntityClassList [Event])
    (.setPhaseConfigList (genPhaseSearch))
    (.setTerminationConfig (genTerminationConfig))))

(defn makeSolver [solverConfig]
  (.buildSolver solverConfig))

(defn genSlots [slots]
  (map #(Slot. (int (first %)) (second %)) slots))

(defn setupSolution [config]
  (doto
   (ScheduleSolution.)
    (.setFirstDay (:firstDay config))
    (.setLastDay (:lastDay config))
    (.setSlots (genSlots (:slots config)))
    (.setEvents (:events config))))

(defn -main
  [& args]
  (println (makeSolverConfig)))