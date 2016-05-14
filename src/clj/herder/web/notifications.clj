(ns herder.web.notifications
  (:require
   [system.repl :refer [system]]))

(defn send-notification [key]
  (if-let [chsk-send (-> system :sente :chsk-send!)]
    (chsk-send :sente/all-users-without-uid [::cache-invalidate [key]])))
