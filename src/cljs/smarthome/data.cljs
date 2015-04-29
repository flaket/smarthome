(ns smarthome.data)

(def state
  {:rooms         [
                   {:kitchen
                    {:stove          {:active? false, :temp nil}
                     :oven           {:active? false, :temp nil}
                     :coffee-machine {:active? false}
                     :lights         :off
                     :temperature    {:current 20, :set-to 20}}}
                   {:living-room
                    {:lights      {:state :off, :value 0}
                     :temperature {:current 20, :set-to 20}
                     :tv          {}}}
                   {:dining-room
                    {:lights      {:state :off, :value 0}
                     :temperature {:current 20, :set-to 20}}}
                   {:bath-room
                    {:lights      :off
                     :temperature {:current 20, :set-to 20}
                     :radio       {}}}
                   {:hall
                    {:lights      :off
                     :temperature {:current 20, :set-to 20}}}
                   {:bed-room
                    {:lights      :off
                     :temperature {:current 20, :set-to 20}}}
                   {:garage
                    {:lights      :off
                     :temperature {:current 20, :set-to 20}}}
                   {:driveway
                    {:heat :off, :lights :off}}
                   {:balcony
                    {:lights :off}}]
   :time          nil
   :time-of-day   :morning
   :season        :spring
   :user-location {:indoor :kitchen, :coordinates [63.433639 10.392072]}
   :user-profile  {}})