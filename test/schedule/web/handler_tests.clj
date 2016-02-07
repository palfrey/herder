(ns schedule.web.handler-tests
  (:use [midje.sweet]
        [peridot.core])
  (:require [schedule.web.handler :refer [app routes]]
            [clj-leveldb]
            [clojure.data.json :as json]
            [clj-uuid :as uuid]))

(defn unpack [response]
  (hash-map
   :body (json/read-str (:body response) :key-fn keyword)
   :status (:status response)))

(fact
 (prerequisite (#'clj-leveldb/get anything :conventions) => [])

 (fact "list conventions"
       (-> (session (app routes))
           (request "/convention")
           :response
           unpack) => {:body {:conventions []}, :status 200})

 (fact "make new convention"
       (-> (session (app routes))
           (request "/convention/new"
                    :request-method :post
                    :params {:conventionName "stuff"
                             :from "dd"
                             :to "blah"})
           :response
           unpack) => {:body {:id "..uuid.."} :status 201}
       (provided
        (#'uuid/v1) => ..uuid..
        (#'clj-leveldb/put anything
                           :conventions ["..uuid.."]
                           "..uuid.." {:name "stuff" :from "dd" :to "blah"}) => nil)))
