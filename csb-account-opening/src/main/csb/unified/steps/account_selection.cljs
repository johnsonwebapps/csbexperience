(ns csb.unified.steps.account-selection
  (:require [csb.unified.state :as state]))

(defn account-card [product selected? on-toggle disabled?]
  (let [{:keys [id name description features required-for-loan]} product]
    [:div.p-5.rounded-xl.border-2.transition-all
     {:class (cond
               disabled? "border-gray-100 bg-gray-50 opacity-60"
               selected? "shadow-lg"
               :else "border-gray-200 hover:border-gray-300 hover:shadow-md cursor-pointer")
      :style (when selected?
               {:border-color "#00857c"
                :background-color "rgba(0, 133, 124, 0.05)"})
      :on-click #(when-not disabled? (on-toggle id))}
     [:div.flex.items-start.gap-4
      [:div.w-6.h-6.rounded.border-2.flex.items-center.justify-center.flex-shrink-0.mt-0.5
       {:style {:border-color (if selected? "#00857c" "#d1d5db")
                :background-color (if selected? "#00857c" "transparent")}}
       (when selected?
         [:svg.w-4.h-4.text-white {:fill "none" :viewBox "0 0 24 24" :stroke "currentColor"}
          [:path {:stroke-linecap "round" :stroke-linejoin "round" :stroke-width 3
                  :d "M5 13l4 4L19 7"}]])]
      [:div.flex-1
       [:div.flex.items-center.gap-2.mb-1
        [:h3.font-bold {:style {:color (if selected? "#00857c" "#333")}} name]
        (when required-for-loan
          [:span.text-xs.px-2.py-0.5.rounded-full.font-medium
           {:style {:background-color "#fef3c7" :color "#92400e"}}
           "Required for Loan"])]
       [:p.text-sm.text-gray-600.mb-3 description]
       [:ul.space-y-1
        (for [feature features]
          ^{:key feature}
          [:li.text-xs.text-gray-500.flex.items-center.gap-1
           [:span {:style {:color "#00857c"}} "✓"] feature])]]]]))

(defn account-selection-step []
  (let [form-data (:form-data @state/app-state)
        flow-type (:flow-type form-data)
        loan-decision (:loan-decision form-data)
        selected-accounts (:selected-accounts form-data)
        
        ;; Checking is required if loan approved (any loan flow)
        loan-approved? (= loan-decision :approved)
        checking-required? (and (not= flow-type :account-only) loan-approved?)
        
        toggle-account (fn [account-id]
                         ;; Don't allow unchecking if checking is required for loan
                         (when-not (and (= account-id "business-checking") checking-required?)
                           (let [new-selection (if (contains? selected-accounts account-id)
                                                 (disj selected-accounts account-id)
                                                 (conj selected-accounts account-id))]
                             (state/update-form-data! {:selected-accounts new-selection}))))]
    
    [:div.space-y-6
     [:div.card
      [:div.flex.items-center.gap-2.mb-4
       [:div.w-8.h-8.rounded-full.flex.items-center.justify-center.text-white.text-sm.font-bold
        {:style {:background-color "#00857c"}} 
        (if (= flow-type :account-only) "3" "7")]
       [:h2.text-xl.font-bold.text-gray-900 "Select Your Accounts"]]
      [:p.text-gray-600.mb-6
       (if checking-required?
         "Your loan approval includes a Business Checking account for payments. You can also add additional accounts."
         "Choose the accounts that best fit your business needs.")]]
     
     ;; Account selection
     [:div.space-y-4
      (for [product state/account-products]
        (let [product-with-required (if (and checking-required? 
                                              (= (:id product) "business-checking"))
                                       (assoc product :required-for-loan true)
                                       product)]
          ^{:key (:id product)}
          [account-card 
           product-with-required
           (contains? selected-accounts (:id product))
           toggle-account
           false]))]
     
     ;; Summary
     [:div.card
      [:h3.font-semibold.text-lg.mb-4.pb-2.border-b {:style {:color "#00857c"}}
       "Selected Accounts"]
      (if (empty? selected-accounts)
        [:p.text-gray-500.text-sm "No accounts selected. Please select at least one account."]
        [:ul.space-y-2
         (for [account-id selected-accounts]
           (let [product (first (filter #(= (:id %) account-id) state/account-products))]
             ^{:key account-id}
             [:li.flex.items-center.gap-2.text-gray-700
              [:span {:style {:color "#00857c"}} "✓"]
              (:name product)]))])]
     
     ;; Info note
     (when checking-required?
       [:div.rounded-lg.p-4 {:style {:background-color "rgba(0, 133, 124, 0.08)"
                                     :border "1px solid rgba(0, 133, 124, 0.2)"}}
        [:div.flex.gap-3
         [:div.text-xl "ℹ️"]
         [:div
          [:h4.font-semibold.mb-1 {:style {:color "#00857c"}} "Why Checking is Required"]
          [:p.text-sm.text-gray-600
           "Your approved loan requires a Business Checking account for automatic payment deductions. "
           "This ensures timely payments and helps maintain your good standing."]]]])
     
     ;; Navigation
     [:div.flex.justify-between.pt-4
      [:button.font-medium.py-3.px-6.rounded-lg.transition-all
       {:style {:color "#00857c" :border "2px solid #00857c"}
        :on-click state/go-back!}
       "← Back"]
      [:button.font-bold.py-3.px-8.rounded-lg.text-white.transition-all
       {:style {:background-color (if (empty? selected-accounts) "#ccc" "#00857c")}
        :disabled (empty? selected-accounts)
        :on-click state/go-next!}
       "Continue →"]]]))
