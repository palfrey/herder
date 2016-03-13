(ns herder.web.conventions-tests
  (:use
   [clojure.test]
   [peridot.core])
  (:require
   [herder.web.handler :refer [app routes]]
   [herder.web.db :as db]
   [clj-uuid :as uuid]
   [korma.core :as kc]

   [herder.web.handler-tests :refer [make-session unpack db-test-fixture]]))

(use-fixtures :each db-test-fixture)

(deftest ListConventions
  (is (= (-> (make-session)
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
  (is (= (-> (make-session)
             (make-convention :from "foo" :to "bar")
             :response
             unpack) {:status 400
                      :body {:errors {:from ["from must be a valid date"]
                                      :to ["to must be a valid date"]}}})))

(deftest MakeNewConvention
  (let [uuid (uuid/v1)]
    (with-redefs [uuid/v1 (fn [] uuid)]
      (is (= (-> (make-session)
                 make-convention
                 :response
                 unpack) {:body {:id (str uuid)} :status 201})))
    (is (= (first (kc/select db/conventions (kc/where {:id uuid})))
           {:name "stuff"
            :id uuid
            :from #inst "2016-01-01T00:00:00.000-00:00"
            :to #inst "2016-01-02T00:00:00.000-00:00"}))))

(defn insert-convention [uuid]
  (kc/insert db/conventions
             (kc/values {:id uuid
                         :from "2016-01-01"})))

(let [con_uuid (uuid/v1)
      str_con_uuid (str con_uuid)]
  (deftest GetConvention
    (insert-convention con_uuid)
    (is (= (-> (make-session)
               (request (str "/convention/" str_con_uuid))
               :response
               unpack)
           {:body
            {:id str_con_uuid :from "2016-01-01T00:00:00Z" :to nil :name nil}
            :status 200}))))
