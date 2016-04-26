(ns herder.web.persons-tests
  (:use
   [clojure.test]
   [peridot.core])
  (:require
   [herder.web.db :as db]
   [clj-uuid :as uuid]
   [korma.core :as kc]

   [herder.web.handler-tests :refer [make-session unpack db-test-fixture]]
   [herder.web.conventions-tests :refer [insert-convention]]))

(use-fixtures :each db-test-fixture)

(defn insert-person [con_uuid person_uuid]
  (kc/insert db/persons
             (kc/values {:id person_uuid
                         :convention_id con_uuid
                         :name "Foo"})))

(let [con_uuid (uuid/v1)
      str_con_uuid (str con_uuid)]
  (deftest MakeNewPersonFail
    (is (=
         {:body {:errors
                 {:name ["Must have a name"]}}
          :status 400}
         (-> (make-session)
             (request (str "/api/convention/" str_con_uuid "/person")
                      :request-method :post
                      :params {:name ""})
             :response
             unpack))))

  (deftest MakeNewPerson
    (insert-convention con_uuid)
    (let [person_uuid (uuid/v1)]
      (with-redefs [uuid/v1 (fn [] person_uuid)]
        (is (=
             {:body {:id (str person_uuid)} :status 201}
             (-> (make-session)
                 (request (str "/api/convention/" str_con_uuid "/person")
                          :request-method :post
                          :params {:name "Foo"})
                 :response
                 unpack))))
      (is (= {:id person_uuid
              :name "Foo"
              :convention_id con_uuid}
             (first (kc/select db/persons (kc/where {:id person_uuid})))))))

  (deftest get-person
    (let [person_uuid (uuid/v1)]
      (insert-convention con_uuid)
      (insert-person con_uuid person_uuid)
      (is (=
           {:body
            {:id (str person_uuid)
             :convention_id str_con_uuid
             :name "Foo"
             :events []
             :non-availability []}
            :status 200}
           (->
            (make-session)
            (request (str "/api/convention/" str_con_uuid "/person/" (str person_uuid)))
            :response
            unpack))))))
