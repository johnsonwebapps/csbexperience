(ns csb.oao.steps.account-services
  (:require [reagent.core :as r]
            [csb.oao.state :as state]))

;; Account services selection (debit card, eStatements, mobile deposit, etc.)

(defn account-services-step []
  (let [{:keys [form-data]} @state/app-state
        selected-services (:selected-services form-data)]
    [:div.space-y-6
     [:div.bg-white.rounded-xl.p-6 {:style {:box-shadow "0 2px 12px rgba(0,0,0,0.08)"}}
      [:h2.text-xl.font-bold.mb-2 {:style {:color "#333"}} "Account Services"]
      [:p.text-sm.text-gray-500.mb-6 "Select additional services for your new account. You can change these later."]
      
      [:div.space-y-3
       (for [{:keys [id label default]} state/account-services-options]
         (let [checked (contains? selected-services id)]
           ^{:key id}
           [:div.rounded-lg.p-4.cursor-pointer.transition-all
            {:style {:border (if checked "2px solid #00857c" "1px solid #e5e7eb")
                     :background-color (if checked "rgba(0,133,124,0.04)" "white")}
             :on-click #(state/update-form-data!
                         {:selected-services
                          (if checked
                            (disj selected-services id)
                            (conj (or selected-services #{}) id))})}
            [:div.flex.items-center.gap-3
             [:div.w-5.h-5.rounded.border-2.flex.items-center.justify-center
              {:style {:border-color (if checked "#00857c" "#d1d5db")
                       :background-color (if checked "#00857c" "white")}}
              (when checked
                [:span.text-white.text-xs "✓"])]
             [:div
              [:span.font-medium.text-sm {:style {:color "#333"}} label]
              (when default
                [:span.ml-2.text-xs.px-2.py-0.5.rounded-full
                 {:style {:background-color "#F0FDF4" :color "#166534"}}
                 "Recommended"])]]]))]]
     
     ;; Navigation
     [:div.flex.justify-between
      [:button.py-3.px-6.rounded-lg.font-semibold
       {:style {:color "#00857c" :border "1px solid #00857c"}
        :on-click #(state/go-back!)}
       "← Back"]
      [:button.py-3.px-8.rounded-lg.font-semibold.text-white
       {:style {:background-color "#00857c"}
        :on-click #(state/go-next!)}
       "Continue →"]]]))
