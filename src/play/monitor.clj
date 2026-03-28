(ns play.monitor
  (:require
   [play.bonds :as bonds]
   [play.telegram :as telegram]))

(defn triggered? [alert prices]
  (case (:type alert)

    :ratio
    (> (bonds/bond-ratio! prices (:a alert) (:b alert))
       (:limit alert))

    :price
    (< (bonds/bond-price! prices (:symbol alert))
       (:limit alert))
    false))

(defn alert->message [alert prices]
  (case (:type alert)

    :ratio
    (let [r (bonds/bond-ratio! prices (:a alert) (:b alert))]
      (format "🚨 %s ratio=%.3f" (:name alert) r))

    :price
    (let [p (bonds/bond-price! prices (:symbol alert))]
      (format "🚨 %s price=%.2f" (:symbol alert) p))))

(defn process-alert! [prices alert]
  (when (triggered? alert prices)
    (telegram/send-telegram (alert->message alert prices))))


(defn monitor-all! [alerts interval-sec]
  (while true
    (let [prices bonds/all-prices]
      (run! #(process-alert! prices %) alerts))
    (Thread/sleep (* interval-sec 1000))))


