(set-env!
 :source-paths #{"src/java" "src/clj" "src/cljs"}
 :resource-paths #{"resources"}
 :format-paths #{"src/clj" "build.boot" "test"}
 :format-regex #"\.(?:clj[sx]?|boot)$"
 :dependencies '[[adzerk/boot-test "1.1.1" :scope "test"]
                 [org.clojure/clojure "1.8.0"]
                 [commons-io "2.4"]

                 [org.slf4j/slf4j-log4j12 "1.7.18"]
                 [clj-time "0.11.0"]
                 [commons-collections/commons-collections "3.2.2"]

                 [compojure "1.5.0"]
                 [ring "1.4.0"]
                 [org.optaplanner/optaplanner-core "6.3.0.Final" :exclusions [commons-io commons-codec]]
                 [ring/ring-defaults "0.2.0"]
                 [org.clojure/tools.namespace "0.2.10"]
                 [org.danielsz/system "0.3.0-SNAPSHOT"]

                 [environ "1.0.2"]
                 [boot-environ "1.0.2"]

                 [org.clojure/java.jdbc "0.4.2"]
                 [lobos "1.0.0-beta3" :exclude [org.clojure/java.jdbc]]
                 [org.xerial/sqlite-jdbc "3.8.11.2"]
                 [korma "0.4.2"]

                 [http-kit "2.1.19"]
                 [bouncer "1.0.0"]
                 [ring/ring-json "0.4.0"]
                 [org.clojure/data.json "0.2.6"]
                 [danlentz/clj-uuid "0.1.6"]

                 [ring/ring-mock "0.3.0" :scope "test"]
                 [peridot "0.4.3" :scope "test"]
                 [com.h2database/h2 "1.4.191"]

                 [cljfmt "0.4.1"]
                 [me.raynes/fs "1.4.6"]
                 [juxt/dirwatch "0.2.3"]

                 [org.clojure/clojurescript "1.7.228"]
                 [reagent "0.6.0-alpha"]
                 [adzerk/boot-cljs "1.7.228-1" :scope "test"]
                 [ajchemist/boot-figwheel "0.5.0-2"] ;; latest release
                 [com.cemerick/piggieback "0.2.1" :scope "test"]
                 [figwheel-sidecar "0.5.0-2" :scope "test"]
                 [org.clojure/tools.nrepl "0.2.12"]
                 [figwheel-sidecar/figwheel-sidecar "0.5.0-6"]])

(require '[adzerk.boot-test :refer [test]]
         '[adzerk.boot-cljs :refer [cljs]]
         '[reloaded.repl :refer [init start stop go reset]]
         '[environ.boot :refer [environ]]
         '[system.boot :refer [system run]]
         '[herder.systems :refer [dev-system]]
         '[boot-figwheel])
(refer 'boot-figwheel :rename '{cljs-repl fw-cljs-repl})

(deftask build []
  (comp
   (javac)
   (aot :namespace '#{herder.solver.types})))

(require '[cljfmt.core :refer [reformat-string]]
         '[clojure.java.io :as io]
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

(deftask testing
  []
  (set-env! :source-paths #(conj % "test"))
  identity)

(deftask tests []
  (comp
   (solver)
   (testing)
   (test)))

(deftask dev-clj []
  (comp
   (fix)
   (solver)
   (watch :verbose true)
   (testing)
   (test)))

(task-options!
 figwheel {:build-ids  ["dev"]
           :all-builds [{:id "dev"
                         :compiler {:main 'herder.core
                                    :output-to "resources/public/herder.js"
                                    :output-dir "resources/public"}
                         :figwheel {:build-id  "dev"
                                    :on-jsload 'herder.core/run
                                    :heads-up-display true
                                    :autoload true
                                    :debug false}}]
           :figwheel-options {:repl true
                              :http-server-root "/"
                              :css-dirs ["target"]}})

(deftask run-figwheel []
  (with-pre-wrap [fs]
    (start-figwheel!)
    fs))

(deftask dev-cljs []
  (comp
   (fix)
   (environ :env {:http-port "3000"})
   (repl)
   (figwheel)
   (run-figwheel)
   (watch)
   (system :sys #'dev-system :auto true :files ["lobos.clj" "handler.clj"])))

(deftask watch-tests []
  (comp
   (watch)
   (tests)))
