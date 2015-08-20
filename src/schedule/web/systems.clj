(ns schedule.web.systems
  (:require [system.core :refer [defsystem]]
            [com.stuartsierra.component :as component]
            [clojure.edn :as edn]
            (system.components
             [http-kit :refer [new-web-server]]
             [repl-server :refer [new-repl-server]]
             [app :refer [new-app]])
            [schedule.web.leveldb :refer [new-leveldb]]
            [environ.core :refer [env]]
            [schedule.web.handler :refer [routes app]]))

(def dbPath "schedule.db")
(def config {:key-encoder pr-str
             :val-encoder pr-str
             :key-decoder (comp keyword byte-streams/to-string)
             :val-decoder (comp edn/read-string byte-streams/to-char-sequence)})

(defn dev-system []
  (component/system-map
   :db (new-leveldb dbPath config)
   :app (component/using
         (new-app routes #'app nil)
         [:db])
   :web (component/using
         (new-web-server (Integer. (env :http-port)))
         {:handler :app})))

(defsystem prod-system
  [:web (new-web-server (Integer. (env :http-port)) app)
   :repl-server (new-repl-server (Integer. (env :repl-port)))
   :db (new-leveldb dbPath config)])
