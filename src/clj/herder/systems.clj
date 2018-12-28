(ns herder.systems
  (:require
   [system.core :refer [defsystem]]
   [com.stuartsierra.component :as component]
   [clojure.edn :as edn]
   (system.components
    [http-kit :refer [new-web-server]]
    [repl-server :refer [new-repl-server]]
    [endpoint :refer [new-endpoint]]
    [middleware :refer [new-middleware]]
    [handler :refer [new-handler]]
    [h2 :as h2]
    [postgres :as postgres]
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

(defn herder-system [db-spec]
  (component/system-map
   :db (new-database db-spec)
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

(defn dev-system []
  (herder-system
   (assoc h2/DEFAULT-DB-SPEC
          :subname (.getAbsolutePath (io/file dbPath))
          :make-pool? true)))

(defn prod-system []
  (herder-system
   (let [db-url (env :database-url)]
     (if (nil? db-url)
       (throw (Exception. "DATABASE_URL isn't set!"))
       (let [db-uri (java.net.URI. db-url)
             [username password] (clojure.string/split (.getUserInfo db-uri) #":")
             port (.getPort db-uri)
             port (if (= port -1) 5432 port)] ; provide the default Postgres port
         (assoc postgres/DEFAULT-DB-SPEC
                :jdbc-url (format "jdbc:postgresql://%s:%s%s" (.getHost db-uri) port (.getPath db-uri))
                :subname (format "//%s:%s%s" (.getHost db-uri) port (.getPath db-uri))
                :host (.getHost db-uri)
                :user username
                :password password))))))
