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
      (lobos/make-tables))))

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
    (is (= (-> (session (app routes))
               (request (str "/convention/" str_con_uuid))
               :response
               unpack)
           {:body
            {:id str_con_uuid :from "2016-01-01T00:00:00Z" :to nil :name nil}
            :status 200})))

  (deftest MakeNewSlotFail
    (is (=
         {:body {:errors
                 {:start ["start must be a valid time"],
                  :end ["end must be a valid time"]}}
          :status 400}
         (-> (session (app routes))
             (request (str "/convention/" str_con_uuid "/slot")
                      :request-method :post
                      :params {:start "garble"
                               :end "garble"})
             :response
             unpack))))

  (deftest MakeNewSlot
    (insert-convention con_uuid)
    (let [slot_uuid (uuid/v1)]
      (with-redefs [uuid/v1 (fn [] slot_uuid)]
        (is (=
             {:body {:id (str slot_uuid)} :status 201}
             (-> (session (app routes))
                 (request (str "/convention/" str_con_uuid "/slot")
                          :request-method :post
                          :params {:start "10:05"
                                   :end "11:00"})
                 :response
                 unpack))))
      (is (= {:id slot_uuid
              :start-minutes 605
              :end-minutes 660
              :convention_id con_uuid}
             (first (kc/select db/slots (kc/where {:id slot_uuid}))))))))
