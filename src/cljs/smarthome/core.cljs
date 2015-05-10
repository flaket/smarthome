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

(def imgs {:living-nw "../assets/livingroom-nw.jpg"
           :living-ne "../assets/livingroom-ne.jpg"
           :living-sw "../assets/livingroom-sw.jpg"
           :living-se "../assets/livingroom-se.jpg"
           :kitchen "../assets/kitchen.jpg"
           :bathroom "../assets/bathroom.jpg"
           :garage "../assets/garage.jpg"
           :hall "../assets/hall.jpg"
           :bedroom "../assets/bedroom.jpg"
           :fridge "../assets/fridge.jpg"
           :dish-on "../assets/dish-on.jpg"
           :dish-off "../assets/dish-off.jpg"
           :oven-on "../assets/oven-on.jpg"
           :oven-off "../assets/oven-off.jpg"
           :stove-on "../assets/stove-on.jpg"
           :stove-off "../assets/stove-off.jpg"
           :tv-on "../assets/tv-on.gif"
           :tv-off "../assets/tv-off.jpg"
           :radio-on "../assets/radio-on.gif"
           :radio-off "../assets/radio-off.jpg"
           :door "../assets/door.jpg"
           :laundry "../assets/laundry.jpg"
           :wash-on "../assets/wash-on.jpg"
           :car "../assets/car.jpg"
           :garage-port "../assets/garageport.jpg"
           :frontdoor-closed "../assets/front-door-closed.jpg"
           :frontdoor-open "../assets/front-door-open.jpg"
           :lightswitch-living "../assets/lightswitch-living.jpg"
           :lightswitch-garage "../assets/lightswitch-garage.jpg"
           :lightswitch-bath "../assets/lightswitch-bath.jpg"
           :lightswitch-bed "../assets/lightswitch-bed.jpg"})

(def dish-on true)
(def oven-on true)
(def stove-on true)
(def tv-on (atom true))
(def radio-on (atom true))
(def wash-on true)
(def car-in true)
(def bath-door-open true)
(def bed-door-open true)
(def front-door-open (atom true))
(def livingroom-lights-off (atom false))
(def bathroom-lights-off (atom false))
(def bedroom-lights-off (atom false))
(def garage-lights-off (atom false))
(def garage-door-closed true)

(def day true)

;; -------------------------
(defn home-page []
  [:div.container
   [:div.line
    ; LIVINGROOM-NW
    [:div {:className (str "room" (when @livingroom-lights-off " dark"))}
     [:img.room-image {:src (:living-nw imgs)}]]
    ; LIVINGROOM-NE
    [:div {:className (str "room" (when @livingroom-lights-off " dark"))}
     [:img.room-image {:src (:living-ne imgs)}]
     [:img.lightswitch-living {:src (:lightswitch-living imgs)
                        :on-click #(if @livingroom-lights-off
                                    (reset! livingroom-lights-off false)
                                    (reset! livingroom-lights-off true))}]]
    ; KITCHEN
    [:div {:className (str "room" (when @livingroom-lights-off " dark"))}
     [:img.room-image {:src (:kitchen imgs)}]
     [:img.fridge {:src (:fridge imgs)}]
     (if dish-on [:img.dish {:src (:dish-on imgs)}]
                 [:img.dish {:src (:dish-off imgs)}])
     [:img.oven {:src (:oven-off imgs)}]
     [:img.stove {:src (:stove-off imgs)}]]]
   [:div.line
    ; LIVINGROOM-SW
    [:div {:className (str "room" (when @livingroom-lights-off " dark"))}
     [:img.room-image {:src (:living-sw imgs)}]
     (if @tv-on [:img.tv {:src (:tv-on imgs) :on-click #(reset! tv-on false)}]
                [:img.tv {:src (:tv-off imgs) :on-click #(reset! tv-on true)}])]
    ; LIVINGROOM-SE
    [:div {:className (str "room" (when @livingroom-lights-off " dark"))}
     [:img.room-image {:src (:living-se imgs)}]
     (if @radio-on [:img.radio-on {:src (:radio-on imgs) :on-click #(reset! radio-on false)}]
                  [:img.radio-off {:src (:radio-off imgs) :on-click #(reset! radio-on true)}])]
    ; BATHROOM
    [:div {:className (str "room" (cond (and bath-door-open @bathroom-lights-off (not @livingroom-lights-off)) " dim"
                                        @bathroom-lights-off " dark"))}
     [:img.room-image {:src (:bathroom imgs)}]
     [:img {:className (str "bathroom-door" (if bath-door-open "-open" "-closed"))
            :src (:door imgs)}]
     [:img.laundry {:src (:laundry imgs)}]
     [:img.wash {:src (:wash-on imgs)}]
     [:img.lightswitch-bath {:src (:lightswitch-bath imgs)
                               :on-click #(if @bathroom-lights-off
                                           (reset! bathroom-lights-off false)
                                           (reset! bathroom-lights-off true))}]]]
   [:div.line
    ; GARAGE
    [:div {:className (str "room" (when @garage-lights-off " dark"))}
     [:img.room-image {:src (:garage imgs)}]
     (when car-in [:img.car {:src (:car imgs)}])
     (when garage-door-closed [:img {:className "garage-port" :src (:garage-port imgs)}])
     [:img.lightswitch-garage {:src (:lightswitch-garage imgs)
                        :on-click #(if @garage-lights-off
                                    (reset! garage-lights-off false)
                                    (reset! garage-lights-off true))}]]
     ; HALL
    [:div {:className (str "room" (when @livingroom-lights-off " dark"))}
     [:img.room-image {:src (:hall imgs)}]
     (if @front-door-open [:img.front-door {:src (:frontdoor-open imgs)
                                           :on-click #(reset! front-door-open false)}]
                         [:img.front-door {:src (:frontdoor-closed imgs)
                                           :on-click #(reset! front-door-open true)}])]
    ; BEDROOM
    [:div {:className (str "room" (cond (and bed-door-open @bedroom-lights-off (not @livingroom-lights-off)) " dim"
                                        @bedroom-lights-off " dark"))}
     [:img.room-image {:src (:bedroom imgs)}]
     [:img {:className (str "bedroom-door" (if bed-door-open "-open" "-closed"))
            :src (:door imgs)}]
     [:img.lightswitch-bed {:src (:lightswitch-bed imgs)
                            :on-click #(if @bedroom-lights-off
                                         (reset! bedroom-lights-off false)
                                         (reset! bedroom-lights-off true))}]]]])

(defn kitchen-page []
  [:div.container
   [:div.line
    ; KITCHEN
    [:div {:className (str "room" (when @livingroom-lights-off " dark"))}
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