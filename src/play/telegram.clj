(ns play.telegram
  (:require [clj-http.client :as http]))

(def telegram-token "8744872293:AAH3ZEhtfGJU5aWDXuLv9uBPnDYs1rYTQ0A")
(def chat-id "897726568")

(defn send-telegram [text]
  (http/post
   (str "https://api.telegram.org/bot" telegram-token "/sendMessage")
   {:form-params {:chat_id chat-id
                  :text text}}))
