(ns schedule.web.handler-tests
  (:use
   [clojure.test]
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

(defn db-test-fixture [f]
  (with-test-db)
  (kd/transaction
   (f)))

(use-fixtures :each db-test-fixture)

(deftest ListConventions
  (is (= (-> (session (app routes))
             (request "/convention")
             :response
             unpack) {:body {:conventions []}, :status 200})))

(defn make-convention [arg & {:keys [from to] :or {from "2016-01-01" to "2016-01-02"}}]
  (request arg "/convention"
           :request-method :post
           :params {:conventionName "stuff"
                    :from from
                    :to to}))

(deftest MakeNewConventionFail
  (is (= (-> (session (app routes))
             (make-convention :from "foo" :to "bar")
             :response
             unpack) {:status 400
                      :body {:errors {:from ["from must be a valid date"]
                                      :to ["to must be a valid date"]}}})))

(deftest MakeNewConvention
  (let [uuid (uuid/v1)]
    (with-redefs [uuid/v1 (fn [] uuid)]
      (is (= (-> (session (app routes))
                 make-convention
                 :response
                 unpack) {:body {:id (str uuid)} :status 201})))
    (is (= (first (kc/select db/conventions (kc/where {:id uuid}))) {:name "stuff"
                                                                     :id uuid
                                                                     :from #inst "2016-01-01T00:00:00.000-00:00"
                                                                     :to #inst "2016-01-02T00:00:00.000-00:00"}))))

(deftest GetConvention
  (let [uuid "1652d4d3-9a88-4feb-a01b-5c1855742747"]
    (kc/insert db/conventions
               (kc/values {:id uuid
                           :from "2016-01-01"}))
    (is (= (-> (session (app routes))
               (request (str "/convention/" uuid))
               :response
               unpack) {:body {:id uuid :from "2016-01-01T00:00:00Z" :to nil :name nil} :status 200}))))
