(ns play.core
  (:require
   [play.monitor :as monitor]
   [play.config :as config]))

(def alerts (config/load-edn "alerts.edn"))
(def interval (config/load-edn "interval-sec.edn"))

(defn -main []
  (println "Starting monitor with alerts:" alerts "and interval:" interval "seconds")
  (monitor/monitor-all! alerts (:interval-sec interval)))


