(ns schedule.web.handler
  (:require
   [compojure.route :as route]
   [compojure.core :refer [defroutes GET]]
   [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
   [reloaded.repl :refer [system]]
   [schedule.web.html :as html]))

(defroutes routes
  (GET "/" [] (html/index (:db system)))
  (route/not-found "Not found"))

(def app
  (-> routes
      (wrap-defaults site-defaults)))
