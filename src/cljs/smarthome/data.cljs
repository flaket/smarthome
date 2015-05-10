(ns smarthome.data)

(def initial-state
  {:rooms         {:livingroom {:lights-off?      false
                                :temperature {:current 20, :set-to 20}
                                :tv-on?         false
                                :radio-on?      false}
                   :kitchen    {:stove          {:active? false, :temp nil}
                                :oven           {:active? false, :temp nil}
                                :coffee-machine {:active? false}
                                :dish-washer    {:active? false}
                                :lights-off?         false
                                :temperature    {:current 20, :set-to 20}}
                   :bathroom   {:door-open?        false
                                :lights-off?      false
                                :washing-machine {:active? false}
                                :laundry {}
                                :temperature {:current 20, :set-to 20}}
                   :garage     {:port-closed? true
                                :car-in?  true
                                :lights-off?  false
                                :temperature {:current 20, :set-to 20}}
                   :hall       {:lights-off?      false
                                :temperature {:current 20, :set-to 20}
                                :front-door-locked?   true}
                   :bedroom    {:door-open?      false
                                :lights-off?      false
                                :temperature {:current 20, :set-to 20}}}
   :time          nil
   :time-of-day   :morning                                  ;morning/day/evening/night
   :weather       :clear                                    ;clear/cloudy/rain/snow
   :user-location {:indoor :kitchen, :coordinates [63.433639 10.392072]}
   :user-profile  {}})



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
           :wash-off "../assets/wash-off.jpg"
           :car "../assets/car.jpg"
           :garage-port "../assets/garageport.jpg"
           :frontdoor-closed "../assets/front-door-closed.jpg"
           :frontdoor-open "../assets/front-door-open.jpg"
           :lightswitch-living "../assets/lightswitch-living.jpg"
           :lightswitch-garage "../assets/lightswitch-garage.jpg"
           :lightswitch-bath "../assets/lightswitch-bath.jpg"
           :lightswitch-bed "../assets/lightswitch-bed.jpg"})
