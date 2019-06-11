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
   [uccx.db :as db]
   [clojure.java.io :as io]
   [io.pedestal.log :as log]
   [io.aviso.logging.setup]
   )
  )


#_(def hrpool (db/hikaricp (:uccx_rt (system/config :dev))))
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
                             (->  (system/new-system :dev)
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


;; Assuming require [clojure.tools.logging :as log]
;;(Thread/setDefaultUncaughtExceptionHandler
;; (reify Thread$UncaughtExceptionHandler
;;   (uncaughtException [_ thread ex]
;;    (log/error ex "Uncaught exception on" (.getName thread)))))
