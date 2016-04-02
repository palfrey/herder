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
   [system.components.sente :refer [sente-routes]]
   [herder.web.conventions :refer [convention-routes]]
   [taoensso.timbre :as timbre :refer (tracef debugf infof warnf errorf)]))

(defn page [component title request]
  (clostache/render
   (slurp (clojure.java.io/resource
           (str "herder/templates/index.clostache")))
   (let [params (assoc (:params request)
                       :component component
                       :title title)]
     {:params (map #(hash-map :key (name %) :value (get params %)) (keys params))})))

(def uuid-regex #"[\w]{8}(-[\w]{4}){3}-[\w]{12}")

(defroutes core-routes
  (GET "/" [] (partial page "herder.conventions.component" "Index"))
  (GET ["/convention/:id" :id uuid-regex] [id] (partial page "herder.slots.component", "Convention"))
  (context "/api" []
    convention-routes)
  (route/files "/static" {:root "target/resources/public/"})
  (route/not-found "Not found"))

(defn routes [system]
  (compojure/routes
   (sente-routes system) core-routes))

(defn event-msg-handler [& args]
  (infof "args %s" (-> args first :event)))

(defn wrap-db [f]
  (fn [req]
    (if-let [sys-db (-> system :db :connection)]
      (kd/with-db sys-db
        (f req))
      (f req))))
