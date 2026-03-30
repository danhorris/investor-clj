(ns play.bonds
  (:require
   [clj-http.client :as http]
   [clojure.set :refer [difference]]))

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
       ;;(sort-by :date)
       (group-by :date)
       (map (fn [[date xs]]
              (let [prices (into {} (map (juxt :bond :c) xs))
                    price-a   (get prices a)
                    price-b   (get prices b)]
                (when (and price-a price-b (not (zero? price-b)))
                  {:date date
                   :ratio (/ price-a price-b)}))))))


(comment
  (bond-ratio! all-prices! "AE38" "GD41")
  (historical-prices! "AE38")
  (historical-prices! "GD41")
  (count (historical-prices! "AE38"))
  (count (historical-prices! "GD41"))
  (->> (historical-prices! "AE38")
       (map :date)
       (into #{}))
  (ratios "AE38" "GD41")
  (last (ratios "AE38" "GD41"))
  (ratios "AE38" "GD35")
  (sort-by :date (ratios "AE38" "GD41"))
  (group-by :date (historic-data-for! "AE38" "GD41"))
  (sort-by :date (historic-data-for! "AE38" "GD41")))


(def dates-AE38 (->> (historical-prices! "AE38")
                     (map :date)
                     (sort-by :date)
                     (into #{})))

(def dates-GD41 (->> (historical-prices! "GD41")
                     (map :date)
                     (sort-by :date)
                     (into #{})))


(first dates-AE38)
(first dates-GD41)
(difference dates-AE38 dates-GD41)
;;falta ordenar la lista de ratios, posiblemente hay algun valor nulo

