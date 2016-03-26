(ns herder.web.persons
  (:require
   [bouncer.core :as b]
   [bouncer.validators :as v]
   [clj-uuid :as uuid]
   [korma.core :as d]
   [herder.web.db :as db]
   [ring.util.response :refer [response status]]
   [compojure.core :refer [GET POST PUT DELETE context]]))

(defn validate-new-person [params]
  (first
   (b/validate
    params
    :name [[v/required :message "Must have a name"]])))

(defn new-person! [{:keys [:params]}]
  (if-let [errors (validate-new-person params)]
    (-> (response {:errors errors})
        (status 400))
    (let [id (str (uuid/v1))]
      (d/insert
       db/persons (d/values [{:id id
                              :name (:name params)
                              :convention_id (:id params)}]))
      (status (response {:id id}) 201))))

(defn get-person [{{:keys [id]} :params}]
  (let [person (first (d/select db/persons (d/where {:id id})))]
    (if (nil? person)
      (status (response (str "No such person " id)) 404)
      (response person))))

(defn get-persons [{{:keys [id]} :params}]
  (let [persons (d/select db/persons (d/where {:convention_id id}) (d/order :name))]
    (response persons)))

(defn delete-person [{{:keys [id]} :params}]
  (let [person (d/delete db/persons (d/where {:id id}))]
    (status (response {}) (if (> person 0) 200 404))))

(def uuid-regex #"[\w]{8}(-[\w]{4}){3}-[\w]{12}")

(def person-context
  (context "/person" []
    (GET "/" [] get-persons)
    (GET ["/:id" :id uuid-regex] [id] get-person)
    (DELETE ["/:id" :id uuid-regex] [id] delete-person)
    (POST "/" [] new-person!)))
