(ns smarthome.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [goog.events :as events]
              [goog.history.EventType :as EventType]
              [cljsjs.react :as react]
              [quil.core :as q :include-macros true]
              [smarthome.data :refer [state]])
    (:import goog.History))

(def my-color (atom 200))
(def bg (atom nil))

#_(defn simple-house []
  (js/background 245)
  (js/fill @my-color)
  (js/rect 50 50 150 150)
  (js/rect 200 50 150 150)
  (js/rect 200 200 150 150)
  (js/rect 50 200 150 150)
  (js/stroke @my-color)
  (js/line 100 200 150 200)
  (js/line 250 200 300 200)
  (js/line 200 100 200 150)
  (js/line 200 250 200 300))

#_(defn setup []
  (swap! bg assoc (js/loadImage "assets/snow.jpg"))
  (js/createCanvas 640 480))

#_(defn draw []
  #_(js/image @bg 0 0)
  #_(simple-house))

#_(doto js/window
  (aset "setup" setup)
  (aset "draw" draw))

(def color 50)

(defn home-page []
  [:div
   [:div.container
    [:canvas {:id "canvas"}]]])

#_(def circles []
  (q/stroke (q/random 255))             ;; Set the stroke colour to a random grey
  (q/stroke-weight (q/random 10))       ;; Set the stroke thickness randomly
  (q/fill (q/random 255))               ;; Set the fill colour to a random grey

  (let [diam (q/random 100)             ;; Set the diameter to a value between 0 and 100
        x    (q/random (q/width))       ;; Set the x coord randomly within the sketch
        y    (q/random (q/height))]     ;; Set the y coord randomly within the sketch
    (q/ellipse x y diam diam)))         ;; Draw a circle at x y with the correct diameter

(def img (atom nil))
(def size 200)

(defn draw []
  (q/image @img 0 0)
  (q/fill 0)
  (q/rect 200 200 size size))

(defn setup []
  (q/smooth)
  (q/frame-rate 1)
  (q/background 200)
  (reset! img (q/load-image "../assets/snow.jpg")))

(q/defsketch my-sketch
             :host "canvas"
             :setup setup
             :draw draw
             :size [640 480])

;; -------------------------
(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))

;; -------------------------
;; History
;; Must be called after routes have been defined.
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (hook-browser-navigation!)
  (mount-root))
