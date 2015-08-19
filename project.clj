(defproject schedule "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.optaplanner/optaplanner-core "6.2.0.Final"]
                 [org.slf4j/slf4j-log4j12 "1.6.6"]
                 [clj-time "0.10.0"]
                 [commons-collections/commons-collections "3.2.1"]
                 [ring "1.3.1"]
                 [ring/ring-defaults "0.1.2"]
                 [compojure "1.2.0"]
                 [org.danielsz/system "0.1.1"]
                 [environ "1.0.0"]
                 [de.ubercode.clostache/clostache "1.4.0"]
                 [factual/clj-leveldb "0.1.1"]]
  :plugins [[lein-environ "1.0.0"]]                 
  :source-paths ["src/clj"]
  :java-source-paths ["src/java"]
  :aot [schedule.solver.types]
  :main ^:skip-aot schedule.web.core
  :target-path "target/%s"
  :cljfmt {:indents {chatty-checker [[:block 1]]}}
  :profiles {:uberjar {:aot :all}
             :prod {:env {:http-port 8000
                          :repl-port 8001}
                    :dependencies [[org.clojure/tools.nrepl "0.2.5"]]}  
             :test {:dependencies [[midje "1.7.0"]]
                    :plugins [[lein-midje "3.1.3"]]}
             :dev [:test {:source-paths ["dev"]
                          :env {:http-port 3000}
                          :plugins [[lein-auto "0.1.2"]
                                    [lein-cljfmt "0.3.0"]]
                          :auto {:default {:file-pattern #"\.(clj|cljs|cljx|edn|java|drl)$"
                                           :paths ["src" "resources" "test"]}}}]})