(ns smarthome.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [goog.events :as events]
              [goog.history.EventType :as EventType]
              [cljsjs.react :as react]
              #_[smarthome.data :refer [state]])
    (:import goog.History))

;(set! (.-className (.-body js/document)) "clear")
;(set! (.-className (.-body js/document)) "cloudy")
;(set! (.-className (.-body js/document)) "rain")
;(set! (.-className (.-body js/document)) "snow")

(def initial-state
  {:rooms         [{:name           :kitchen
                    :stove          {:active? false, :temp nil}
                    :oven           {:active? false, :temp nil}
                    :coffee-machine {:active? false}
                    :lights         0
                    :temperature    {:current 20, :set-to 20}}
                   {:name        :livingroom
                    :lights      0
                    :temperature {:current 20, :set-to 20}
                    :tv          :off}
                   {:name        :bathroom
                    :door        :closed
                    :lights      0
                    :temperature {:current 20, :set-to 20}
                    :radio       :off}
                   {:name        :hall
                    :lights      0
                    :temperature {:current 20, :set-to 20}}
                   {:name        :bedroom
                    :door        :closed
                    :lights      0
                    :temperature {:current 20, :set-to 20}}
                   {:name        :garage
                    :door        :closed
                    :lights      0
                    :temperature {:current 20, :set-to 20}}
                   {:name        :hall
                    :door        :closed
                    :lights      0
                    :temperature {:current 20, :set-to 20}}]
   :time          nil
   :time-of-day   :morning                                  ;morning/day/evening/night
   :weather       :clear                                    ;clear/cloudy/rain/snow
   :user-location {:indoor :kitchen, :coordinates [63.433639 10.392072]}
   :user-profile  {}})

(def state (atom initial-state))

(def dish-on false)
(def oven-on true)
(def stove-on true)
(def tv-on true)
(def radio-on false)
(def wash-on true)
(def car-in true)
(def bath-door-open false)
(def bed-door-open false)
(def front-door-open false)
(def livingroom-lights-off false)
(def bathroom-lights-off true)
(def bedroom-lights-off true)
(def garage-lights-off false)
(def garage-door-closed true)

;; -------------------------
(defn home-page []
  [:div.container
   [:div.line
    ; LIVINGROOM-NW
    [:div {:className (str "room" (when livingroom-lights-off " dark"))}
     [:img.room-image {:src "../assets/livingroom-nw.jpg"}]]
    ; LIVINGROOM-NE
    [:div {:className (str "room" (when livingroom-lights-off " dark"))}
     [:img.room-image {:src "../assets/livingroom-ne.jpg"}]]
    ; KITCHEN
    [:div {:className (str "room" (when livingroom-lights-off " dark"))}
     [:img.room-image {:src "../assets/kitchen.jpg"}]
     [:img.fridge {:src "../assets/fridge.jpg"}]
     (if dish-on [:img.dish {:src "../assets/dish-on.jpg"}]
                 [:img.dish {:src "../assets/dish-off.jpg"}])
     [:img.oven {:src "../assets/oven-off.jpg"}]
     [:img.stove {:src "../assets/stove-off.jpg"}]]]
   [:div.line
    ; LIVINGROOM-SW
    [:div {:className (str "room" (when livingroom-lights-off " dark"))}
     [:img.room-image {:src "../assets/livingroom-sw.jpg"}]
     [:img.tv {:src "../assets/tv-on.jpg"}]]
    ; LIVINGROOM-SE
    [:div {:className (str "room" (when livingroom-lights-off " dark"))}
     [:img.room-image {:src "../assets/livingroom-se.jpg"}]
     (if radio-on [:img.radio-off {:src "../assets/radio-off.jpg"}]
                  [:img.radio-on {:src "../assets/radio-on.jpg"}])]
    ; BATHROOM
    [:div {:className (str "room" (cond (and bath-door-open bathroom-lights-off) " dim"
                                        bathroom-lights-off " dark"))}
     [:img.room-image {:src "../assets/bathroom.jpg"}]
     [:img {:className (str "bathroom-door" (if bath-door-open "-open" "-closed"))
            :src "../assets/door.jpg"}]
     [:img.laundry {:src "../assets/laundry.jpg"}]
     [:img.wash {:src "../assets/wash-on.jpg"}]]]
   [:div.line
    ; GARAGE
    [:div {:className (str "room" (when garage-lights-off " dark"))}
     [:img.room-image {:src "../assets/garage.jpg"}]
     (when car-in [:img.car {:src "../assets/car.jpg"}])
     (when garage-door-closed [:img {:className "garage-port" :src "../assets/garageport.jpg"}])]
    ; HALL
    [:div {:className (str "room" (when livingroom-lights-off " dark"))}
     [:img.room-image {:src "../assets/hall.jpg"}]
     (if front-door-open [:img.front-door {:src "../assets/front-door-closed.jpg"}]
                         [:img.front-door {:src "../assets/front-door-open.jpg"}])]
    ; BEDROOM
    [:div {:className (str "room" (cond (and bed-door-open bedroom-lights-off) " dim"
                                        bedroom-lights-off " dark"))}
     [:img.room-image {:src "../assets/bedroom.jpg"}]
     [:img {:className (str "bedroom-door" (if bed-door-open "-open" "-closed"))
            :src "../assets/door.jpg"}]]]])
(defn kitchen-page []
  [:div.container
   [:div.line
    ; KITCHEN
    [:div {:className (str "room" (when livingroom-lights-off " dark"))}
     [:img.room-image {:src "../assets/kitchen.jpg"}]
     [:img.fridge {:src "../assets/fridge.jpg"}]
     (if dish-on [:img.dish {:src "../assets/dish-on.jpg"}]
                 [:img.dish {:src "../assets/dish-off.jpg"}])
     [:img.oven {:src "../assets/oven-off.jpg"}]
     [:img.stove {:src "../assets/stove-off.jpg"}]]]])

#_(defn livingroom-page []
  [:div.container
   [:div.row [:a {:href "#/"} "home"]]
   [:div.row
    [:div [:h2 "LIVINGROOM"]]]])

#_(defn bedroom-page []
  [:div.container
   [:div.row [:a {:href "#/"} "home"]]
   [:div.row
    [:div [:h2 "BEDROOM"]]]])

#_(defn hall-page []
  [:div.container
   [:div.row [:a {:href "#/"} "home"]]
   [:div.row
    [:div [:h2 "HALL"]]]])

#_(defn bathroom-page []
  [:div.container
   [:div.row [:a {:href "#/"} "home"]]
   [:div.row
    [:div [:h2 "BATHROOM"]]]])

#_(defn garage-page []
  [:div.container
   [:div.row [:a {:href "#/"} "home"]]
   [:div.row
    [:div [:h2 "GARAGE"]]]])

(session/put! :current-page #'home-page)

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))
(secretary/defroute "/kitchen" []
                    (session/put! :current-page #'kitchen-page))
#_(secretary/defroute "/livingroom" []
                    (session/put! :current-page #'livingroom-page))
#_(secretary/defroute "/bedroom" []
                    (session/put! :current-page #'bedroom-page))
#_(secretary/defroute "/hall" []
                    (session/put! :current-page #'hall-page))
#_(secretary/defroute "/bathroom" []
                    (session/put! :current-page #'bathroom-page))
#_(secretary/defroute "/garage" []
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