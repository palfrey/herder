(ns schedule.web.systems
  (:require [system.core :refer [defsystem]]
            (system.components
             [jetty :refer [new-web-server]]
             [repl-server :refer [new-repl-server]])
            [schedule.web.leveldb :refer [new-leveldb]]
            [environ.core :refer [env]]
            [schedule.web.handler :refer [app]]))

(def dbPath "schedule.db")
(def config {:key-decoder byte-streams/to-string
             :val-decoder byte-streams/to-string})

(defsystem dev-system
  [:web (new-web-server (Integer. (env :http-port)) app)
   :db (new-leveldb dbPath config)])

(defsystem prod-system
  [:web (new-web-server (Integer. (env :http-port)) app)
   :repl-server (new-repl-server (Integer. (env :repl-port)))
   :db (new-leveldb dbPath config)])
