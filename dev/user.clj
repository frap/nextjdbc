(ns user
  (:require
   [clojure.pprint :refer [pprint]]
   [clojure.test :refer [run-all-tests]]
   [clojure.reflect :refer [reflect]]
   [clojure.repl :refer [apropos dir doc find-doc pst source]]
   [com.stuartsierra.component :as component]
   [reloaded.repl :refer [system init start stop go reset reset-all]]
   [aero.core :as aero]
   [uccx.system :as system]
   [uccx.sql :as sql]
   [clojure.java.io :as io]
   [io.aviso.logging.setup]
   )
  )

(defn new-dev-system
  "Create a development system"
  []
  (component/system-using
   (system/new-system-map (system/config :dev))
   (system/new-dependency-map)))

;;(alter-var-root #'default-config assoc :color true :reporters ["documentation"])
;;(alter-var-root #'log/*logger-factory* (constantly (log-service/make-factory log-config)))

(reloaded.repl/set-init! new-dev-system)

(defn test-all []
  (run-all-tests #"uccx.*test$"))

(defn reset-and-test []
  (reset)
  (time (test-all)))
