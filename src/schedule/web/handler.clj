(ns schedule.web.handler
  (:use [clostache.parser])
  (:require
   [compojure.route :as route]
   [compojure.handler :as handler]
   [compojure.core :refer [defroutes GET context routes]]
   [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
   [reloaded.repl :refer [system]]))

(def common-partials {:footer (render-resource "templates/footer.mustache")})

(def common-features 
  {:header #(-> (render-resource "templates/header.mustache") (clojure.string/replace "%TITLE%" %))})

(defn index [db]
  (render-resource
   "templates/index.mustache"
   (assoc common-features
          :conventions (get db :conventions))
   common-partials))

(defn new-convention [db]
  (render-resource "templates/new-convention.mustache" common-features common-partials))

(let [db (:db system)]
  (defroutes convention-routes
    (context "/convention" []
      (GET "/new" [] (new-convention db))))
  (defroutes core-routes
    (GET "/" [] (index db))
    (route/not-found "Not found")))

(def app
  (-> (routes convention-routes core-routes)
      (wrap-defaults site-defaults)))
