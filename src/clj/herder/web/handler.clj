(ns herder.web.handler
  (:require
   [clostache.parser :as clo]
   [compojure.route :as route]
   [compojure.core :refer [defroutes GET POST PUT context] :as compojure]
   [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
   [ring.util.response :refer [redirect response status]]
   [ring.middleware.json :refer [wrap-json-response]]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [korma.core :as d]

   [herder.web.conventions :refer [convention-routes]]
   [herder.web.db :as db]))

(defn common-partials [] {:footer (clo/render-resource "templates/footer.mustache")})

(defn common-features []
  {:header #(-> (clo/render-resource "templates/header.mustache") (clojure.string/replace "%TITLE%" %))})

(defn render [template values]
  (clo/render-resource
   template
   (merge common-features values {:anti-forgery (anti-forgery-field)})
   common-partials))

(defn index [params]
  (let [conventions (d/select db/conventions)]
    (render "templates/index.mustache"
            {:conventions conventions})))

(defroutes core-routes
  (GET "/" [] index)
  (route/resources "/")
  (route/not-found "Not found"))

(def routes (compojure/routes convention-routes core-routes))

(defn app [routes]
  (-> routes
      (wrap-defaults api-defaults)
      wrap-json-response))
