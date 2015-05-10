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
(defn home-page []
  [:div.container
   [:div.line
    ; LIVINGROOM-NW
    [:div {:className (str "room" (when (:lights-off? (:livingroom (:rooms @state))) " dark"))}
     [:img.room-image {:src (:living-nw imgs)}]]
    ; LIVINGROOM-NE
    [:div {:className (str "room" (when (:lights-off? (:livingroom (:rooms @state))) " dark"))}
     [:img.room-image {:src (:living-ne imgs)}]
     [:img.lightswitch-living {:src      (:lightswitch-living imgs)
                               :on-click #(if (:lights-off? (:livingroom (:rooms @state)))
                                           (swap! state assoc-in [:rooms :livingroom :lights-off?] false)
                                           (swap! state assoc-in [:rooms :livingroom :lights-off?] true))}]]
    ; KITCHEN
    [:div {:className (str "room" (when (:lights-off? (:livingroom (:rooms @state))) " dark"))}
     [:img.room-image {:src (:kitchen imgs)}]
     [:img.fridge {:src (:fridge imgs)}]
     (if (:active? (:dish-washer (:kitchen (:rooms @state))))
       [:img.dish {:src      (:dish-on imgs)
                   :on-click #(swap! state assoc-in [:rooms :kitchen :dish-washer :active?] false)}]
       [:img.dish {:src (:dish-off imgs)
                   :on-click #(swap! state assoc-in [:rooms :kitchen :dish-washer :active?] true)}])
     (if (:active? (:oven (:kitchen (:rooms @state))))
       [:img.oven {:src (:oven-on imgs)
                   :on-click #(swap! state assoc-in [:rooms :kitchen :oven :active?] false)}]
       [:img.oven {:src (:oven-off imgs)
                   :on-click #(swap! state assoc-in [:rooms :kitchen :oven :active?] true)}])
     (if (:active? (:stove (:kitchen (:rooms @state))))
       [:img.stove {:src (:stove-on imgs)
                    :on-click #(swap! state assoc-in [:rooms :kitchen :stove :active?] false)}]
       [:img.stove {:src (:stove-off imgs)
                    :on-click #(swap! state assoc-in [:rooms :kitchen :stove :active?] true)}])]]
   [:div.line
    ; LIVINGROOM-SW
    [:div {:className (str "room" (when (:lights-off? (:livingroom (:rooms @state))) " dark"))}
     [:img.room-image {:src (:living-sw imgs)}]
     (if (:tv-on? (:livingroom (:rooms @state)))
       [:img.tv {:src (:tv-on imgs)
                 :on-click #(swap! state assoc-in [:rooms :livingroom :tv-on?] false)}]
       [:img.tv {:src (:tv-off imgs)
                 :on-click #(swap! state assoc-in [:rooms :livingroom :tv-on?] true)}])]
    ; LIVINGROOM-SE
    [:div {:className (str "room" (when (:lights-off? (:livingroom (:rooms @state))) " dark"))}
     [:img.room-image {:src (:living-se imgs)}]
     (if (:radio-on? (:livingroom (:rooms @state)))
       [:img.radio-on {:src (:radio-on imgs)
                       :on-click #(swap! state assoc-in [:rooms :livingroom :radio-on?] false)}]
       [:img.radio-off {:src (:radio-off imgs)
                        :on-click #(swap! state assoc-in [:rooms :livingroom :radio-on?] true)}])
     [:div {:on-click #(swap! state update-in [:rooms :livingroom :temperature :set-to] inc)}
      [:div.temp (:current (:temperature (:livingroom (:rooms @state))))]
      [:div.set-temp (:set-to (:temperature (:livingroom (:rooms @state))))]]]
    ; BATHROOM
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
     [:img.laundry {:src (:laundry imgs)}]
     (if (:active? (:washing-machine (:bathroom (:rooms @state))))
       [:img.wash {:src (:wash-on imgs)
                   :on-click #(swap! state assoc-in [:rooms :bathroom :washing-machine :active?] false)}]
       [:img.wash {:src (:wash-off imgs)
                   :on-click #(swap! state assoc-in [:rooms :bathroom :washing-machine :active?] true)}])
     [:img.lightswitch-bath {:src (:lightswitch-bath imgs)
                               :on-click #(if (:lights-off? (:bathroom (:rooms @state)))
                                           (swap! state assoc-in [:rooms :bathroom :lights-off?] false)
                                           (swap! state assoc-in [:rooms :bathroom :lights-off?] true))}]
     [:div {:on-click #(swap! state update-in [:rooms :bathroom :temperature :set-to] inc)}
      [:div.temp (:current (:temperature (:bathroom (:rooms @state))))]
      [:div.set-temp (:set-to (:temperature (:bathroom (:rooms @state))))]]]]
   [:div.line
    ; GARAGE
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
                                    (swap! state assoc-in [:rooms :garage :lights-off?] true))}]]
     ; HALL
    [:div {:className (str "room" (when (:lights-off? (:livingroom (:rooms @state))) " dark"))}
     [:img.room-image {:src (:hall imgs)}]
     (if (:front-door-locked? (:hall (:rooms @state)))
       [:img.front-door {:src (:frontdoor-closed imgs)
                         :on-click #(swap! state assoc-in [:rooms :hall :front-door-locked?] false)}]
       [:img.front-door {:src (:frontdoor-open imgs)
                         :on-click #(swap! state assoc-in [:rooms :hall :front-door-locked?] true)}])]
    ; BEDROOM
    [:div {:className (str "room" (cond
                                    (and (:door-open? (:bedroom (:rooms @state)))
                                         (:lights-off? (:bedroom (:rooms @state)))
                                         (not (:lights-off? (:livingroom (:rooms @state)))))
                                    " dim"
                                    (:lights-off? (:bedroom (:rooms @state)))
                                    " dark"))}
     [:img.room-image {:src (:bedroom imgs)}]
     [:img {:className (str "bedroom-door" (if (:door-open? (:bathroom (:rooms @state))) "-open" "-closed"))
            :src (:door imgs)}]
     [:img.lightswitch-bed {:src (:lightswitch-bed imgs)
                            :on-click #(if (:lights-off? (:bedroom (:rooms @state)))
                                        (swap! state assoc-in [:rooms :bedroom :lights-off?] false)
                                        (swap! state assoc-in [:rooms :bedroom :lights-off?] true))}]
     [:div {:on-click #(swap! state update-in [:rooms :bedroom :temperature :set-to] inc)}
      [:div.temp (:current (:temperature (:bedroom (:rooms @state))))]
      [:div.set-temp (:set-to (:temperature (:bedroom (:rooms @state))))]]]]])

(defn kitchen-page []
  [:div.container
   ])

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