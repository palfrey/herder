(defproject schedule "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.optaplanner/optaplanner-core "6.2.0.Final"]
                 [org.slf4j/slf4j-log4j12 "1.6.6"]
                 [clj-time "0.10.0"]]
  :source-paths ["src/clj"]
  :java-source-paths ["src/java"]
  :aot [schedule.types]
  :main ^:skip-aot schedule.core
  :target-path "target/%s"
  :cljfmt {:indents {chatty-checker [[:block 1]]}}
  :profiles {:uberjar {:aot :all}
             :dev {:dependencies [[midje "1.7.0"]]
                   :plugins [[lein-midje "3.1.3"]
                             [lein-auto "0.1.2"]
                             [lein-cljfmt "0.3.0"]]
                   :auto {:default {:file-pattern #"\.(clj|cljs|cljx|edn|java)$"}}}})
