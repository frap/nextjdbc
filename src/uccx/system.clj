;; Copyright © 2018, Red Elvis.
(ns uccx.system
  (:require
   [aero.core :as aero]
   [clojure.java.io :as io]
   [com.stuartsierra.component :refer [system-map system-using]]
   [duct.component.hikaricp :refer [hikaricp]]
   )
  )


(defn config
  "Read EDN config, with the given profile. See Aero docs at
  https://github.com/juxt/aero for details."
  [profile]
  (aero/read-config (io/resource "config.edn") {:profile profile}))


(defn new-system-map
  "Create the system. See https://github.com/stuartsierra/component"
  [config]
  (system-map
   :db-hr       (hikaricp    (:uccx_hr config))
   :db-rt       (hikaricp    (:uccx_rt config))
   ;;  :uccx-stats  (new-localdb)
   ))

(defn new-dependency-map
  "Declare the dependency relationships between components. See
  https://github.com/stuartsierra/component"
  []
  {})

(defn new-system
  "Construct a new system, configured with the given profile"
  [profile]
  (let [config (config profile)]
    (-> (new-system-map config)
        (system-using (new-dependency-map)))))
