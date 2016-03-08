(set-env!
 :source-paths #{"src/java" "src/clj" "test" "build.boot"}
 :resource-paths #{"resources"}
 :format-paths #{"src/clj" "build.boot" "test"}
 :dependencies '[[adzerk/boot-test "1.1.0" :scope "test"]
                 [org.clojure/clojure "1.6.0"]
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
                 [lobos "1.0.0-beta3"]
                 [org.xerial/sqlite-jdbc "3.8.11.2"]
                 [korma "0.4.2"]

                 [http-kit "2.1.18"]
                 [de.ubercode.clostache/clostache "1.4.0"]
                 [bouncer "0.3.3"]
                 [solver "0.1.0-SNAPSHOT"]
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

(defn build []
  (javac)
  (aot :namespace '#{schedule.solver.types}))

(require '[cljfmt.core :refer [reformat-string]]
         '[clojure.java.io :as io]
         '[me.raynes.fs :as fs]
		'[juxt.dirwatch :refer [watch-dir close-watcher]])

(defn grep [re dir]
  (filter #(re-find re (str %)) (file-seq (io/file dir))))

(defn find-files [fname]
  (let [f (io/file fname)]
    (if (.isDirectory f)
      (apply concat
             (fs/walk
              (fn [root dirs files]
                (grep #"\.clj[sx]?$" root)) fname))
      [f])))

(defn reformat-str [s]
  (cljfmt.core/reformat-string s {}))

(deftask fix
  []
  (with-pre-wrap [fs]
    (let [files (:format-paths boot.pod/env)]
      (doseq [f     (mapcat find-files files)
              :let  [original (slurp f)
                     revised  (reformat-str original)]
              :when (not= original revised)]
        (println "Reformatting" f)
        (spit f revised)))
    fs))

(deftask solver []
  (comp
   (build)
   (target "target")))

(deftask tests []
  (comp
   (build)
   (test)))

(deftask fast-watch []
  (comp
   (solver)
   (watch)
   (test)))

(deftask watch-tests []
  (comp
   (watch)
   (tests)))
