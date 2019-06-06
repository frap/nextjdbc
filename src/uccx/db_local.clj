(ns uccx.db-local
  (:require
   [clojure.edn :as edn]
   [clojure.java.io :as io]
   [com.stuartsierra.component :as component]))

(defrecord UccxStatsLocalDb [data]

  component/Lifecycle

  (start [this]
    (assoc this :data (-> (io/resource "rtstats.edn")
                         slurp
                         edn/read-string
                         atom)))

  (stop [this]
    (assoc this :data nil)))

(defn new-db
  []
  {:db (map->UccxStatsLocalDb {})})


(defn find-queue-by-name
  [db queue-name]
  (->> db
       :data
       deref
       :csqname
       (filter #(= queue-name (:csqname %)))
       first))
