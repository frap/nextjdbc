(ns uccx.db
  (:require [com.stuartsierra.component :as component]
            [hikari-cp.core :as hikari]
            [clojure.java.jdbc :as jdbc]
            [io.pedestal.log :as log]
            [clojure.string :as str])
  (:import [com.zaxxer.hikari HikariConfig HikariDataSource]) )


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


(defn- make-config
  [{:keys [uri driver-class-name username password auto-commit? conn-timeout
           idle-timeout max-lifetime conn-test-query min-idle max-pool-size pool-name
           register-mbeans? metric-registry]}]
  (let [cfg (HikariConfig.)]
    (when uri                  (.setJdbcUrl cfg uri))
    (when driver-class-name    (.setDriverClassName cfg driver-class-name))
    (when username             (.setUsername cfg username))
    (when password             (.setPassword cfg password))
    (when (some? auto-commit?) (.setAutoCommit cfg auto-commit?))
    (when conn-timeout         (.setConnectionTimeout cfg conn-timeout))
    (when idle-timeout         (.setIdleTimeout cfg idle-timeout))
    (when max-lifetime         (.setMaxLifetime cfg max-lifetime))
    (when max-pool-size        (.setMaximumPoolSize cfg max-pool-size))
    (when metric-registry      (.setMetricRegistry cfg metric-registry))
    (when min-idle             (.setMinimumIdle cfg min-idle))
    (when pool-name            (.setPoolName cfg pool-name))
    (when register-mbeans?     (.setRegisterMbeans cfg register-mbeans?))
    cfg))

(defn- make-spec [component]
  {:datasource (HikariDataSource. (make-config component))})


(defrecord HikariCP [uri]
  component/Lifecycle
  (start [component]
    (if (:spec component)
      component
      (assoc component :spec (make-spec component))))
  (stop [component]
    (if-let [spec (:spec component)]
      (do
        (try
          (.close (:datasource spec))
          (catch Exception e
            (log/warn e "Error while stopping HikariCP")))
        (dissoc component :spec))
      component)))

(defn hikaricp [options]
 ;; {:pre [(:uri options)]}
  (map->HikariCP options))


(defn ^:private query
  [component statement]
  (let [[sql & params] statement]
    (log/debug :sql (str/replace sql #"\s+" " ")
               :params params))
  (jdbc/query (:ds component) statement))
