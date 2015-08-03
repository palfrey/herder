(ns schedule.core
  (:import [org.optaplanner.core.api.solver SolverFactory])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!")
  (println (SolverFactory/createFromXmlResource "departments/resourcescheduler/appointments/solver/appointmentsSolverConfig.xml"))
)