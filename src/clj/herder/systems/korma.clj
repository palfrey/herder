(ns herder.systems.korma
  (:require [com.stuartsierra.component :as component]
            [korma.db :as db]
            [lobos.migration :as mig]
            [lobos.connectivity :as conn]
            [herder.web.lobos :as lobos]))

(defrecord KormaDatabase [db-spec connection]
  component/Lifecycle
  (start [component]
    (let [db-spec (:db-spec component)
          sname nil]
      (binding [mig/*src-directory* "src/clj"
                mig/*migrations-namespace* 'herder.web.lobos]
        (mig/create-migrations-table db-spec sname)
        (let [conn (db/create-db db-spec)
              names (mig/pending-migrations db-spec sname)]
          (conn/with-connection db-spec
            (mig/do-migrations db-spec sname :up names))
          (assoc component :connection conn)))))
  (stop [component]
    (assoc component :connection nil)))

(defn new-database [db-spec]
  (map->KormaDatabase {:db-spec db-spec}))
