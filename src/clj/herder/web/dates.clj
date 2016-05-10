(ns herder.web.dates
  (:require
   [clj-time.format :as f]
   [clj-time.coerce :as c]))

(def date-format
  (f/formatter "yyyy-MM-dd"))

(defn to-sql-date [from]
  (c/to-sql-date (f/parse date-format from)))
