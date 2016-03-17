(ns herder.web.handler
  (:require
   [compojure.route :as route]
   [compojure.core :refer [defroutes GET POST PUT context] :as compojure]
   [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
   [ring.util.response :refer [redirect response status file-response]]
   [ring.middleware.json :refer [wrap-json-response]]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [korma.core :as d]
   [korma.db :as kd]
   [reloaded.repl :refer [system]]

   [herder.web.conventions :refer [convention-routes]]
   [herder.web.db :as db]))

(defn index [params]
  (println (keys params))
  (let [conventions (d/select db/conventions)]
    (file-response "templates/index.html")))

(defroutes core-routes
  (GET "/" [] index)
  (route/resources "/")
  (route/not-found "Not found"))

(def routes (compojure/routes convention-routes core-routes))

(defn wrap-db [f]
  (fn [req]
    (if-let [sys-db (:db system)]
      (kd/with-db (:db system)
        (f req))
      (f req))))

(def app
  (-> #'routes
      (wrap-defaults (assoc-in api-defaults [:params :nested] true))
      wrap-db
      wrap-json-response))
