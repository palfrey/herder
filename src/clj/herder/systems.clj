(ns herder.systems
  (:require [system.core :refer [defsystem]]
            [com.stuartsierra.component :as component]
            [clojure.edn :as edn]
            (system.components
             [http-kit :refer [new-web-server]]
             [repl-server :refer [new-repl-server]]
             [endpoint :refer [new-endpoint]]
             [middleware :refer [new-middleware]]
             [handler :refer [new-handler]]
             [h2 :refer [DEFAULT-DB-SPEC]]
             [sente :refer [new-channel-socket-server]])
            [environ.core :refer [env]]
            [herder.systems.korma :refer [new-database]]
            [herder.systems.solver :refer [new-solver]]
            [herder.web.handler :refer [event-msg-handler routes wrap-db]]
            [clojure.java.io :as io]
            [taoensso.sente.server-adapters.http-kit :refer [http-kit-adapter]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]))

(def dbPath "herder.db")

(defn make-middleware []
  (new-middleware
   {:middleware [[wrap-json-response]
                 [wrap-keyword-params]
                 [wrap-json-params]
                 [wrap-defaults :defaults]
                 [wrap-db]]
    :defaults (assoc-in api-defaults [:params :nested] true)}))

(defn dev-system []
  (component/system-map
   :db (new-database
        (assoc DEFAULT-DB-SPEC
               :subname (.getAbsolutePath (io/file dbPath))
               :make-pool? true))
   :sente (new-channel-socket-server event-msg-handler http-kit-adapter)
   :routes (component/using
            (new-endpoint routes)
            [:sente])
   :middleware (make-middleware)
   :handler (component/using
             (new-handler)
             [:routes :middleware])
   :web (component/using
         (new-web-server (Integer. (env :http-port)))
         [:handler :db])
   :solver (component/using (new-solver) [:db])))

(defsystem prod-system
  [:web (new-web-server (Integer. (env :http-port)))
   :repl-server (new-repl-server (Integer. (env :repl-port)))
   :db (new-database)])
