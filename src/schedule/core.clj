(ns schedule.core
  (:require [schedule.Solution])
  (:import 
  	[org.optaplanner.core.api.solver SolverFactory]
  	[org.optaplanner.core.config.solver SolverConfig]
  	[org.optaplanner.core.config.score.director ScoreDirectorFactoryConfig]
  	[org.optaplanner.core.config.score.definition ScoreDefinitionType]
  	[schedule Solution]
  )
  (:gen-class))

(defn scoreDirectoryFactory []
	(doto
		(ScoreDirectorFactoryConfig.)
		(.setScoreDefinitionType ScoreDefinitionType/SIMPLE)
		(.setScoreDrlList ["schedule/solver/rules.drl"])
	)
)

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!")
  (println 
  	(doto
  		(SolverConfig.)
  		(.setSolutionClass Solution/class)
  		(.setScoreDirectorFactoryConfig (scoreDirectoryFactory))
  		(.buildSolver)
  	)
  )
)