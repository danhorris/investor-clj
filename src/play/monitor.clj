(ns play.monitor
  (:require
   [play.bonds :as bonds]
   [play.telegram :as telegram]))

(defn triggered? [alert prices]
  (case (:type alert)

    :ratio
    (> (bonds/bond-ratio! prices (:a alert) (:b alert))
       (:limit alert))

    :price-under
    (< (bonds/bond-price-value! prices (:symbol alert) :px_bid)
       (:limit alert))

    :price-above
    (> (bonds/bond-price-value! prices (:symbol alert) :px_ask)
       (:limit alert))

    false))

(defn alert->message [alert prices]
  (case (:type alert)

    :ratio
    (let [r (bonds/bond-ratio! prices (:a alert) (:b alert))]
      (format "🚨 %s ratio=%.3f" (:name alert) r))

    :price-under
    (let [p (bonds/bond-price-value! prices (:symbol alert) :px_bid)]
      (format "🚨 %s price=%.2f" (:symbol alert) p))

    :price-above
    (let [p (bonds/bond-price-value! prices (:symbol alert) :px_ask)]
      (format "🚨 %s price=%.2f" (:symbol alert) p))))

(defn process-alert! [prices alert]
  (when (triggered? alert prices)
    (telegram/send-telegram (alert->message alert prices))))


(defn monitor-all! [alerts interval-sec]
  (while true
    (let [prices bonds/all-prices!]
      (run! #(process-alert! prices %) alerts))
    (Thread/sleep (* interval-sec 1000))))


