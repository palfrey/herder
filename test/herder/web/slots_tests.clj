(ns herder.web.slots-tests
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

(let [con_uuid (uuid/v1)
      str_con_uuid (str con_uuid)]
  (deftest MakeNewSlotFail
    (is (=
         {:body {:errors
                 {:start ["start must be a valid time"],
                  :end ["end must be a valid time"]}}
          :status 400}
         (-> (make-session)
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
             (-> (make-session)
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
