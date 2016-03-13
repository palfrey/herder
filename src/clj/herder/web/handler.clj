(ns herder.web.handler
  (:require
   [clostache.parser :as clo]
   [compojure.route :as route]
   [compojure.handler :as handler]
   [compojure.core :refer [defroutes GET POST PUT context] :as compojure]
   [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
   [reloaded.repl :refer [system]]
   [bouncer.core :as b]
   [bouncer.validators :as v]
   [ring.util.response :refer [redirect response status]]
   [ring.middleware.json :refer [wrap-json-response]]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [clj-uuid :as uuid]
   [clj-time.core :as t]
   [clj-time.format :as f]
   [korma.core :as d]
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
    (let [id (str (uuid/v1))]
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

(def time-format (f/formatter "hh:mm"))

(defn validate-new-slot [params]
  (first
   (b/validate
    params
    :start [[v/datetime time-format :message "start must be a valid time"]]
    :end [[v/datetime time-format :message "end must be a valid time"]])))

(defn minutes [time]
  (let [when (f/parse time-format time)]
    (+ (t/minute when) (* 60 (t/hour when)))))

(defn new-slot! [{:keys [:params]}]
  (if-let [errors (validate-new-slot params)]
    (-> (response {:errors errors})
        (status 400))
    (let [id (str (uuid/v1))]
      (d/insert
       db/slots (d/values [{:id id
                            :start-minutes (-> params :start minutes)
                            :end-minutes (-> params :end minutes)
                            :convention_id (:id params)}]))
      (status (response {:id id}) 201))))

(def uuid-regex #"[\w]{8}(-[\w]{4}){3}-[\w]{12}")

(defroutes convention-routes
  (context "/convention" []
    (GET "/" [] list-conventions)
    (POST "/" [] save-new-convention!)
    ;(context ["/:id" :id uuid-regex] [id]) - FIXME
    (context "/:id" [id]
      (GET "/" [id] get-convention)
      (PUT "/" [id] edit-convention)
      (context "/slot" []
        (POST "/" [] new-slot!)))))

(defroutes core-routes
  (GET "/" [] index)
  (route/resources "/")
  (route/not-found "Not found"))

(def routes (compojure/routes convention-routes core-routes))

(defn app [routes]
  (-> routes
      (wrap-defaults api-defaults)
      wrap-json-response))
