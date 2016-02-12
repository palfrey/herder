(ns schedule.web.handler-tests
  (:use [midje.sweet]
        [peridot.core])
  (:require [schedule.web.handler :refer [app routes]]
            [clj-leveldb]
            [clojure.data.json :as json]
            [clj-uuid :as uuid]))

(defn unpack [response]
  (try
    (hash-map
     :body (json/read-str (:body response) :key-fn keyword)
     :status (:status response))
    (catch NullPointerException ex
      (println response)
      response)))

(fact

 (fact "list conventions"
       (-> (session (app routes))
           (request "/convention")
           :response
           unpack) => {:body {:conventions []}, :status 200}
       (provided (#'clj-leveldb/get anything :conventions) => []))

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
       (-> (session (app routes))
           make-convention
           :response
           unpack) => {:body {:id "..uuid.."} :status 201}
       (provided
        (#'clj-leveldb/get anything :conventions) => []
        (#'uuid/v1) => ..uuid..
        (#'clj-leveldb/put anything
                           :conventions ["..uuid.."]
                           "..uuid.." {:name "stuff" :from "2016-01-01" :to "2016-01-02"}) => nil))

 (fact "get convention"
       (-> (session (app routes))
           (request "/convention/1652d4d3-9a88-4feb-a01b-5c1855742747")
           :response
           unpack) => {:body {:foo "bar"} :status 200}
       (provided
        (#'clj-leveldb/get anything "1652d4d3-9a88-4feb-a01b-5c1855742747") => {:foo :bar})))
