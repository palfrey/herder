(ns herder.web.handler
  (:require
   [compojure.route :as route]
   [compojure.core :refer [defroutes GET POST PUT context] :as compojure]
   [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
   [ring.util.response :refer [file-response content-type]]
   [ring.middleware.json :refer [wrap-json-response wrap-json-params]]
   [ring.middleware.keyword-params :refer [wrap-keyword-params]]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [korma.core :as d]
   [korma.db :as kd]
   [reloaded.repl :refer [system]]
   [clostache.parser :as clostache]

   [herder.web.conventions :refer [convention-routes]]))

(defn page [component title params]
  (clostache/render
   (slurp (clojure.java.io/resource
           (str "herder/templates/index.clostache")))
   {:component component
    :title title}))

(defroutes core-routes
  (GET "/" [] (partial page "herder.conventions.conventions-component" "Index"))
  (context "/api" []
    convention-routes)
  (route/files "/static" {:root "target/resources/public/"})
  (route/not-found "Not found"))

(def routes core-routes)

(defn wrap-db [f]
  (fn [req]
    (if-let [sys-db (-> system :db :connection)]
      (kd/with-db sys-db
        (f req))
      (f req))))

(def app
  (-> #'routes
      wrap-json-response
      wrap-keyword-params
      wrap-json-params

      (wrap-defaults (assoc-in api-defaults [:params :nested] true))
      wrap-db))
