(ns schedule.web.handler
  (:require
   [clostache.parser :as clo]
   [compojure.route :as route]
   [compojure.handler :as handler]
   [compojure.core :refer [defroutes GET POST context] :as compojure]
   [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
   [reloaded.repl :refer [system]]
   [bouncer.core :as b]
   [bouncer.validators :as v]
   [ring.util.response :refer [redirect]]
   [clj-leveldb :as leveldb]
   [ring.util.anti-forgery :refer [anti-forgery-field]]))

(def common-partials {:footer (clo/render-resource "templates/footer.mustache")})

(def common-features 
  {:header #(-> (clo/render-resource "templates/header.mustache") (clojure.string/replace "%TITLE%" %))})

(defn render [template values]
  (clo/render-resource
   template
   (merge common-features values {:anti-forgery (anti-forgery-field)})
   common-partials))

(defn index [{{:keys [connection]} :db}]
  (let [conventions (leveldb/get connection :conventions)]
    (render "templates/index.mustache"
            {:conventions (map #(assoc (leveldb/get connection %) :id %) conventions)})))

(defn validate-new-convention [params]
  (first
   (b/validate
    params
    :conventionName [[v/required :message "Need a name for the convention"]])))

(defn new-convention [{:keys [flash]}]
  (render "templates/new-convention.mustache" {:errors (:errors flash)}))

(defn save-new-convention! [{{:keys [connection]} :db :keys [:params]}]
  (if-let [errors (validate-new-convention params)]
    (-> (redirect "/convention/new")
        (assoc :flash (assoc params :errors errors)))
    (let [id (java.util.UUID/randomUUID)
          existingConventions (leveldb/get connection :conventions)
          daterange (clojure.string/split (:daterange params) #"-")]
      (leveldb/put connection
                   :conventions (conj existingConventions id)
                   id {:name (:conventionName params) :from (first daterange) :to (second daterange)})
      (redirect "/"))))

(defn show-convention [{{:keys [connection]} :db {:keys [id]} :params}]
  (let [convention (leveldb/get connection (java.util.UUID/fromString id))]
    (println convention)
    (println id)
    (render "templates/show-convention.mustache" convention)))

(defroutes convention-routes
  (context "/convention" []
    (GET "/new" [] new-convention)
    (POST "/new" [] save-new-convention!)
    (GET ["/:id", :id #"[\w]{8}(-[\w]{4}){3}-[\w]{12}"] [id] show-convention)))

(defroutes core-routes
  (GET "/" [] index)
  (route/resources "/")
  (route/not-found "Not found"))

(def routes (compojure/routes convention-routes core-routes))

(defn app [routes]
  (-> routes
      (wrap-defaults site-defaults)))
