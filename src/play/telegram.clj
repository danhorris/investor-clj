(ns play.telegram
  (:require [clj-http.client :as http]))

(def telegram-token (System/getenv "TELEGRAM_TOKEN"))
(def chat-id (System/getenv "CHAT_ID"))


(defn send-telegram [text]
  (http/post
   (str "https://api.telegram.org/bot" telegram-token "/sendMessage")
   {:form-params {:chat_id chat-id
                  :text text}}))

