(ns herder.web.notifications
  (:require
   [reloaded.repl :refer [system]]))

(defn send-notification [key]
  (if-let [chsk-send (-> system :sente :chsk-send!)]
    (chsk-send :sente/all-users-without-uid [::cache-invalidate [key]])))
