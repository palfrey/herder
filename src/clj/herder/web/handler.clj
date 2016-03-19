(ns herder.web.handler
  (:require
   [compojure.route :as route]
   [compojure.core :refer [defroutes GET POST PUT context] :as compojure]
   [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
   [ring.util.response :refer [file-response content-type]]
   [ring.middleware.json :refer [wrap-json-response]]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [korma.core :as d]
   [korma.db :as kd]
   [reloaded.repl :refer [system]]

   [herder.web.conventions :refer [convention-routes]]))

(defn index [params]
  (content-type (file-response "resources/herder/templates/index.html") "text/html"))

(defroutes core-routes
  (GET "/" [] index)
  (route/files "/resources/public/" {:root "target/resources/public/"})
  (route/not-found "Not found"))

(def routes (compojure/routes convention-routes core-routes))

(defn wrap-db [f]
  (fn [req]
    (if-let [sys-db-spec (:db-spec (:db system))]
      (kd/with-db (kd/create-db sys-db-spec)
        (f req))
      (f req))))

(def app
  (-> #'routes
      (wrap-defaults (assoc-in api-defaults [:params :nested] true))
      wrap-db
      wrap-json-response))
