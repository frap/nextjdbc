(ns user
  (:require
   [uccx.schema :as s]
   [com.walmartlabs.lacinia :as lacinia]
   [com.walmartlabs.lacinia.pedestal :as lp]
   [io.pedestal.http :as http]
   [clojure.java.browse :refer [browse-url]]
   [clojure.pprint :refer [pprint]]
   [clojure.test :refer [run-all-tests]]
   [clojure.reflect :refer [reflect]]
   [clojure.repl :refer [apropos dir doc find-doc pst source]]
   [com.stuartsierra.component :as component]
   ;; [reloaded.repl :refer [system init start stop go reset reset-all]]
   [aero.core :as aero]
   [uccx.system :as system]
   [uccx.sql :as sql]
   [clojure.java.io :as io]
   [io.pedestal.log :as log]
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

;;(reloaded.repl/set-init! new-dev-system)

;; if code for namespace is reloaded the system Var will maintain its value
(defonce system nil)
;;(defonce system (system/new-system))

(defn q
  [query-string]
  (-> system
     :schema-provider
     :schema
     (lacinia/execute query-string nil nil))
  )

(defn start
  []
  (alter-var-root #'system (fn [_]
                             (-> :dev (system/new-system)
                                component/start-system)))
  (browse-url "http://localhost:8888/")
  :started)

(defn stop
  []
  (when (some? system)
    (component/stop-system system)
    (alter-var-root #'system (constantly nil)))
  :stopped)

(comment
  (start)
  (stop)
  )


(defn test-all []
  (run-all-tests #"uccx.*test$"))

(defn reset-and-test []
;;  (reset)
  (time (test-all)))
