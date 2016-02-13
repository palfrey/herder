(ns schedule.web.handler
  (:require
   [clostache.parser :as clo]
   [compojure.route :as route]
   [compojure.handler :as handler]
   [compojure.core :refer [defroutes GET POST context] :as compojure]
   [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
   [reloaded.repl :refer [system]]
   [bouncer.core :as b]
   [bouncer.validators :as v]
   [ring.util.response :refer [redirect response status]]
   [ring.middleware.json :refer [wrap-json-response]]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [clj-uuid :as uuid]
   [clj-time.format :as f]
   [korma.core :as d]
   [schedule.web.db :as db]))

(def common-partials {:footer (clo/render-resource "templates/footer.mustache")})

(def common-features
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

(defn validate-new-convention [params]
  (first
   (b/validate
    params
    :conventionName [[v/required :message "Need a name for the convention"]]
    :from [[v/datetime (f/formatter "yyyy-MM-dd")]]
    :to [[v/datetime (f/formatter "yyyy-MM-dd")]])))

(defn new-convention [{:keys [flash]}]
  (render "templates/new-convention.mustache" {:errors (:errors flash)}))

(defn save-new-convention! [{:keys [:params]}]
  (if-let [errors (validate-new-convention params)]
    (-> (response {:errors errors})
        (status 400))
    (let [id (str (uuid/v1))
          existingConventions (d/select db/conventions)]
      (d/insert
       db/conventions (d/values [{:id id :name (:conventionName params) :from (:from params) :to (:to params)}]))
      (status (response {:id id}) 201))))

(defn get-convention [{{:keys [id]} :params}]
  (let [convention (d/select db/conventions (d/where {:id id}))]
    (response (first convention))))

(defn edit-convention [])

(defn list-conventions [params]
  (let [conventions (d/select db/conventions)]
    (response {:conventions conventions})))

(def uuid-regex #"[\w]{8}(-[\w]{4}){3}-[\w]{12}")

(defroutes convention-routes
  (context "/convention" []
    (GET "/" [] list-conventions)
    (POST "/new" [] save-new-convention!)
    (GET ["/:id", :id uuid-regex] [id] get-convention)
    (POST ["/:id", :id uuid-regex] [id] edit-convention)))

(defroutes core-routes
  (GET "/" [] index)
  (route/resources "/")
  (route/not-found "Not found"))

(def routes (compojure/routes convention-routes core-routes))

(defn app [routes]
  (-> routes
      (wrap-defaults api-defaults)
      wrap-json-response))
