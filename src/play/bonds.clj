(ns play.bonds
  (:require
   [clj-http.client :as http]))

(def url-bonds "https://data912.com/live/arg_bonds")
(def url-cedears "https://data912.com/live/arg_cedears")
(def url-bonds-historic "https://data912.com/historical/bonds")


(defn bond-price-value! [prices symbol attr]
  (get-in prices [symbol attr]))

(defn bond-ratio! [prices a b]
  (/ (bond-price-value! prices a :px_bid) (bond-price-value! prices b :px_ask)))

(defn prices-map! [url]
  (->> (http/get url {:as :json})
       :body
       (map (fn [b]
              [(:symbol b)
               {:px_bid (:px_bid b)
                :px_ask (:px_ask b)
                :c (:c b)}]))
       (into {})))

(defn historical-prices! [bond]
  (->> (http/get (str url-bonds-historic "/" bond) {:as :json})
       :body))

(def all-prices (conj (prices-map! url-bonds) (prices-map! url-cedears)))

(def all-data-historic
  (concat
   (map #(assoc % :bond "AE38") (historical-prices! "AE38"))
   (map #(assoc % :bond "GD41") (historical-prices! "GD41"))))


(comment
  (bond-ratio! all-prices "AE38" "GD41")
  (historical-prices! "AE38")
  (historical-prices! "GD41")
  (all-data-historic))



