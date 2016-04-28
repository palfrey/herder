(ns herder.solver.schedule
  (:use [herder.solver.types])
  (:import
   [org.optaplanner.core.config.solver SolverConfig]
   [org.optaplanner.core.config.score.director ScoreDirectorFactoryConfig]
   [org.optaplanner.core.config.score.definition ScoreDefinitionType]
   [org.optaplanner.core.config.constructionheuristic ConstructionHeuristicPhaseConfig ConstructionHeuristicType]
   [org.optaplanner.core.config.localsearch LocalSearchPhaseConfig LocalSearchType]
   [org.optaplanner.core.config.localsearch.decider.acceptor AcceptorConfig]
   [org.optaplanner.core.config.solver.termination TerminationConfig]

   [herder.solver.types HerderSolution Event Slot]))

(defn scoreDirectoryFactory []
  (doto
   (ScoreDirectorFactoryConfig.)
    (.setScoreDefinitionType ScoreDefinitionType/HARD_SOFT)
    (.setScoreDrlList ["herder/solver/rules.drl"])))

(defn genConstructionHeuristic []
  (doto
   (ConstructionHeuristicPhaseConfig.)
    (.setConstructionHeuristicType ConstructionHeuristicType/FIRST_FIT_DECREASING)))

(defn genLocalSearch [type]
  (doto
   (LocalSearchPhaseConfig.)
    (.setLocalSearchType type)))

(defn genPhaseSearch []
  [(genConstructionHeuristic)
   (genLocalSearch LocalSearchType/TABU_SEARCH)])

(defn genTerminationConfig []
  (doto
   (TerminationConfig.)
    (.setSecondsSpentLimit 10)
    (.setBestScoreLimit "0hard/0soft")))

(defn makeSolverConfig []
  (doto
   (SolverConfig.)
    (.setSolutionClass HerderSolution)
    (.setScoreDirectorFactoryConfig (scoreDirectoryFactory))
    (.setEntityClassList [Event])
    (.setPhaseConfigList (genPhaseSearch))
    (.setTerminationConfig (genTerminationConfig))))

(defn makeSolver [solverConfig]
  (.buildSolver solverConfig))

(defn genSlots [slots]
  (map #(Slot. (first %) (second %)) slots))

(defn setupSolution [config]
  (doto
   (HerderSolution.)
    (.setFirstDay (:firstDay config))
    (.setLastDay (:lastDay config))
    (.setSlots (genSlots (:slots config)))
    (.setEvents (:events config))))
