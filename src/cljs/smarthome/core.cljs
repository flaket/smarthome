(ns smarthome.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [goog.events :as events]
              [goog.history.EventType :as EventType]
              [cljsjs.react :as react]
              [smarthome.data :refer [state]])
    (:import goog.History))

;(set! (.-className (.-body js/document)) "winter")
;(set! (.-className (.-body js/document)) "spring")
(set! (.-className (.-body js/document)) "summer")
;(set! (.-className (.-body js/document)) "autumn")

(def my-state (atom state))

;; -------------------------
(defn home-page []
  [:div.container
   [:div.line
    [:div.room
     [:img.bedroom {:src "../assets/bedroom.jpg"}]
     [:img.small {:src "../assets/bedroom.jpg"}]]
    [:div.room
     [:img.bedroom {:src "../assets/bedroom.jpg"}]]
    [:div.room
     [:img.bedroom {:src "../assets/bedroom.jpg"}]]]
   [:div.line
    [:div.room
     [:img.bedroom {:src "../assets/bedroom.jpg"}]]
    [:div.room
     [:img.bedroom {:src "../assets/bedroom.jpg"}]]
    [:div.room
     [:img.bedroom {:src "../assets/bedroom.jpg"}]]]
   [:div.line
    [:div.room
     [:img.bedroom {:src "../assets/bedroom.jpg"}]]
    [:div.room
     [:img.bedroom {:src "../assets/bedroom.jpg"}]]
    [:div.room
     [:img.bedroom {:src "../assets/bedroom.jpg"}]]]])

(defn kitchen-page []
  [:div.container
   [:div.line
    [:div.room
     [:img {:src "../assets/bedroom.jpg"}]]]])

(defn livingroom-page []
  [:div.container
   [:div.row [:a {:href "#/"} "home"]]
   [:div.row
    [:div [:h2 "LIVINGROOM"]]]])

(defn bedroom-page []
  [:div.container
   [:div.row [:a {:href "#/"} "home"]]
   [:div.row
    [:div [:h2 "BEDROOM"]]]])

(defn hall-page []
  [:div.container
   [:div.row [:a {:href "#/"} "home"]]
   [:div.row
    [:div [:h2 "HALL"]]]])

(defn bathroom-page []
  [:div.container
   [:div.row [:a {:href "#/"} "home"]]
   [:div.row
    [:div [:h2 "BATHROOM"]]]])

(defn garage-page []
  [:div.container
   [:div.row [:a {:href "#/"} "home"]]
   [:div.row
    [:div [:h2 "GARAGE"]]]])

#_(session/put! :current-page #'kitchen-page)

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))
(secretary/defroute "/kitchen" []
                    (session/put! :current-page #'kitchen-page))
(secretary/defroute "/livingroom" []
                    (session/put! :current-page #'livingroom-page))
(secretary/defroute "/bedroom" []
                    (session/put! :current-page #'bedroom-page))
(secretary/defroute "/hall" []
                    (session/put! :current-page #'hall-page))
(secretary/defroute "/bathroom" []
                    (session/put! :current-page #'bathroom-page))
(secretary/defroute "/garage" []
                    (session/put! :current-page #'garage-page))


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
(defn current-page []
  [:div [(session/get :current-page)]])

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (hook-browser-navigation!)
  (mount-root))