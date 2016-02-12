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
        (#'uuid/v1) => ..uuid..
        (#'clj-leveldb/put anything
                           :conventions ["..uuid.."]
                           "..uuid.." {:name "stuff" :from "2016-01-01" :to "2016-01-02"}) => nil)))
