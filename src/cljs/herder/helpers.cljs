(ns herder.helpers
  (:require
   [reagent.core :as r]
   [ajax.core :refer [GET POST DELETE]]
   [clojure.walk :refer [keywordize-keys]]))

(defonce state (r/atom {}))

(defn key-handler [key data]
  (.log js/console (str key) "Response" (pr-str data))
  (swap! state assoc key (keywordize-keys data)))

(defn get-data [key url & {:keys [refresh] :or {refresh false}}]
  (if (and (not refresh) (contains? @state key))
    (key @state)
    (do
      (GET url {:handler (partial key-handler key)})
      {})))
