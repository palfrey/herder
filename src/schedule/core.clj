(ns schedule.core
  (:use [schedule.types])
  (:import 
  	[org.optaplanner.core.api.solver SolverFactory]
  	[org.optaplanner.core.config.solver SolverConfig]
  	[org.optaplanner.core.config.score.director ScoreDirectorFactoryConfig]
  	[org.optaplanner.core.config.score.definition ScoreDefinitionType]
    [org.optaplanner.core.config.constructionheuristic ConstructionHeuristicPhaseConfig]

  	[schedule.types Solution Event]
  )
  (:gen-class))

(defn scoreDirectoryFactory []
	(doto
		(ScoreDirectorFactoryConfig.)
		(.setScoreDefinitionType ScoreDefinitionType/SIMPLE)
		(.setScoreDrlList ["schedule/solver/rules.drl"])
	)
)

(defn genPhaseSearch []
  [(ConstructionHeuristicPhaseConfig.)]
)

(defn -main
  [& args]
  (println 
  	(doto
  		(SolverConfig.)
  		(.setSolutionClass (class (Solution.)))
  		(.setScoreDirectorFactoryConfig (scoreDirectoryFactory))
      (.setEntityClassList [(class (Event.))])
      (.setPhaseConfigList (genPhaseSearch))
  		(.buildSolver)
  	)
  )
)