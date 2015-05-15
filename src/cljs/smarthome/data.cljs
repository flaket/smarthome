(ns smarthome.data)

(def initial-state
  {:rooms              {:livingroom {:lights-off? false
                                     :temperature {:current 21, :set-to 21}
                                     :tv-on?      false
                                     :radio-on?   false}
                        :kitchen    {:stove       {:active? false, :temp nil}
                                     :oven        {:active? false, :temp nil}
                                     :dish-washer {:active? false, :time-remaining 10}
                                     :fridge      ["Eggs" "Bacon" "Salad" "Tomatoes" "Almonds" "Avocado"]
                                     :lights-off? false}
                        :bathroom   {:door-open?      true
                                     :lights-off?     false
                                     :washing-machine {:active? false, :time-remaining 55}
                                     :laundry         {:whites 24
                                                       :colors 90}
                                     :temperature     {:current 19, :set-to 20}}
                        :garage     {:port-closed? false
                                     :car-in?      true
                                     :lights-off?  true}
                        :hall       {:lights-off?        false
                                     :front-door-locked? true}
                        :bedroom    {:door-open?  false
                                     :lights-off? false
                                     :temperature {:current 16, :set-to 16}}}
   :weather            {:current [:rain 10 "10:05"], :forecast-1 [:thunder 8 "13:05"], :forecast-2 [:thunder 7 "16:05"]}
   :diagnostics        {:smoke-detector-batteries :ok       ;ok/low/empty
                        :fire-alarm      :on              ;on/off/activated!
                        :burglary-alarm   :off               ;on/off/activated!
                        :pipes      :ok                     ;ok/faulty
                        :electrical :ok}                    ;ok/faulty
   :view               :home
   :show-state?        false
   :time-of-day        :morning
   :user               {:current-location :out
                        :last-view        :home}
   :simulation-running false
   :scenario           {:name :temp, :text "temporary text here.."}})

(def imgs {:living-nw          "../assets/livingroom-nw.jpg"
           :living-ne          "../assets/livingroom-ne.jpg"
           :living-sw          "../assets/livingroom-sw.jpg"
           :living-se          "../assets/livingroom-se.jpg"
           :kitchen            "../assets/kitchen.jpg"
           :bathroom           "../assets/bathroom.jpg"
           :garage             "../assets/garage.jpg"
           :hall               "../assets/hall.jpg"
           :bedroom            "../assets/bedroom.jpg"
           :fridge             "../assets/fridge.jpg"
           :dish-on            "../assets/dish-on.gif"
           :dish-off           "../assets/dish-off.jpg"
           :oven-on            "../assets/oven-on.jpg"
           :oven-off           "../assets/oven-off.jpg"
           :stove-on           "../assets/stove-on.jpg"
           :stove-off          "../assets/stove-off.jpg"
           :tv-on              "../assets/tv-on.gif"
           :tv-off             "../assets/tv-off.jpg"
           :radio-on           "../assets/radio-on.gif"
           :radio-off          "../assets/radio-off.jpg"
           :door               "../assets/door.jpg"
           :laundry            "../assets/laundry.jpg"
           :wash-on            "../assets/wash-on.gif"
           :wash-off           "../assets/wash-off.jpg"
           :car                "../assets/car.jpg"
           :garage-port        "../assets/garageport.jpg"
           :frontdoor-closed   "../assets/front-door-closed.jpg"
           :frontdoor-open     "../assets/front-door-open.jpg"
           :lightswitch-living "../assets/lightswitch-living.jpg"
           :lightswitch-garage "../assets/lightswitch-garage.jpg"
           :lightswitch-bath   "../assets/lightswitch-bath.jpg"
           :lightswitch-bed    "../assets/lightswitch-bed.jpg"
           :sunny              "../assets/weather_sun.png"
           :sunny-cloudy       "../assets/weather_suncloud.png"
           :cloudy             "../assets/weather_cloud.png"
           :rain               "../assets/weather_rain.png"
           :snow               "../assets/weather_snow.png"
           :thunder            "../assets/weather_thunder.png"})