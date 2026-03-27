(ns play.bonds)
(require '[clj-http.client :as http])
(def url-bonds "https://data912.com/live/arg_bonds")
(def url-cedears "https://data912.com/live/arg_cedears")

(defn bond-ratio! [prices a b]
  (/ (prices a) (prices b)))

(defn bond-price! [prices symbol]
  (prices symbol))

(defn prices-map! [url]
  (->> (http/get url {:as :json})
       :body
       (map (juxt :symbol :c))
       (into {})))

(def all-prices (conj (prices-map! url-bonds) (prices-map! url-cedears)))

