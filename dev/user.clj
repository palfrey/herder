(ns user
  (:require 
   [reloaded.repl :refer [system init start stop go reset]]
   [schedule.web.systems :refer [dev-system]]
   [clojure.tools.namespace.repl :refer [set-refresh-dirs]]))

(clojure.tools.namespace.repl/set-refresh-dirs "src/clj")
(reloaded.repl/set-init! dev-system)
; type (go) in the repl to start your development-time system.
