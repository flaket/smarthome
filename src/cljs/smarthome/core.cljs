(ns smarthome.core
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [cljsjs.react :as react]
            [cljs.core.async :refer [chan close! <!]]
            [smarthome.data :refer [initial-state imgs]])
  (:require-macros
    [cljs.core.async.macros :as m :refer [go-loop]])
  (:import goog.History))

(defn timeout [ms]
  (let [c (chan)]
    (js/setTimeout (fn [] (close! c)) ms)
    c))

(def state (atom initial-state))

;; -------------------------
;; Scenario simulations
(defn start-simulation! []
  (go-loop []
           ; fire alarm
           (reset! state initial-state)
           (swap! state assoc :scenario "A fire has broken out and the fire alarm goes off!")
           (<! (timeout 5000))
           (swap! state assoc-in [:diagnostics :fire-alarm] :activated!)
           (swap! state assoc :view :diagnostics)
           (swap! state assoc :scenario "The graphics show that the fire alarm has been activated.")
           (<! (timeout 8000))

           ; stove still on
           (reset! state initial-state)
           (swap! state assoc-in [:rooms :livingroom :lights-off?] false)
           (swap! state assoc-in [:rooms :kitchen :stove :active?] true)
           (swap! state assoc-in [:rooms :hall :front-door-locked?] false)
           (swap! state assoc :scenario "The inhabitant is about to leave the house..")
           (<! (timeout 5000))
           (swap! state assoc-in [:rooms :livingroom :lights-off?] true)
           (swap! state assoc-in [:rooms :hall :front-door-locked?] true)
           (swap! state assoc-in [:rooms :garage :car-in?] false)
           (swap! state assoc :scenario "But she forgot to turn off the stove!")
           (<! (timeout 5000))
           (swap! state assoc :view :kitchen)
           (swap! state assoc :scenario "The still hot oven is shown to the user.")
           (<! (timeout 8000))

           ; burglary alarm
           (reset! state initial-state)
           (swap! state assoc :scenario "...")
           (swap! state assoc :scenario "The burglary detector has detected an intruder!")
           (<! (timeout 5000))
           (swap! state assoc-in [:diagnostics :burglary-alarm] :activated!)
           (swap! state assoc :view :diagnostics)
           (swap! state assoc :scenario "The graphics shows that the burglary alarm has been activated.")
           (<! (timeout 8000))

           (recur)))

;; -------------------------
(defn kitchen [z]
  [:div {:className (str "room" z (when (:lights-off? (:livingroom (:rooms @state))) " dark"))}
   [:img.room-image {:src      (:kitchen imgs)
                     :on-click #(swap! state assoc :view :kitchen)}]
   [:img.fridge {:src      (:fridge imgs)
                 :on-click #(swap! state assoc :view :food)}]
   (if (:active? (:dish-washer (:kitchen (:rooms @state))))
     [:div
      [:img.dish {:src      (:dish-on imgs)
                  :on-click #(swap! state assoc-in [:rooms :kitchen :dish-washer :active?] false)}]
      [:div.dish-time (str "0:" (:time-remaining (:dish-washer (:kitchen (:rooms @state)))))]]
     [:img.dish {:src      (:dish-off imgs)
                 :on-click #(swap! state assoc-in [:rooms :kitchen :dish-washer :active?] true)}])
   (if (:active? (:oven (:kitchen (:rooms @state))))
     [:img.oven {:src      (:oven-on imgs)
                 :on-click #(swap! state assoc-in [:rooms :kitchen :oven :active?] false)}]
     [:img.oven {:src      (:oven-off imgs)
                 :on-click #(swap! state assoc-in [:rooms :kitchen :oven :active?] true)}])
   (if (:active? (:stove (:kitchen (:rooms @state))))
     [:img.stove {:src      (:stove-on imgs)
                  :on-click #(swap! state assoc-in [:rooms :kitchen :stove :active?] false)}]
     [:img.stove {:src      (:stove-off imgs)
                  :on-click #(swap! state assoc-in [:rooms :kitchen :stove :active?] true)}])])

(defn temperature [room]
  [:div
   [:div.temp {:on-click #(swap! state update-in [:rooms room :temperature :set-to] dec)}
    (:current (:temperature (room (:rooms @state))))]
   [:div.set-temp {:on-click #(swap! state update-in [:rooms room :temperature :set-to] inc)}
    (:set-to (:temperature (room (:rooms @state))))]])

(defn bathroom [z]
  [:div {:className (str "room" z (cond
                                    (and (:door-open? (:bathroom (:rooms @state)))
                                         (:lights-off? (:bathroom (:rooms @state)))
                                         (not (:lights-off? (:livingroom (:rooms @state)))))
                                    " dim"
                                    (:lights-off? (:bathroom (:rooms @state)))
                                    " dark"))}
   [:img.room-image {:src      (:bathroom imgs)
                     :on-click #(swap! state assoc :view :bathroom)}]
   [:img {:className (str "bathroom-door" (if (:door-open? (:bathroom (:rooms @state))) "-open" "-closed"))
          :src       (:door imgs)}]
   [:div
    [:img.laundry {:src (:laundry imgs)}]
    [:div.whites (str (:whites (:laundry (:bathroom (:rooms @state)))) "%")]
    [:div.colors (str (:colors (:laundry (:bathroom (:rooms @state)))) "%")]]
   (if (:active? (:washing-machine (:bathroom (:rooms @state))))
     [:div
      [:img.wash {:src      (:wash-on imgs)
                  :on-click #(swap! state assoc-in [:rooms :bathroom :washing-machine :active?] false)}]
      [:div.wash-time (str "0:" (:time-remaining (:washing-machine (:bathroom (:rooms @state)))))]]
     [:img.wash {:src      (:wash-off imgs)
                 :on-click #(swap! state assoc-in [:rooms :bathroom :washing-machine :active?] true)}])
   [:img.lightswitch-bath {:src      (:lightswitch-bath imgs)
                           :on-click #(if (:lights-off? (:bathroom (:rooms @state)))
                                       (swap! state assoc-in [:rooms :bathroom :lights-off?] false)
                                       (swap! state assoc-in [:rooms :bathroom :lights-off?] true))}]
   (temperature :bathroom)])

(defn bedroom [z]
  [:div {:className (str "room" z (cond
                                    (and (:door-open? (:bedroom (:rooms @state)))
                                         (:lights-off? (:bedroom (:rooms @state)))
                                         (not (:lights-off? (:livingroom (:rooms @state)))))
                                    " dim"
                                    (:lights-off? (:bedroom (:rooms @state)))
                                    " dark"))}
   [:img.room-image {:src      (:bedroom imgs)
                     :on-click #(swap! state assoc :view :bedroom)}]
   [:img {:className (str "bedroom-door" (if (:door-open? (:bedroom (:rooms @state))) "-open" "-closed"))
          :src       (:door imgs)}]
   [:img.lightswitch-bed {:src      (:lightswitch-bed imgs)
                          :on-click #(if (:lights-off? (:bedroom (:rooms @state)))
                                      (swap! state assoc-in [:rooms :bedroom :lights-off?] false)
                                      (swap! state assoc-in [:rooms :bedroom :lights-off?] true))}]
   (temperature :bedroom)])

(defn hall [z]
  [:div {:className (str "room" z (when (:lights-off? (:livingroom (:rooms @state))) " dark"))}
   [:img.room-image {:src      (:hall imgs)
                     :on-click #(swap! state assoc :view :hall)}]
   (if (:front-door-locked? (:hall (:rooms @state)))
     [:img.front-door {:src      (:frontdoor-closed imgs)
                       :on-click #(swap! state assoc-in [:rooms :hall :front-door-locked?] false)}]
     [:img.front-door {:src      (:frontdoor-open imgs)
                       :on-click #(swap! state assoc-in [:rooms :hall :front-door-locked?] true)}])])

(defn garage [z]
  [:div {:className (str "room" z (when (:lights-off? (:garage (:rooms @state))) " dark"))}
   [:img {:className "room-image"
          :src       (:garage imgs)
          :on-click  #(swap! state assoc :view :garage)}]
   (when (:car-in? (:garage (:rooms @state))) [:img.car {:src (:car imgs)}])
   (if (:port-closed? (:garage (:rooms @state)))
     [:img {:className "garage-port"
            :src       (:garage-port imgs)
            :on-click  #(swap! state assoc-in [:rooms :garage :port-closed?] false)}]
     [:div {:className "garage-port"
            :on-click  #(swap! state assoc-in [:rooms :garage :port-closed?] true)}])
   [:img.lightswitch-garage {:src      (:lightswitch-garage imgs)
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
     [:img.tv {:src      (:tv-on imgs)
               :on-click #(swap! state assoc-in [:rooms :livingroom :tv-on?] false)}]
     [:img.tv {:src      (:tv-off imgs)
               :on-click #(swap! state assoc-in [:rooms :livingroom :tv-on?] true)}])])

(defn livingroom-se []
  [:div {:className (str "room" (when (:lights-off? (:livingroom (:rooms @state))) " dark"))}
   [:img.room-image {:src (:living-se imgs)}]
   (if (:radio-on? (:livingroom (:rooms @state)))
     [:img.radio-on {:src      (:radio-on imgs)
                     :on-click #(swap! state assoc-in [:rooms :livingroom :radio-on?] false)}]
     [:img.radio-off {:src      (:radio-off imgs)
                      :on-click #(swap! state assoc-in [:rooms :livingroom :radio-on?] true)}])
   (temperature :livingroom)])

(defn food []
  [:div {:className "row text"}
   [:div.text-header "Items needed to stock the fridge"]
   [:ul
    (for [item (:fridge (:kitchen (:rooms @state)))] [:li item])]])

(defn diagnostics []
  (let [{smoke :smoke-detector-batteries fire :fire-alarm burglar :burglary-alarm pipes :pipes el :electrical}
        (:diagnostics @state)]
    [:div
     [:div {:className "row text text-header"}
      [:div {:className "four columns"} "Fire alarm"]
      [:div {:className (str "four columns" (cond
                                              (= fire :off) " off"
                                              (= fire :on) " on"
                                              (= fire :activated!) " warning"))
             :on-click #(swap! state assoc-in [:diagnostics :fire-alarm] (if (= fire :off) :on :off))}
       (name fire)]]
     [:div {:className "row text text-header"}
      [:div {:className "four columns"} "Burglary alarm"]
      [:div {:className (str "four columns" (cond
                                              (= burglar :off) " off"
                                              (= burglar :on) " on"
                                              (= burglar :activated!) " warning"))
             :on-click #(swap! state assoc-in [:diagnostics :burglary-alarm] (if (= burglar :off) :on :off))}
       (name burglar)]]
     [:div {:className "row text text-header"}
      [:div {:className "four columns"} "Smoke detector batteries"]
      [:div {:className (str "four columns" (cond
                                              (= smoke :low) " off"
                                              (= smoke :ok) " on"
                                              (= smoke :empty) " off"))}
       (name smoke)]]
     [:div {:className "row text text-header"}
      [:div {:className "four columns"} "Electrical wiring"]
      [:div {:className (str "four columns" (cond
                                              (= el :faulty) " off"
                                              (= el :ok) " on"))}
       (name el)]]
     [:div {:className "row text text-header"}
      [:div {:className "four columns"} "Water pipes"]
      [:div {:className (str "four columns" (cond
                                              (= pipes :faulty) " off"
                                              (= pipes :ok) " on"))}
       (name pipes)]]]))

(defn weather []
  (let [[weather temp time] (:current (:weather @state))
        [f-weather f-temp f-time] (:forecast-1 (:weather @state))
        [f2-weather f2-temp f2-time] (:forecast-2 (:weather @state))]
    [:div
     [:div {:className "row text"}
      [:div {:className "four columns text-header"} (str "Current: " time)]
      [:div {:className "four columns text-header"} (str "Forecast: " f-time)]
      [:div {:className "four columns text-header"} (str "Forecast: " f2-time)]]
     [:div {:className "row"}
      [:img {:className "four columns weather" :src (weather imgs)}]
      [:img {:className "four columns weather" :src (f-weather imgs)}]
      [:img {:className "four columns weather" :src (f2-weather imgs)}]]
     [:div {:className "row text"}
      [:div {:className "four columns text-header"} (str temp " Celsius")]
      [:div {:className "four columns text-header"} (str f-temp " Celsius")]
      [:div {:className "four columns text-header"} (str f2-temp " Celsius")]]]))

(defn data-structure []
  [:div {:className "row"}
   (if (:show-state? @state)
     [:button {:className "button-primary three columns"
               :on-click  #(swap! state assoc :show-state? false)}
      (str "Hide data structure")]
     [:button {:className "button-primary three columns"
               :on-click  #(swap! state assoc :show-state? true)}
      (str "Show data structure")])
   (when (:show-state? @state)
     [:div {:className "nine columns text text-header"}
      (str @state)])])

(defn simulation []
  [:div {:className "row"}
   [:button {:className "button-primary three columns"
             :on-click  #(do
                          (start-simulation!)
                          (swap! state assoc :simulation? true))}
    (str "Start simulation")]
   [:div {:className "nine columns text text-header"} (:scenario @state)]])

(defn navigation []
  [:div
   [:div {:className "row nav"}
    [:button {:className "three columns" :on-click #(swap! state assoc :view :home)} "Home"]
    [:button {:className "three columns" :on-click #(swap! state assoc :view :food)} "Food"]
    [:button {:className "three columns" :on-click #(swap! state assoc :view :diagnostics)} "Diagnostics"]
    [:button {:className "three columns" :on-click #(swap! state assoc :view :weather)} "Weather"]]])

(defn home-page []
  (let [view (:view @state)]
    [:div.container
     (data-structure)
     (simulation)
     (navigation)
     (case view
       :kitchen (kitchen " zoom")
       :bathroom (bathroom " zoom")
       :bedroom (bedroom " zoom")
       :garage (garage " zoom")
       :hall (hall " zoom")
       :food (food)
       :weather (weather)
       :diagnostics (diagnostics)
       :home
       [:div
        [:div.row
         (livingroom-nw)
         (livingroom-ne)
         (kitchen "")]
        [:div.row
         (livingroom-sw)
         (livingroom-se)
         (bathroom "")]
        [:div.row
         (garage "")
         (hall "")
         (bedroom "")]])]))

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