(ns herder.web.db
  (:require [korma.db :as db]
            [korma.core :as core]))

;(defdb db (sqlite3 {:db "resources/db/korma.db"}))

(core/defentity conventions)
(core/defentity slots)
(core/defentity persons)
(core/defentity events)
(core/defentity events-persons)
(core/defentity schedule)
(core/defentity schedule-issues)
(core/defentity schedule-issues-events)
