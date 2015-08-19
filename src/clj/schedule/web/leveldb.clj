(ns schedule.web.leveldb
  (:require
   [com.stuartsierra.component :as component]
   [clj-leveldb :as leveldb]))

(defrecord LevelDB [dbPath config connection]
  component/Lifecycle
  (start [component]
    (let [connection (leveldb/create-db dbPath config)]
      (assoc component :connection connection)))
  (stop [component]
    (when connection
      (.close connection)
      component)))

(defn new-leveldb
  ([dbPath]
   (map->LevelDB {:dbPath dbPath :config {}}))
  ([dbPath config]
   (map->LevelDB {:dbPath dbPath :config config})))