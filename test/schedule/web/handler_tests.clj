(ns schedule.web.handler-tests
  (:use [midje.sweet]
        [peridot.core])
  (:require [schedule.web.handler :refer [app routes]]
            [schedule.web.lobos :as lobos]
            [clojure.data.json :as json]
            [schedule.web.db :as db]
            [clj-uuid :as uuid]
            [korma.core :as kc]
            [korma.db :as kd]
            [lobos.migration :as lm]
            [lobos.connectivity :as lc]))

(defn unpack [response]
  (try
    (hash-map
     :body (json/read-str (:body response) :key-fn keyword)
     :status (:status response))
    (catch Exception ex
      (println response)
      response)))

(defn with-test-db []
  (let [db-details (kd/h2 {:db "mem:test_mem"})]
    (lc/close-global :korma-test-connection true)
    (lc/open-global :korma-test-connection db-details)

    (kd/defdb db db-details)
    (kc/exec-raw ["drop all objects"])
    (lc/with-connection :korma-test-connection
      (lobos/add-conventions-table))))

(against-background
 [(before :contents (with-test-db))
  (around :contents (kd/transaction ?form))]
 (fact "list conventions"
       (-> (session (app routes))
           (request "/convention")
           :response
           unpack) => {:body {:conventions []}, :status 200})

 (defn make-convention [arg & {:keys [from to] :or {from "2016-01-01" to "2016-01-02"}}]
   (request arg "/convention/new"
            :request-method :post
            :params {:conventionName "stuff"
                     :from from
                     :to to}))

 (fact "make new convention fail"
       (-> (session (app routes))
           (make-convention :from "foo" :to "bar")
           :response
           unpack) => {:status 400
                       :body {:errors {:from ["from must be a valid date"]
                                       :to ["to must be a valid date"]}}})

 (fact "make new convention"
       (let [uuid (uuid/v1)]
         (-> (session (app routes))
             make-convention
             :response
             unpack) => {:body {:id (str uuid)} :status 201}
         (provided
          (#'uuid/v1) => uuid)))

 (fact "get convention"
       (let [uuid "1652d4d3-9a88-4feb-a01b-5c1855742747"]
         (kc/insert db/conventions
                    (kc/values {:id uuid
                                :from "2016-01-01"}))
         (-> (session (app routes))
             (request (str "/convention/" uuid))
             :response
             unpack) => {:body {:id uuid :from "2016-01-01T00:00:00Z" :to nil :name nil} :status 200})))
