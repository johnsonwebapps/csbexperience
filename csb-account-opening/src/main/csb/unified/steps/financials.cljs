(ns csb.unified.steps.financials
  (:require [csb.unified.state :as state]))

(defn form-field [{:keys [label required hint]} & children]
  [:div.mb-4
   [:label.block.text-sm.font-medium.text-gray-700.mb-1
    label
    (when required [:span.text-red-500.ml-1 "*"])]
   (into [:div] children)
   (when hint [:p.text-xs.text-gray-500.mt-1 hint])])

(defn financials-step []
  (let [form-data (:form-data @state/app-state)
        update-field! #(state/update-form-data! {%1 (-> %2 .-target .-value)})
        update-bool! #(state/update-form-data! {%1 (-> %2 .-target .-checked)})]
    [:div.space-y-6
     [:div.card
      [:div.flex.items-center.gap-2.mb-4
       [:div.w-8.h-8.rounded-full.flex.items-center.justify-center.text-white.text-sm.font-bold
        {:style {:background-color "#00857c"}} "4"]
       [:h2.text-xl.font-bold.text-gray-900 "Financial Information"]]
      [:p.text-gray-600.mb-6
       "Help us understand your business's financial position. This information is used to assess your loan application."]]
     
     ;; Revenue & Income
     [:div.card
      [:h3.font-semibold.text-lg.mb-4.pb-2.border-b {:style {:color "#00857c"}}
       "Revenue & Income"]
      
      [:div.grid.grid-cols-2.gap-4
       [form-field {:label "Annual Revenue (Current Year)" :required true}
        [:div.relative
         [:span.absolute.left-3.text-gray-500 {:style {:top "50%" :transform "translateY(-50%)"}} "$"]
         [:input.form-input.w-full.rounded-lg.border-gray-300.pl-7
          {:type "number"
           :value (:annual-revenue form-data)
           :on-change #(update-field! :annual-revenue %)
           :placeholder "500,000"}]]]
       
       [form-field {:label "Annual Revenue (Prior Year)"}
        [:div.relative
         [:span.absolute.left-3.text-gray-500 {:style {:top "50%" :transform "translateY(-50%)"}} "$"]
         [:input.form-input.w-full.rounded-lg.border-gray-300.pl-7
          {:type "number"
           :value (:annual-revenue-prior form-data)
           :on-change #(update-field! :annual-revenue-prior %)
           :placeholder "450,000"}]]]]
      
      [:div.grid.grid-cols-2.gap-4
       [form-field {:label "Net Income (Current Year)" :required true}
        [:div.relative
         [:span.absolute.left-3.text-gray-500 {:style {:top "50%" :transform "translateY(-50%)"}} "$"]
         [:input.form-input.w-full.rounded-lg.border-gray-300.pl-7
          {:type "number"
           :value (:net-income form-data)
           :on-change #(update-field! :net-income %)
           :placeholder "75,000"}]]]
       
       [form-field {:label "Cash on Hand"}
        [:div.relative
         [:span.absolute.left-3.text-gray-500 {:style {:top "50%" :transform "translateY(-50%)"}} "$"]
         [:input.form-input.w-full.rounded-lg.border-gray-300.pl-7
          {:type "number"
           :value (:cash-on-hand form-data)
           :on-change #(update-field! :cash-on-hand %)
           :placeholder "50,000"}]]]]]
     
     ;; Assets & Liabilities
     [:div.card
      [:h3.font-semibold.text-lg.mb-4.pb-2.border-b {:style {:color "#00857c"}}
       "Assets & Liabilities"]
      
      [:div.grid.grid-cols-2.gap-4
       [form-field {:label "Total Business Assets" :required true
                    :hint "Include equipment, inventory, accounts receivable, etc."}
        [:div.relative
         [:span.absolute.left-3.text-gray-500 {:style {:top "50%" :transform "translateY(-50%)"}} "$"]
         [:input.form-input.w-full.rounded-lg.border-gray-300.pl-7
          {:type "number"
           :value (:total-assets form-data)
           :on-change #(update-field! :total-assets %)
           :placeholder "200,000"}]]]
       
       [form-field {:label "Total Business Liabilities" :required true
                    :hint "Include all outstanding debts and obligations"}
        [:div.relative
         [:span.absolute.left-3.text-gray-500 {:style {:top "50%" :transform "translateY(-50%)"}} "$"]
         [:input.form-input.w-full.rounded-lg.border-gray-300.pl-7
          {:type "number"
           :value (:total-liabilities form-data)
           :on-change #(update-field! :total-liabilities %)
           :placeholder "50,000"}]]]]
      
      [form-field {:label "Current Monthly Debt Payments"
                   :hint "Total of all existing loan and credit payments"}
       [:div.relative
        [:span.absolute.left-3.text-gray-500 {:style {:top "50%" :transform "translateY(-50%)"}} "$"]
        [:input.form-input.w-full.rounded-lg.border-gray-300.pl-7
         {:type "number"
          :value (:existing-debt-payments form-data)
          :on-change #(update-field! :existing-debt-payments %)
          :placeholder "2,500"}]]]]
     
     ;; Collateral
     [:div.card
      [:h3.font-semibold.text-lg.mb-4.pb-2.border-b {:style {:color "#00857c"}}
       "Collateral"]
      
      [:div.mb-4
       [:label.flex.items-center.gap-3.cursor-pointer
        [:input.h-5.w-5.rounded
         {:type "checkbox"
          :checked (:collateral-offered form-data)
          :on-change #(update-bool! :collateral-offered %)}]
        [:span.text-gray-700 "I can offer collateral to secure this loan"]]]
      
      (when (:collateral-offered form-data)
        [:div.space-y-4.pl-8.border-l-2 {:style {:border-color "#00857c"}}
         [form-field {:label "Collateral Type"}
          [:select.form-select.w-full.rounded-lg.border-gray-300
           {:value (:collateral-type form-data)
            :on-change #(update-field! :collateral-type %)}
           [:option {:value ""} "Select type..."]
           [:option {:value "real-estate"} "Real Estate"]
           [:option {:value "equipment"} "Equipment / Machinery"]
           [:option {:value "inventory"} "Inventory"]
           [:option {:value "accounts-receivable"} "Accounts Receivable"]
           [:option {:value "vehicles"} "Vehicles"]
           [:option {:value "securities"} "Securities / Investments"]
           [:option {:value "cash"} "Cash / CD"]
           [:option {:value "other"} "Other Assets"]]]
         
         [form-field {:label "Description of Collateral"}
          [:textarea.form-textarea.w-full.rounded-lg.border-gray-300
           {:rows 2
            :value (:collateral-description form-data)
            :on-change #(update-field! :collateral-description %)
            :placeholder "Describe the collateral..."}]]
         
         [form-field {:label "Estimated Value"}
          [:div.relative
           [:span.absolute.left-3.text-gray-500 {:style {:top "50%" :transform "translateY(-50%)"}} "$"]
           [:input.form-input.w-full.rounded-lg.border-gray-300.pl-7
            {:type "number"
             :value (:collateral-value form-data)
             :on-change #(update-field! :collateral-value %)
             :placeholder "150,000"}]]]])]
     
     ;; Disclosures
     [:div.card
      [:h3.font-semibold.text-lg.mb-4.pb-2.border-b {:style {:color "#00857c"}}
       "Required Disclosures"]
      
      [:div.space-y-4
       [:div
        [:label.flex.items-start.gap-3.cursor-pointer
         [:input.h-5.w-5.rounded.mt-0.5
          {:type "checkbox"
           :checked (:bankruptcy-history form-data)
           :on-change #(update-bool! :bankruptcy-history %)}]
         [:span.text-gray-700 
          "Has the business or any owner filed for bankruptcy in the past 7 years?"]]
        (when (:bankruptcy-history form-data)
          [:div.mt-2.ml-8
           [:textarea.form-textarea.w-full.rounded-lg.border-gray-300
            {:rows 2
             :value (:bankruptcy-details form-data)
             :on-change #(update-field! :bankruptcy-details %)
             :placeholder "Please provide details..."}]])]
       
       [:div
        [:label.flex.items-start.gap-3.cursor-pointer
         [:input.h-5.w-5.rounded.mt-0.5
          {:type "checkbox"
           :checked (:tax-liens form-data)
           :on-change #(update-bool! :tax-liens %)}]
         [:span.text-gray-700 
          "Are there any outstanding tax liens against the business?"]]]
       
       [:div
        [:label.flex.items-start.gap-3.cursor-pointer
         [:input.h-5.w-5.rounded.mt-0.5
          {:type "checkbox"
           :checked (:pending-lawsuits form-data)
           :on-change #(update-bool! :pending-lawsuits %)}]
         [:span.text-gray-700 
          "Is the business currently involved in any pending lawsuits?"]]]]]
     
     ;; Navigation
     [:div.flex.justify-between.pt-4
      [:button.font-medium.py-3.px-6.rounded-lg.transition-all
       {:style {:color "#00857c" :border "2px solid #00857c"}
        :on-click state/go-back!}
       "← Back"]
      [:button.font-bold.py-3.px-8.rounded-lg.text-white.transition-all
       {:style {:background-color "#00857c"}
        :on-click state/go-next!}
       "Continue →"]]]))
