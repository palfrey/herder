(ns herder.state
  (:require
   [reagent.core :as r]))

(defonce state (r/atom {}))
