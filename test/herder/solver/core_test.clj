(ns herder.solver.core-test
  (:use
   [herder.solver.types]
   [herder.solver.schedule]
   [clojure.test])
  (:require
   [clj-time.core :as t])
  (:import
   [herder.solver.types HerderSolution Event Slot Person]
   [org.optaplanner.core.config.solver SolverConfig]
   [org.optaplanner.core.api.solver Solver]
   [org.joda.time Interval]))

(defn isClass [ofClass thing]
  (.isAssignableFrom ofClass (class thing)))

(deftest CanMakeSolverConfig
  (is (isClass SolverConfig (makeSolverConfig))))

(deftest CanMakeSolver
  (is (isClass Solver (-> (makeSolverConfig) (makeSolver)))))

(defn solve [config]
  (let [solver (-> (makeSolverConfig) (makeSolver))
        configured (setupSolution config)]
    (.solve solver configured)
    solver))

(def defaultConfig
  {:firstDay (t/date-time 2015 7 6)
   :lastDay (t/date-time 2015 7 7)
   :slots [[(t/hours 10) (t/hours 4)]
           [(t/hours 14) (t/hours 4)]]
   :events []})

(defn getSolution [events]
  (->
   (assoc defaultConfig :events events)
   (solve)
   (.getBestSolution)))

(defmacro n-of [func count items]
  (list 'map func items))

(deftest CanSolve
  (is (isClass Solver (solve defaultConfig))))
(deftest CanGetBestSolution
  (is (isClass HerderSolution (getSolution []))))

(deftest SlotGenerationWorks
  (is (n-of (partial isClass Slot) 1 (genSlots [[(t/hours 10) (t/hours 4)]])))) ; one-of
(deftest SlotGenerationWorksWithDefault
  (is (n-of (partial isClass Slot) 2 (genSlots (:slots defaultConfig))))) ; two-of
(deftest ConfigWorks
  (is (n-of (partial isClass Slot) 2 (-> (setupSolution defaultConfig) (.getSlots))))) ; two-of
(deftest SlotRangeIsDerivedFromTheSlots
  (is (n-of (partial isClass org.joda.time.Interval) 4 (-> (setupSolution defaultConfig) (.getSlotRange))))) ; four-of

(deftest SolutionHasGoodScore
  (is (= "0hard/0soft"
         (->
          (getSolution [])
          (.getScore)
          (.toString)))))

(deftest EventsHaveDistinctSlots
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

  (deftest EventsHaveDistinctSlotsWithFullSolution
    (is (->>
         (getSolution (repeatedly 4 #(eventWithPerson alpha)))
         (.getEvents)
         (map #(.getSlot %))
         distinct?)))

  (deftest EventsHavedistinctSlotsWithOverlyFullSolution
    (->> (getSolution
          (concat
           (repeatedly 4 #(eventWithPerson alpha))
           (repeatedly 4 #(eventWithPerson beta))))
         (.getEvents)
         (map #(vector (.getSlot %) (-> (.getPeople %) first)))
         distinct?)))
