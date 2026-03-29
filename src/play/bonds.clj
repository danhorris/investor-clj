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

(def all-prices! (conj (prices-map! url-bonds) (prices-map! url-cedears)))

(defn historic-data-for! [& bonds]
  (->> bonds
       (mapcat (fn [bond]
                 (map #(assoc % :bond bond)
                      (historical-prices! bond))))))
(defn ratios [a b]
  (->> (historic-data-for! a b)
       (group-by :date)
       (map (fn [[date xs]]
              (let [prices (into {} (map (juxt :bond :c) xs))
                    a   (get prices a)
                    b   (get prices b)]
                {:date date
                 :ratio (/ a b)})))))


(comment
  (bond-ratio! all-prices! "AE38" "GD41")
  (historical-prices! "AE38")
  (historical-prices! "GD41")
  (historic-data-for! "AE38" "GD41")
  (ratios "AE38" "GD41")
  (ratios "AE38" "GD35")
  (sort-by :date (ratios "AE38" "GD41"))
  (group-by :date (historic-data-for! "AE38" "GD41"))
  (sort-by :date (historic-data-for! "AE38" "GD41")))




