(ns smarthome.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [goog.events :as events]
              [goog.history.EventType :as EventType]
              [cljsjs.react :as react]
              [smarthome.data :refer [initial-state imgs]])
    (:import goog.History))

(def state (atom initial-state))

;; -------------------------
(defn kitchen []
  [:div {:className (str "room" (when (:lights-off? (:livingroom (:rooms @state))) " dark"))}
   [:img.room-image {:src (:kitchen imgs)}]
   [:img.fridge {:src (:fridge imgs)
                 :on-click #(swap! state assoc :view :food)}]
   (if (:active? (:dish-washer (:kitchen (:rooms @state))))
     [:div
      [:img.dish {:src      (:dish-on imgs)
                       :on-click #(swap! state assoc-in [:rooms :kitchen :dish-washer :active?] false)}]
      [:div.dish-time (str "0:" (:time-remaining (:dish-washer (:kitchen (:rooms @state)))))]]
     [:img.dish {:src (:dish-off imgs)
                 :on-click #(swap! state assoc-in [:rooms :kitchen :dish-washer :active?] true)}])
   (if (:active? (:oven (:kitchen (:rooms @state))))
     [:img.oven {:src      (:oven-on imgs)
                 :on-click #(swap! state assoc-in [:rooms :kitchen :oven :active?] false)}]
     [:img.oven {:src (:oven-off imgs)
                 :on-click #(swap! state assoc-in [:rooms :kitchen :oven :active?] true)}])
   (if (:active? (:stove (:kitchen (:rooms @state))))
     [:img.stove {:src (:stove-on imgs)
                  :on-click #(swap! state assoc-in [:rooms :kitchen :stove :active?] false)}]
     [:img.stove {:src (:stove-off imgs)
                  :on-click #(swap! state assoc-in [:rooms :kitchen :stove :active?] true)}])])

(defn bathroom []
  [:div {:className (str "room" (cond
                                  (and (:door-open? (:bathroom (:rooms @state)))
                                       (:lights-off? (:bathroom (:rooms @state)))
                                       (not (:lights-off? (:livingroom (:rooms @state)))))
                                  " dim"
                                  (:lights-off? (:bathroom (:rooms @state)))
                                  " dark"))}
   [:img.room-image {:src (:bathroom imgs)}]
   [:img {:className (str "bathroom-door" (if (:door-open? (:bathroom (:rooms @state))) "-open" "-closed"))
          :src (:door imgs)}]
   [:div
    [:img.laundry {:src (:laundry imgs)}]
    [:div.whites (str (:whites (:laundry (:bathroom (:rooms @state)))) "%")]
    [:div.colors (str (:colors (:laundry (:bathroom (:rooms @state)))) "%")]]
   (if (:active? (:washing-machine (:bathroom (:rooms @state))))
     [:div
      [:img.wash {:src      (:wash-on imgs)
                       :on-click #(swap! state assoc-in [:rooms :bathroom :washing-machine :active?] false)}]
      [:div.wash-time (str "0:" (:time-remaining (:washing-machine (:bathroom (:rooms @state)))))]]
     [:img.wash {:src (:wash-off imgs)
                 :on-click #(swap! state assoc-in [:rooms :bathroom :washing-machine :active?] true)}])
   [:img.lightswitch-bath {:src (:lightswitch-bath imgs)
                           :on-click #(if (:lights-off? (:bathroom (:rooms @state)))
                                       (swap! state assoc-in [:rooms :bathroom :lights-off?] false)
                                       (swap! state assoc-in [:rooms :bathroom :lights-off?] true))}]
   [:div {:on-click #(swap! state update-in [:rooms :bathroom :temperature :set-to] inc)}
    [:div.temp (:current (:temperature (:bathroom (:rooms @state))))]
    [:div.set-temp (:set-to (:temperature (:bathroom (:rooms @state))))]]])

(defn bedroom []
  [:div {:className (str "room" (cond
                                  (and (:door-open? (:bedroom (:rooms @state)))
                                       (:lights-off? (:bedroom (:rooms @state)))
                                       (not (:lights-off? (:livingroom (:rooms @state)))))
                                  " dim"
                                  (:lights-off? (:bedroom (:rooms @state)))
                                  " dark"))}
   [:img.room-image {:src (:bedroom imgs)}]
   [:img {:className (str "bedroom-door" (if (:door-open? (:bedroom (:rooms @state))) "-open" "-closed"))
          :src (:door imgs)}]
   [:img.lightswitch-bed {:src (:lightswitch-bed imgs)
                          :on-click #(if (:lights-off? (:bedroom (:rooms @state)))
                                      (swap! state assoc-in [:rooms :bedroom :lights-off?] false)
                                      (swap! state assoc-in [:rooms :bedroom :lights-off?] true))}]
   [:div {:on-click #(swap! state update-in [:rooms :bedroom :temperature :set-to] inc)}
    [:div.temp (:current (:temperature (:bedroom (:rooms @state))))]
    [:div.set-temp (:set-to (:temperature (:bedroom (:rooms @state))))]]])

(defn hall []
  [:div {:className (str "room" (when (:lights-off? (:livingroom (:rooms @state))) " dark"))}
   [:img.room-image {:src (:hall imgs)}]
   (if (:front-door-locked? (:hall (:rooms @state)))
     [:img.front-door {:src (:frontdoor-closed imgs)
                       :on-click #(swap! state assoc-in [:rooms :hall :front-door-locked?] false)}]
     [:img.front-door {:src (:frontdoor-open imgs)
                       :on-click #(swap! state assoc-in [:rooms :hall :front-door-locked?] true)}])])

(defn garage []
  [:div {:className (str "room" (when (:lights-off? (:garage (:rooms @state))) " dark"))}
   [:img.room-image {:src (:garage imgs)
                     :on-click  #(swap! state assoc-in [:rooms :garage :port-closed?] true)}]
   (when (:car-in? (:garage (:rooms @state))) [:img.car {:src (:car imgs)}])
   (when (:port-closed? (:garage (:rooms @state)))
     [:img {:className "garage-port"
            :src       (:garage-port imgs)
            :on-click  #(swap! state assoc-in [:rooms :garage :port-closed?] false)}])
   [:img.lightswitch-garage {:src (:lightswitch-garage imgs)
                             :on-click #(if (:lights-off? (:garage (:rooms @state)))
                                         (swap! state assoc-in [:rooms :garage :lights-off?] false)
                                         (swap! state assoc-in [:rooms :garage :lights-off?] true))}]])

(defn livingroom-nw []
  [:div {:className (str "room" (when (:lights-off? (:livingroom (:rooms @state))) " dark"))}
   [:img.room-image {:src (:living-nw imgs)}]])

(defn livingroom-ne []
  [:div {:className (str "room" (when (:lights-off? (:livingroom (:rooms @state))) " dark"))}
   [:img.room-image {:src (:living-ne imgs)}]
   [:img.lightswitch-living {:src      (:lightswitch-living imgs)
                             :on-click #(if (:lights-off? (:livingroom (:rooms @state)))
                                         (swap! state assoc-in [:rooms :livingroom :lights-off?] false)
                                         (swap! state assoc-in [:rooms :livingroom :lights-off?] true))}]])

(defn livingroom-sw []
  [:div {:className (str "room" (when (:lights-off? (:livingroom (:rooms @state))) " dark"))}
   [:img.room-image {:src (:living-sw imgs)}]
   (if (:tv-on? (:livingroom (:rooms @state)))
     [:img.tv {:src (:tv-on imgs)
               :on-click #(swap! state assoc-in [:rooms :livingroom :tv-on?] false)}]
     [:img.tv {:src (:tv-off imgs)
               :on-click #(swap! state assoc-in [:rooms :livingroom :tv-on?] true)}])])

(defn livingroom-se []
  [:div {:className (str "room" (when (:lights-off? (:livingroom (:rooms @state))) " dark"))}
   [:img.room-image {:src (:living-se imgs)}]
   (if (:radio-on? (:livingroom (:rooms @state)))
     [:img.radio-on {:src (:radio-on imgs)
                     :on-click #(swap! state assoc-in [:rooms :livingroom :radio-on?] false)}]
     [:img.radio-off {:src (:radio-off imgs)
                      :on-click #(swap! state assoc-in [:rooms :livingroom :radio-on?] true)}])
   [:div {:on-click #(swap! state update-in [:rooms :livingroom :temperature :set-to] inc)}
    [:div.temp (:current (:temperature (:livingroom (:rooms @state))))]
    [:div.set-temp (:set-to (:temperature (:livingroom (:rooms @state))))]]])

(defn food []
  [:div {:className "row food"}
   [:div.food-header "Items needed to stock the fridge"]
   [:ul
    (for [item (:fridge (:kitchen (:rooms @state)))] [:li item])]])

(defn navigation []
  [:div
   [:div {:className "row nav"}
    [:button {:className "two columns" :on-click #(swap! state assoc :view :home)} "Home"]
    [:button {:className "two columns" :on-click #(swap! state assoc :view :livingroom)} "Living Room"]
    [:button {:className "two columns" :on-click #(swap! state assoc :view :kitchen)} "Kitchen"]
    [:button {:className "two columns" :on-click #(swap! state assoc :view :bathroom)} "Bathroom"]
    [:button {:className "two columns" :on-click #(swap! state assoc :view :garage)} "Garage"]
    [:button {:className "two columns" :on-click #(swap! state assoc :view :hall)} "Hall"]]
   [:div {:className "row nav"}
    [:button {:className "two columns" :on-click #(swap! state assoc :view :bedroom)} "Bedroom"]
    [:button {:className "two columns" :on-click #(swap! state assoc :view :food)} "Food"]
    [:button {:className "two columns" :on-click #()} "Diagnostics"]
    [:button {:className "two columns" :on-click #()} "Weather"]
    [:button {:className "two columns" :on-click #()} "Energy"]]])

(defn simulation []
  [:div {:className "row sim"}
   (if (:simulation-running? @state)
     [:button {:className "button-primary four columns"
               :on-click  #(swap! state assoc :simulation-running? false)}
      (str "Simulation Running")]
     [:button {:className "button-primary four columns"
               :on-click  #(swap! state assoc :simulation-running? true)}
      (str "Simulation Paused")])
   (when (:simulation-running? @state)
     [:div {:className "eight columns food food-header"}
      (:text (:scenario @state))])])

(defn data-structure []
  [:div {:className "row"}
   (if (:show-state? @state)
     [:button {:className "button-primary four columns"
               :on-click  #(swap! state assoc :show-state? false)}
      (str "Hide Data Structure")]
     [:button {:className "button-primary four columns"
               :on-click  #(swap! state assoc :show-state? true)}
      (str "Show Data Structure")])
   (when (:show-state? @state)
     [:div {:className "eight columns food food-header"}
      (str @state)])])

(defn home-page []
  (let [view (:view @state)]
    [:div.container
     (navigation)
     (cond
       (= :kitchen view) (kitchen)
       (= :bathroom view) (bathroom)
       (= :bedroom view) (bedroom)
       (= :garage view) (garage)
       (= :hall view) (hall)
       (= :food view) (food)
       (= :home view)
       [:div
        [:div.row
         (livingroom-nw)
         (livingroom-ne)
         (kitchen)]
        [:div.row
         (livingroom-sw)
         (livingroom-se)
         (bathroom)]
        [:div.row
         (garage)
         (hall)
         (bedroom)]]
       (= :livingroom view)
       [:div
        [:div.row
         (livingroom-nw)
         (livingroom-ne)
         [:div.room]]
        [:div.row
         (livingroom-sw)
         (livingroom-se)
         [:div.room]]])
     [:div.row]
     (simulation)
     (data-structure)]))

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
(defn current-page []
  [:div [(session/get :current-page)]])

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (hook-browser-navigation!)
  (mount-root))