(defproject solver "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.optaplanner/optaplanner-core "6.2.0.Final" :exclusions [commons-io]]
                 [org.slf4j/slf4j-log4j12 "1.6.6"]
                 [clj-time "0.10.0"]
                 [midje "1.7.0" :exclusions [org.clojure/tools.namespace]]
                 [commons-collections/commons-collections "3.2.1"]]
  :source-paths ["src/clj"]
  :java-source-paths ["src/java"]
  :aot [schedule.solver.types]
  :target-path "target/%s"
  :cljfmt {:indents {chatty-checker [[:block 1]]}}
  :profiles {:uberjar {:aot :all}
             :dev {:dependencies []
                    :plugins [[lein-midje "3.1.3"]                       [lein-auto "0.1.2"]
                                    [lein-cljfmt "0.3.0"]]
                          :auto {:default {:file-pattern #"\.(clj|cljs|cljx|edn|java|drl)$"
                                           :paths ["src" "resources" "test"]}}}})                                           