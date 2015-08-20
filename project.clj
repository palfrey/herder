(defproject schedule "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.slf4j/slf4j-log4j12 "1.6.6"]
                 [compojure "1.4.0"]
                 [ring "1.4.0"]
                 [ring/ring-defaults "0.1.2"]
                 [org.clojure/tools.namespace "0.2.10"]
                 [org.danielsz/system "0.1.9" :exclusions [org.clojure/tools.namespace]]
                 [environ "1.0.0"]
                 [de.ubercode.clostache/clostache "1.4.0"]
                 [factual/clj-leveldb "0.1.1"]
                 [solver "0.1.0-SNAPSHOT"]]
  :plugins [[lein-environ "1.0.0"]]
  :main ^:skip-aot schedule.web.core
  :target-path "target/%s"
  :cljfmt {:indents {chatty-checker [[:block 1]]}}
  :profiles {:uberjar {:aot :all}
             :prod {:env {:http-port 8000
                          :repl-port 8001}
                    :dependencies [[org.clojure/tools.nrepl "0.2.5"]]}  
             :dev [:test {:source-paths ["dev"]
                          :env {:http-port 3000}
                          :plugins [[lein-auto "0.1.2"]
                                    [lein-cljfmt "0.3.0"]]}]})