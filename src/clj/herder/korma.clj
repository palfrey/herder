(ns herder.korma
  (:require [com.stuartsierra.component :as component]
            [korma.db :as db]))

(defrecord KormaDatabase [db-spec connection]
  component/Lifecycle
  (start [component]
    (let [conn (db/create-db (:db-spec component))]
      (assoc component :connection conn)))
  (stop [component]
    ;(println "component" component)
    ;(.close (:connection component))
    (assoc component :connection nil)))

(defn new-database [db-spec]
  (map->KormaDatabase {:db-spec db-spec}))
