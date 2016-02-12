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
   [clj-leveldb :as leveldb]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [clj-uuid :as uuid]
   [clj-time.format :as f]))

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
    :conventionName [[v/required :message "Need a name for the convention"]]
    :from [[v/datetime (f/formatter "yyyy-MM-dd")]]
    :to [[v/datetime (f/formatter "yyyy-MM-dd")]])))

(defn new-convention [{:keys [flash]}]
  (render "templates/new-convention.mustache" {:errors (:errors flash)}))

(defn save-new-convention! [{{:keys [connection]} :db :keys [:params]}]
  (if-let [errors (validate-new-convention params)]
    (-> (response {:errors errors})
        (status 400))
    (let [id (str (uuid/v1))
          existingConventions (leveldb/get connection :conventions)]
      (leveldb/put connection
                   :conventions (conj existingConventions id)
                   id {:name (:conventionName params) :from (:from params) :to (:to params)})
      (status (response {:id id}) 201))))

(defn show-convention [{{:keys [connection]} :db {:keys [id]} :params}]
  (let [convention (leveldb/get connection (java.util.UUID/fromString id))]
    (println convention)
    (println id)
    (render "templates/show-convention.mustache" convention)))

(defn edit-convention [])

(defn list-conventions [{{:keys [connection]} :db}]
  (let [conventions (leveldb/get connection :conventions)]
    (response {:conventions (map #(assoc (leveldb/get connection %) :id %) conventions)})))

(def uuid-regex #"[\w]{8}(-[\w]{4}){3}-[\w]{12}")

(defroutes convention-routes
  (context "/convention" []
    (GET "/" [] list-conventions)
    (POST "/new" [] save-new-convention!)
    (GET ["/:id", :id uuid-regex] [id] show-convention)
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
