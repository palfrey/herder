(defproject schedule "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
  	[org.clojure/clojure "1.6.0"]
  	[org.optaplanner/optaplanner-core "6.2.0.Final"]
   ]
  :main ^:skip-aot schedule.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
