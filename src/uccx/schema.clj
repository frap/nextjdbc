(ns uccx.schema
  "Contains custom resolvers and a function to provide the full schema."
  (:require
   [clojure.java.io :as io]
   [com.walmartlabs.lacinia.util :as util]
   [com.walmartlabs.lacinia.schema :as schema]
   [com.stuartsierra.component :as component]
   [clojure.edn :as edn]))

(defn resolve-queue-by-name
  [queues-map context args value]
  (let [{:keys [csqname]} args]
    (get queues-map csqname)))


(defn resolver-map
  [component]
  (let [queue-data (-> (io/resource "rtstats.edn")
                      slurp
                      edn/read-string)
        queue-map (->> queue-data
                       :queues
                       (reduce #(assoc %1 (:csqname %2) %2) {}))]
    {:query/queue-by-name (partial resolve-queue-by-name queue-map)}))

(defn load-schema
  [component]
  (-> (io/resource "uccx-queues-schema.edn")
     slurp
     edn/read-string
     (util/attach-resolvers (resolver-map component))
     schema/compile))


(defrecord SchemaProvider [schema]

  component/Lifecycle

  (start [this]
    (assoc this :schema (load-schema this)))

  (stop [this]
    (assoc this :schema nil)))

(defn new-schema-provider
  []
  {:schema-provider (map->SchemaProvider {})})
