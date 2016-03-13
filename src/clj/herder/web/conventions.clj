(ns herder.web.conventions
  (:require
   [bouncer.core :as b]
   [bouncer.validators :as v]
   [clj-uuid :as uuid]
   [clj-time.format :as f]
   [korma.core :as d]
   [ring.util.response :refer [response status]]
   [compojure.core :refer [defroutes GET POST PUT context]]

   [herder.web.db :as db]
   [herder.web.slots :as slots]))

(defn validate-new-convention [params]
  (first
   (b/validate
    params
    :conventionName [[v/required :message "Need a name for the convention"]]
    :from [[v/datetime (f/formatter "yyyy-MM-dd")]]
    :to [[v/datetime (f/formatter "yyyy-MM-dd")]])))

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

(def uuid-regex #"[\w]{8}(-[\w]{4}){3}-[\w]{12}")

(defroutes convention-routes
  (context "/convention" []
    (GET "/" [] list-conventions)
    (POST "/" [] save-new-convention!)
	      ;(context ["/:id" :id uuid-regex] [id]) - FIXME
    (context "/:id" [id]
      (GET "/" [id] get-convention)
      (PUT "/" [id] edit-convention)
      slots/slot-context)))
