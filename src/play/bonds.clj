(ns play.bonds)
(require '[clj-http.client :as http])
(def url-bonds "https://data912.com/live/arg_bonds")
(def url-cedears "https://data912.com/live/arg_cedears")

(defn bond-ratio! [prices a b]
  (/ (get-in prices [a :px_bid]) (get-in prices [b :px_ask])))

(defn bond-price! [prices symbol]
  (get-in prices [symbol :c]))

(defn prices-map! [url]
  (->> (http/get url {:as :json})
       :body
       (map (fn [b]
              [(:symbol b)
               {:px_bid (:px_bid b)
                :px_ask (:px_ask b)
                :c (:c b)}]))
       (into {})))

(def all-prices (conj (prices-map! url-bonds) (prices-map! url-cedears)))
