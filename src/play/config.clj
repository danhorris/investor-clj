(ns play.config
  (:require
   [clojure.edn :as edn]
   [clojure.java.io :as io]))

(defn load-edn [resource-name]
  (-> resource-name
      io/resource
      slurp
      edn/read-string))