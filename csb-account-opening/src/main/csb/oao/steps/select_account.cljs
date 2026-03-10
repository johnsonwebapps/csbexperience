(ns csb.oao.steps.select-account
  (:require [reagent.core :as r]
            [csb.oao.state :as state]))

;; Account selection step — shared across all three flows

(defn select-account-step []
  (let [{:keys [form-data]} @state/app-state
        selected (:selected-account form-data)]
    [:div.space-y-6
     [:div.bg-white.rounded-xl.p-6 {:style {:box-shadow "0 2px 12px rgba(0,0,0,0.08)"}}
      [:h2.text-xl.font-bold.mb-2 {:style {:color "#333"}} "Select an Account"]
      [:p.text-sm.text-gray-500.mb-6 "Choose the account type that best fits your business needs."]
      
      [:div.space-y-4
       (for [{:keys [id name description features]} state/account-products]
         ^{:key id}
         [:div.rounded-lg.p-5.cursor-pointer.transition-all
          {:style {:border (if (= selected id)
                            "2px solid #00857c"
                            "1px solid #e5e7eb")
                   :background-color (if (= selected id)
                                       "rgba(0,133,124,0.04)"
                                       "white")}
           :on-click #(state/update-form-data! {:selected-account id})}
          [:div.flex.items-start.justify-between
           [:div
            [:h3.font-semibold {:style {:color "#333" :font-size "16px"}} name]
            [:p.text-sm.text-gray-500.mt-1 description]
            [:div.flex.flex-wrap.gap-3.mt-3
             (for [feat features]
               ^{:key feat}
               [:span.text-xs.px-2.py-1.rounded-full
                {:style {:background-color "#F0FDF4" :color "#166534"}}
                (str "✓ " feat)])]]
           [:div.w-5.h-5.rounded-full.border-2.flex.items-center.justify-center.flex-shrink-0.mt-1
            {:style {:border-color (if (= selected id) "#00857c" "#d1d5db")}}
            (when (= selected id)
              [:div.w-3.h-3.rounded-full {:style {:background-color "#00857c"}}])]]])]]
     
     ;; Account details based on selection
     (when (seq selected)
       (let [product (->> state/account-products (filter #(= (:id %) selected)) first)]
         [:div.bg-white.rounded-xl.p-6 {:style {:box-shadow "0 2px 12px rgba(0,0,0,0.08)"}}
          [:h3.font-semibold.mb-3 {:style {:color "#333"}} (str (:name product) " Details")]
          [:div.grid.grid-cols-2.gap-4.text-sm
           [:div
            [:span.text-gray-500 "Monthly Fee: "]
            [:span.font-medium "Waived with qualifying activity"]]
           [:div
            [:span.text-gray-500 "Minimum Opening Deposit: "]
            [:span.font-medium "Contact us for details"]]
           [:div
            [:span.text-gray-500 "FDIC Insured: "]
            [:span.font-medium "Yes"]]
           [:div
            [:span.text-gray-500 "Online & Mobile Banking: "]
            [:span.font-medium "Included"]]]]))
     
     ;; Navigation
     [:div.flex.justify-between
      [:button.py-3.px-6.rounded-lg.font-semibold
       {:style {:color "#00857c" :border "1px solid #00857c"}
        :on-click #(state/go-back!)}
       "← Back"]
      [:button.py-3.px-8.rounded-lg.font-semibold.text-white
       {:style {:background-color "#00857c"
                :opacity (if (seq selected) 1 0.5)}
        :disabled (empty? selected)
        :on-click #(when (seq selected) (state/go-next!))}
       "Continue →"]]]))
