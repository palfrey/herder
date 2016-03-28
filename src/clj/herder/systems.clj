(ns herder.systems
  (:require [system.core :refer [defsystem]]
            [com.stuartsierra.component :as component]
            [clojure.edn :as edn]
            (system.components
             [http-kit :refer [new-web-server]]
             [repl-server :refer [new-repl-server]]
             [h2 :refer [DEFAULT-DB-SPEC]])
            [environ.core :refer [env]]
            [herder.systems.korma :refer [new-database]]
            [herder.web.handler :refer [app]]
            [clojure.java.io :as io]))

(def dbPath "herder.db")

(defn dev-system []
  (component/system-map
   :db (new-database
        (assoc DEFAULT-DB-SPEC
               :subname (.getAbsolutePath (io/file dbPath))
               :make-pool? true))
   :web (component/using
         (new-web-server (Integer. (env :http-port)) app)
         [:db])))

(defsystem prod-system
  [:web (new-web-server (Integer. (env :http-port)) app)
   :repl-server (new-repl-server (Integer. (env :repl-port)))
   :db (new-database)])
