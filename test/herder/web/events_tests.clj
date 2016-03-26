(ns herder.web.events-tests
  (:use
   [clojure.test]
   [peridot.core])
  (:require
   [herder.web.db :as db]
   [clj-uuid :as uuid]
   [korma.core :as kc]

   [herder.web.handler-tests :refer [make-session unpack db-test-fixture]]
   [herder.web.conventions-tests :refer [insert-convention]]
   [herder.web.persons-tests :refer [insert-person]]))

(use-fixtures :each db-test-fixture)

(defn insert-event [con_uuid event_uuid & person_uuids]
  (kc/insert db/events
             (kc/values {:id event_uuid
                         :convention_id con_uuid
                         :name "Foo"}))
  (doseq [person person_uuids]
    (kc/insert db/events-persons
               (kc/values {:event_id event_uuid
                           :person_id person}))))

(let [con_uuid (uuid/v1)
      str_con_uuid (str con_uuid)
      person_uuid (uuid/v1)]
  (deftest MakeNewEventFail
    (is (=
         {:body {:errors
                 {:name ["Must have a name"]}}
          :status 400}
         (-> (make-session)
             (request (str "/api/convention/" str_con_uuid "/event")
                      :request-method :post
                      :params {:name ""})
             :response
             unpack))))

  (deftest MakeNewEvent
    (insert-convention con_uuid)
    (let [event_uuid (uuid/v1)]
      (insert-person con_uuid person_uuid)
      (with-redefs [uuid/v1 (fn [] event_uuid)]
        (is (=
             {:body {:id (str event_uuid)} :status 201}
             (-> (make-session)
                 (request (str "/api/convention/" str_con_uuid "/event")
                          :request-method :post
                          :params {:name "Foo"
                                   :persons [(str person_uuid)]})
                 :response
                 unpack))))
      (is (= {:id event_uuid
              :name "Foo"
              :convention_id con_uuid}
             (first (kc/select db/events (kc/where {:id event_uuid})))))

      (is (= {:event_id event_uuid
              :person_id person_uuid}
             (first (kc/select db/events-persons (kc/where {:event_id event_uuid})))))

      (is (= {:body {}
              :status 200}
             (->
              (make-session)
              (request (str "/api/convention/" str_con_uuid "/event/" (str event_uuid))
                       :request-method :delete)
              :response
              unpack)))))

  (deftest MakeNewEventWithNoPerson
    (insert-convention con_uuid)
    (let [event_uuid (uuid/v1)]
      (with-redefs [uuid/v1 (fn [] event_uuid)]
        (is (=
             {:body {:id (str event_uuid)} :status 201}
             (-> (make-session)
                 (request (str "/api/convention/" str_con_uuid "/event")
                          :request-method :post
                          :params {:name "Foo"
                                   :persons []})
                 :response
                 unpack))))
      (is (= {:body
              {:id (str event_uuid)
               :name "Foo"
               :convention_id (str con_uuid)
               :persons []}
              :status 200}
             (->
              (make-session)
              (request (str "/api/convention/" str_con_uuid "/event/" (str event_uuid)))
              :response
              unpack)))))

  (deftest get-event
    (let [event_uuid (uuid/v1)]
      (insert-convention con_uuid)
      (insert-person con_uuid person_uuid)
      (insert-event con_uuid event_uuid person_uuid)
      (is (=
           {:body
            {:id (str event_uuid)
             :convention_id str_con_uuid
             :name "Foo"
             :persons [(str person_uuid)]}
            :status 200}
           (->
            (make-session)
            (request (str "/api/convention/" str_con_uuid "/event/" (str event_uuid)))
            :response
            unpack))))))
