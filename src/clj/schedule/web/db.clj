(ns schedule.web.db
  (:require [korma.db :as db]
            [korma.core :as core]))

;(defdb db (sqlite3 {:db "resources/db/korma.db"}))

(core/defentity conventions)
(core/defentity slots)
