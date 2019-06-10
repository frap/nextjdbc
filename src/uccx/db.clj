(ns uccx.db
  (:require [com.stuartsierra.component :as component]
            [hikari-cp.core :as hikari]
            [clojure.java.jdbc :as jdbc]
            [io.pedestal.log :as log]
            [clojure.string :as str]))

(def ORACLE-SPEC {:username      "ATEA_UAW"
                  :password      "password"
                  :database-name "xepdb1"
                  :server-name   "localhost"
                  :port-number   1521
                  :driverType    "thin" 
                  :adapter       "oracle"})

(def DEFAULT-H2-MEM-SPEC
  {:adapter "h2"
   :url     "jdbc:h2:mem:"
   :user     "sa"
   :password ""})

;; db-spec format: https://github.com/tomekw/hikari-cp#configuration-options
(defrecord Hikari [db-spec]
  component/Lifecycle
  (start [component]
    (let [s (hikari/make-datasource db-spec)]
      (assoc component :datasource s)))
  (stop [component]
    (when-let [s (:datasource component)]
      (try
        (hikari/close-datasource s)
        (catch Exception e
          (log/warn e "Error while stopping uccx db"))))
    (assoc component :datasource nil)))

(defn new-hikari-cp [db-spec]
  (map->Hikari db-spec))


(defn ^:private query
  [component statement]
  (let [[sql & params] statement]
    (log/debug :sql (str/replace sql #"\s+" " ")
               :params params))
  (jdbc/query (:ds component) statement))

