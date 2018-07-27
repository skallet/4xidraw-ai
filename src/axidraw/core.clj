(ns axidraw.core
  (:require [serial.core :as s]
            [serial.util :as su]))

(def p "COM4")
(def port (atom nil))
(def buffer (atom []))

(defn process-buffer []
  (let [res (->> @buffer (butlast) (clojure.string/join ""))]
    (println (str "Response: " res)))
  (reset! buffer []))

(defn receive [x]
  (let [b (.read x)]
    (if (= b 10)
      (process-buffer)
      (swap! buffer conj (char b)))))

(defn open-port []
  (reset! port (s/open p))
  (s/listen! @port receive))

(defn close-port []
  (s/close @port)
  (reset! port nil))

(defn send-msg [msg]
  (when-let [p @port]
    (println "Sending: " (str msg))
    (let [b (map (comp byte int) (str msg "\r"))]
      (s/write @port b))))

(def cord-m (map
               (fn [[x y]] (clojure.string/replace
                             (format "G1 X%.2f Y%.2f F10000" x y)
                             "," "."))
               [[50.0 0.0]
                [25.0 30.0]
                [50.0 60.0]
                [0.0 60.0]]))

(defn drawM []
  (open-port)
  (send-msg "M3 S60")
  (send-msg "G28")
  (send-msg "M3 S0")
  (doall (map send-msg cord-m))
  (send-msg "M3 S60")
  (send-msg "G28")
  (close-port))

(drawM)
