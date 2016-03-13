(defproject herder "0.1.0-SNAPSHOT"
  :plugins [[lein-environ "1.0.0"]
            [lein-cljfmt "0.4.1"]
            [lein-auto "0.1.2"]]
  :aliases {"format" ["auto" "do" ["cljfmt" "fix" "project.clj" "build.boot" "src" "test"]]}
  :auto {:default {:paths ["."]
                   :file-pattern #"\.(clj|cljs|cljx|edn|boot)$"}}
  :cljfmt {:indents {chatty-checker [[:block 1]]}}
  :profiles {:prod {:env {:http-port 8000
                          :repl-port 8001}}
             :dev [:test {:source-paths ["dev"]
                          :env {:http-port 3000}}]})
