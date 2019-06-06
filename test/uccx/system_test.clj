(ns uccx.system-tests
  (:require
   [clojure.test :refer [deftest is]]
   [uccx.system :as system]
   [com.stuartsierra.component :as component]
   [com.walmartlabs.lacinia :as lacinia]))

(defn ^:private test-system
  "Creates a new system suitable for testing, and ensures that
  the HTTP port won't conflict with a default running system."
  []
  (-> (system/new-system)
     (assoc-in [:server :port] 8989)))

(defn ^:private q
  "Extracts the compiled schema and executes a query."
  [system query variables]
  (-> system
     (get-in [:schema-provider :schema])
     (lacinia/execute query variables nil)))

(deftest can-read-board-game
  (let [system (component/start-system (test-system))
        results (q system
                   "{ queue_by_name(csqname: \"Dev\") { csqname callshandled callsabandoned totalcalls }}"
                   nil)]
    (is (= {:data {:game_by_id {:classhandled 2
                                :callsabandoned 2
                                :csqname "Zertz"
                                :totalcalls nil
                                }}}
           results))
    (component/stop-system system)))
