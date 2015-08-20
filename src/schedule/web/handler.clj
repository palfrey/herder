(ns schedule.web.handler
  (:require
   [clostache.parser :as clo]
   [compojure.route :as route]
   [compojure.handler :as handler]
   [compojure.core :refer [defroutes GET context routes]]
   [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
   [reloaded.repl :refer [system]]))

(def common-partials {:footer (clo/render-resource "templates/footer.mustache")})

(def common-features 
  {:header #(-> (clo/render-resource "templates/header.mustache") (clojure.string/replace "%TITLE%" %))})

(defn render [template values]
  (clo/render-resource
   template
   (merge common-features values)
   common-partials))

(defn index [db]
  (render "templates/index.mustache"
          {:conventions (get db :conventions)}))

(defn new-convention [db]
  (render "templates/new-convention.mustache" {}))

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
