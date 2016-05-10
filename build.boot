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
                 [org.optaplanner/optaplanner-core "6.4.0.Final" :exclusions [commons-io commons-codec]]
                 [ring/ring-defaults "0.2.0"]
                 [org.clojure/tools.namespace "0.2.10"]
                 [org.danielsz/system "0.3.0-SNAPSHOT"]
                 [de.ubercode.clostache/clostache "1.4.0"]
                 [com.taoensso/sente "1.8.1"]

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
                 [postgresql "9.3-1102.jdbc41"]

                 [cljfmt "0.5.0"]
                 [me.raynes/fs "1.4.6"]
                 [juxt/dirwatch "0.2.3"]

                 [org.clojure/clojurescript "1.8.40"]
                 [reagent "0.6.0-alpha"]
                 [org.webjars/bootstrap "4.0.0-alpha.2"]
                 [deraen/boot-sass "0.2.1"]
                 [cljs-ajax "0.5.4"]
                 [cljsjs/jquery "2.2.2-0"]
                 [cljsjs/jquery-daterange-picker "0.0.8-0"]
                 [cljsjs/jquery-timepicker "1.8.10-0"]
                 [secretary "1.2.3"]
                 [com.andrewmcveigh/cljs-time "0.4.0"]

                 [adzerk/boot-cljs "1.7.228-1" :scope "test"]
                 [ajchemist/boot-figwheel "0.5.0-2"] ;; latest release
                 [com.cemerick/piggieback "0.2.1" :scope "test"]
                 [figwheel-sidecar "0.5.0-2" :scope "test"]
                 [org.clojure/tools.nrepl "0.2.12"]
                 [figwheel-sidecar/figwheel-sidecar "0.5.2"]])

(require '[adzerk.boot-test :refer [test]]
         '[adzerk.boot-cljs :refer [cljs]]
         '[reloaded.repl :refer [init start stop go reset]]
         '[environ.boot :refer [environ]]
         '[system.boot :refer [system run]]
         '[herder.systems :refer [dev-system prod-system]]
         '[boot-figwheel]
         '[deraen.boot-sass :refer [sass]])
(refer 'boot-figwheel :rename '{cljs-repl fw-cljs-repl})

(deftask build []
  (comp
   (javac)
   (aot :namespace '#{herder.solver.helpers})
   (aot :namespace '#{herder.solver.event herder.solver.person herder.solver.slot herder.solver.solution})))

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

(deftask make-solver []
  (comp
   (build)
   (target "target")))

(deftask testing
  []
  (set-env! :source-paths #(conj % "test"))
  identity)

(deftask tests []
  (comp
   (make-solver)
   (testing)
   (test)))

(task-options!
 figwheel {:build-ids  ["dev"]
           :all-builds [{:id "dev"
                         :compiler {:main 'herder.core
                                    :output-to "resources/public/js/herder.js"
                                    :output-dir "resources/public/js"
                                    :asset-path "/static/js"}
                         :figwheel {:build-id  "dev"
                                    :on-jsload 'herder.core/mount-root
                                    :heads-up-display true
                                    :autoload true
                                    :debug false}}]
           :figwheel-options {:repl true
                              :http-server-root "resources/public/"}})

(deftask run-figwheel []
  (with-pre-wrap [fs]
    (start-figwheel!)
    fs))

(deftask dev-clj []
  (comp
   (fix)
   (make-solver)
   (watch :verbose true)
   (testing)
   (test)))

(deftask kill-pods []
  (with-pre-wrap [fs]
    (doseq [pod (->> boot.pod/pods (map key))
            :let [name (.getName pod)
                  allowed ["worker" "core" "deraen.boot-sass" "boot.pod"]]]
      (if (not (.contains allowed name))
        (do
          (println "Killing " name)
          (boot.pod/destroy-pod pod))
        (println "Not killing " name)))
    fs))

(deftask dev []
  (comp
   (fix)
   (build)
   (environ :env {:http-port "3000"})
   (figwheel)
   (run-figwheel)
   (sift
    :add-jar {'cljsjs/jquery-daterange-picker #"^cljsjs/common/jquery-daterange-picker.inc.css$"
              'cljsjs/jquery-timepicker #"^cljsjs/common/jquery-timepicker.inc.css$"}
    :move {#"cljsjs/common/(jquery-daterange-picker.inc.css)" "resources/public/css/$1"
           #"cljsjs/common/(jquery-timepicker.inc.css)" "resources/public/css/$1"})
   (watch)
   (sass)
   (sift :move {#"herder/sass/(.*)" "resources/public/css/$1"})
   (build)
   (target :no-clean true)
   (system :sys #'dev-system :auto true :files ["lobos.clj" "handler.clj" "solver.clj" "systems.clj" "run.clj" "rules.drl"])
   (testing)
   (test)
   (kill-pods)))

(deftask prod []
  (comp
   (build)
   (cljs :ids #{"herder"})
   (sift
    :add-jar {'cljsjs/jquery-daterange-picker #"^cljsjs/common/jquery-daterange-picker.inc.css$"
              'cljsjs/jquery-timepicker #"^cljsjs/common/jquery-timepicker.inc.css$"}
    :move {#"cljsjs/common/(jquery-daterange-picker.inc.css)" "resources/public/css/$1"
           #"cljsjs/common/(jquery-timepicker.inc.css)" "resources/public/css/$1"
           #"herder.js" "resources/public/js/herder.js"
           #"herder.out/(.*)" "resources/public/js/herder.out/$1"})
   (sass)
   (sift :move {#"herder/sass/(.*)" "resources/public/css/$1"})
   (target :no-clean true)
   (run :main-namespace "herder.core" :arguments [#'prod-system])
   (wait)))

(deftask watch-tests []
  (comp
   (watch)
   (tests)))
