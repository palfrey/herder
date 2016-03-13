(ns herder.web.handler-tests
  (:use
   [peridot.core])
  (:require
   [herder.web.handler :refer [app routes]]
   [herder.web.lobos :as lobos]
   [clojure.data.json :as json]
   [herder.web.db :as db]
   [korma.core :as kc]
   [korma.db :as kd]
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
   (f))
  (lc/close-global :korma-test-connection true))

(defn make-session []
  (session (app routes)))
