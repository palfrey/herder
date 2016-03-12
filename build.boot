(set-env!
 :source-paths #{"src/java" "src/clj" "test" "build.boot"}
 :resource-paths #{"resources"}
 :format-paths #{"src/clj" "build.boot" "test"}
 :format-regex #"\.(?:clj[sx]?|boot)$"
 :dependencies '[[adzerk/boot-test "1.1.0" :scope "test"]
                 [org.clojure/clojure "1.8.0"]
                 [commons-io "2.4"]

                 [org.slf4j/slf4j-log4j12 "1.6.6"]
                 [clj-time "0.10.0"]
                 [commons-collections/commons-collections "3.2.1"]

                 [compojure "1.4.0"]
                 [ring "1.4.0"]
                 [org.optaplanner/optaplanner-core "6.2.0.Final" :exclusions [commons-io]]
                 [ring/ring-defaults "0.1.2"]
                 [org.clojure/tools.namespace "0.2.10"]
                 [org.danielsz/system "0.1.9" :exclusions [org.clojure/tools.namespace]]
                 [environ "1.0.0"]
                 [lobos "1.0.0-beta3" :exclude [org.clojure/java.jdbc]]
                 [org.xerial/sqlite-jdbc "3.8.11.2"]
                 [org.clojure/java.jdbc "0.3.7"]
                 [korma "0.4.2"]

                 [http-kit "2.1.18"]
                 [de.ubercode.clostache/clostache "1.4.0"]
                 [bouncer "0.3.3"]
                 [ring/ring-json "0.4.0"]
                 [org.clojure/data.json "0.2.6"]
                 [danlentz/clj-uuid "0.1.6"]

                 [ring/ring-mock "0.3.0" :scope "test"]
                 [midje "1.8.3" :scope "test"]
                 [peridot "0.4.3" :scope "test"]
                 [com.h2database/h2 "1.3.172"]

                 [cljfmt "0.4.1"]
                 [me.raynes/fs "1.4.6"]
                 [juxt/dirwatch "0.2.3"]])

(require '[adzerk.boot-test :refer :all])

(deftask build []
  (comp
   (javac)
   (aot :namespace '#{schedule.solver.types})))

(require '[cljfmt.core :refer [reformat-string]]
         '[clojure.java.io :as io]
         '[me.raynes.fs :as fs]
         '[juxt.dirwatch :refer [watch-dir close-watcher]]
         '[clojure.string :as str])

(defn reformat-str [s]
  (cljfmt.core/reformat-string s {}))

(defn fix-path [{:keys [file action]}]
  (let [path (.getAbsolutePath file)
        matches-path (-> boot.pod/env :format-paths ((partial filter #(str/includes? path %))) empty? not)
        matches-regex (-> boot.pod/env :format-regex (#(re-find % path)) nil? not)]
    (if (and matches-path matches-regex)
      (let  [original (slurp file)
             revised  (reformat-str original)]
        (if (not= original revised)
          (spit file revised))))))

(defonce watcher (atom nil))

(deftask fix
  []
  (with-pre-wrap [fs]
    (if-let [old-watcher @watcher]
      (close-watcher old-watcher))
    (reset! watcher (watch-dir fix-path (io/file ".")))
    fs))

(deftask solver []
  (comp
   (build)
   (target "target")))

(deftask tests []
  (comp
   (solver)
   (test)))

(deftask dev []
  (comp
   (fix)
   (solver)
   (watch :verbose true)
   (test)))

(deftask watch-tests []
  (comp
   (watch)
   (tests)))
