(ns smarthome.data)

(def state
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