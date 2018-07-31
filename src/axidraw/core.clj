(ns axidraw.core
  (:require [serial.core :as s]
            [serial.util :as su]
            [quil.core :as q]
            [quil.middleware :as m]))

(def paper [210 297])
(def mul 3)
(def size (map #(* mul %) paper))

(defn create-initial-state []
  {:center
   {:x (/ (q/width) 2)
    :y (/ (q/height) 2)}
   :planets
   [[50 50 1 [255]]
    [40 20 12 [0 150 150]]
    [20 20 -24 [150 0 150]]
    [20 10 -50 [0 220 0]]
    [3 4 45 [150 10 10]]
    [15 10 -20 [0 0 220]]
    [5 20 -2 [10 150 10]]
    [4 8 (/ -1 16) [220 0 0]]
    [2 5 (/ 1 32) [10 10 150]]

    [3 4 45 [150 10 10]]
    [2 5 (/ 1 32) [10 10 150]]
    [5 20 -2 [10 150 10]]
    [4 8 (/ -1 16) [220 0 0]]

    [5 20 -2 [10 150 10]]
    [3 4 45 [150 10 10]]
    [2 5 (/ 1 32) [10 10 150]]
    [4 8 (/ -1 16) [220 0 0]]

    [2 5 (/ 1 32) [10 10 150]]
    [5 20 -2 [10 150 10]]
    [4 8 (/ -1 16) [220 0 0]]
    [3 4 45 [150 10 10]]]
   :t 0})

(defn setup []
  (q/frame-rate 512)
  (q/background 0)
  (create-initial-state))

(defn draw [{center :center planets :planets t :t}]
  (q/stroke 255)

  (loop [cx (:x center)
         cy (:y center)
         [a b s stroke] (first planets)
         moons (rest planets)]
    (let [mt (* t s)
          mx (+ cx (* a (Math/cos mt)))
          my (+ cy (* -1 b (Math/sin mt)))]
      (apply q/stroke stroke)
      (when (empty? moons)
        (q/point mx my))
      (if (not (empty? moons))
        (recur mx my (first moons) (rest moons))))))

(defn update-state [state]
  (update state :t + (/ (* 2 Math/PI) 10000)))

(q/defsketch exampl
  :title "Oh so many moons"
  :settings #(q/smooth 2)
  :setup setup
  :draw draw
  :update update-state
  :size size
  :features [:keep-on-top]
  :middleware [m/fun-mode])

; (def p "COM4")
; (def port (atom nil))
; (def buffer (atom []))
;
; (defn process-buffer []
;   (let [res (->> @buffer (butlast) (clojure.string/join ""))]
;     (println (str "Response: " res)))
;   (reset! buffer []))
;
; (defn receive [x]
;   (let [b (.read x)]
;     (if (= b 10)
;       (process-buffer)
;       (swap! buffer conj (char b)))))
;
; (defn open-port []
;   (reset! port (s/open p))
;   (s/listen! @port receive))
;
; (defn close-port []
;   (s/close @port)
;   (reset! port nil))
;
; (defn send-msg [msg]
;   (when-let [p @port]
;     (println "Sending: " (str msg))
;     (let [b (map (comp byte int) (str msg "\r"))]
;       (s/write @port b))))
;
; (def cord-m (map
;                (fn [[x y]] (clojure.string/replace
;                              (format "G1 X%.2f Y%.2f F10000" x y)
;                              "," "."))
;                [[50.0 0.0]
;                 [25.0 30.0]
;                 [50.0 60.0]
;                 [0.0 60.0]]))
;
; (defn drawM []
;   (open-port)
;   (send-msg "M3 S60")
;   (send-msg "G28")
;   (send-msg "M3 S0")
;   (doall (map send-msg cord-m))
;   (send-msg "M3 S60")
;   (send-msg "G28")
;   (close-port))
;
; (drawM)
