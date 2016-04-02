(ns herder.web.notifications
  (:require
   [reloaded.repl :refer [system]]))

(defn send-notification []
  ((-> system :sente :chsk-send!) :sente/all-users-without-uid [::cache-invalidate [:conventions]]))
