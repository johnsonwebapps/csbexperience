(ns csb.loan.steps.financial-info
  (:require [reagent.core :as r]
            [clojure.string :as str]
            [csb.loan.state :as state]
            [csb.components.ui.form-field :refer [form-field]]))

(defn currency-input [field form-data placeholder]
  [:div.relative
   [:span.absolute.left-3.text-gray-500.font-medium
    {:style {:top "50%" :transform "translateY(-50%)"}}
    "$"]
   [:input.form-input.pl-7 {:type "text"
                             :placeholder placeholder
                             :value (field form-data)
                             :on-change #(state/update-form-data!
                                          {field (.. % -target -value)})}]])

(defn financial-info-step [form-data]
  (let [errors (r/atom {})
        show-existing-loans (r/atom false)]
    (fn [form-data]
      (let [validate (fn []
                       (cond-> {}
                         (str/blank? (:annual-revenue form-data))
                         (assoc :annual-revenue "Annual revenue is required.")
                         
                         (str/blank? (:net-income form-data))
                         (assoc :net-income "Net income is required.")
                         
                         (str/blank? (:total-assets form-data))
                         (assoc :total-assets "Total assets is required.")
                         
                         (str/blank? (:total-liabilities form-data))
                         (assoc :total-liabilities "Total liabilities is required.")))
            handle-next (fn []
                          (let [e (validate)]
                            (reset! errors e)
                            (when (empty? e)
                              (state/go-next!))))
            set-field (fn [field]
                        (fn [e]
                          (state/update-form-data! {field (.. e -target -value)})))]
        [:div.space-y-6
         ;; Header
         [:div.card
          [:h2.text-xl.font-bold.mb-2 {:style {:color "#333"}} "Financial Information"]
          [:p.text-gray-500.text-sm
           "Provide your business's financial details. This information helps us assess creditworthiness and determine appropriate loan terms. "
           "Fields marked with " [:span.text-red-500 "*"] " are required."]]
         
         ;; Revenue & Profitability
         [:div.card.space-y-5
          [:h3.font-bold.text-gray-700.border-b.pb-2 "Revenue & Profitability"]
          
          [:div.grid.sm:grid-cols-2.gap-5
           [form-field {:label "Annual Revenue (Current Year)" :required true :error (:annual-revenue @errors)}
            [currency-input :annual-revenue form-data "e.g., 1,500,000"]]
           
           [form-field {:label "Annual Revenue (Prior Year)"}
            [currency-input :annual-revenue-prior-year form-data "e.g., 1,200,000"]]]
          
          [:div.grid.sm:grid-cols-2.gap-5
           [form-field {:label "Net Income (Current Year)" :required true :error (:net-income @errors)}
            [currency-input :net-income form-data "e.g., 150,000"]]
           
           [form-field {:label "Net Income (Prior Year)"}
            [currency-input :net-income-prior-year form-data "e.g., 120,000"]]]
          
          [form-field {:label "Gross Profit Margin (%)"}
           [:div.relative
            [:input.form-input.pr-8 {:type "number"
                                      :placeholder "e.g., 35"
                                      :min 0
                                      :max 100
                                      :value (:gross-profit-margin form-data)
                                      :on-change (set-field :gross-profit-margin)}]
            [:span.absolute.right-3.text-gray-500.font-medium
             {:style {:top "50%" :transform "translateY(-50%)"}}
             "%"]]]]
         
         ;; Balance Sheet
         [:div.card.space-y-5
          [:h3.font-bold.text-gray-700.border-b.pb-2 "Balance Sheet Summary"]
          
          [:div.grid.sm:grid-cols-2.gap-5
           [form-field {:label "Total Assets" :required true :error (:total-assets @errors)}
            [currency-input :total-assets form-data "e.g., 500,000"]]
           
           [form-field {:label "Total Liabilities" :required true :error (:total-liabilities @errors)}
            [currency-input :total-liabilities form-data "e.g., 200,000"]]]
          
          [:div.grid.sm:grid-cols-2.gap-5
           [form-field {:label "Cash on Hand"}
            [currency-input :cash-on-hand form-data "e.g., 50,000"]]
           
           [form-field {:label "Accounts Receivable"}
            [currency-input :accounts-receivable form-data "e.g., 75,000"]]]
          
          [:div.grid.sm:grid-cols-2.gap-5
           [form-field {:label "Accounts Payable"}
            [currency-input :accounts-payable form-data "e.g., 45,000"]]
           
           [form-field {:label "Inventory Value"}
            [currency-input :inventory-value form-data "e.g., 100,000"]]]]
         
         ;; Existing Debt
         [:div.card.space-y-5
          [:h3.font-bold.text-gray-700.border-b.pb-2 "Existing Debt Obligations"]
          
          [form-field {:label "Current Monthly Debt Payments" :hint "Total of all existing loan payments"}
           [currency-input :current-debt-payments form-data "e.g., 5,000"]]
          
          [:label.flex.items-center.gap-3.cursor-pointer.mt-2
           [:input.w-5.h-5 {:type "checkbox"
                           :checked @show-existing-loans
                           :on-change #(reset! show-existing-loans (.. % -target -checked))}]
           [:span.text-gray-700 "I have existing business loans to report"]]
          
          (when @show-existing-loans
            [:div.mt-4.pt-4.border-t.space-y-4
             [:p.text-sm.text-gray-500 "List any existing business loans, lines of credit, or other debt:"]
             [:div.rounded-lg.p-4 {:style {:background-color "#f8f8f8"}}
              [:textarea.form-input {:rows 4
                                     :placeholder "Example:\n- Bank of America LOC: $50,000 limit, $25,000 balance, $500/mo payment\n- Equipment loan: $75,000 original, $45,000 remaining, $1,200/mo payment"
                                     :value (str (get form-data :existing-loans-text ""))
                                     :on-change #(state/update-form-data!
                                                  {:existing-loans-text (.. % -target -value)})}]]])]
         
         ;; Financial History
         [:div.card.space-y-5
          [:h3.font-bold.text-gray-700.border-b.pb-2 "Financial History"]
          [:p.text-sm.text-gray-500.mb-4 
           "Please disclose any financial issues. Honest disclosure helps us find the right solution for your business."]
          
          [:div.space-y-4
           [:label.flex.items-start.gap-3.cursor-pointer.p-3.rounded-lg.hover:bg-gray-50
            [:input.w-5.h-5.mt-0.5 {:type "checkbox"
                                    :checked (:bankruptcy-history form-data)
                                    :on-change #(state/update-form-data!
                                                 {:bankruptcy-history (.. % -target -checked)})}]
            [:div
             [:span.text-gray-700.font-medium "Has the business or any owner filed for bankruptcy in the last 7 years?"]]]
           
           (when (:bankruptcy-history form-data)
             [:div.ml-8
              [form-field {:label "Please provide details"}
               [:textarea.form-input {:rows 2
                                      :placeholder "Type of bankruptcy, year filed, discharge status..."
                                      :value (:bankruptcy-details form-data)
                                      :on-change (set-field :bankruptcy-details)}]]])
           
           [:label.flex.items-start.gap-3.cursor-pointer.p-3.rounded-lg.hover:bg-gray-50
            [:input.w-5.h-5.mt-0.5 {:type "checkbox"
                                    :checked (:tax-liens form-data)
                                    :on-change #(state/update-form-data!
                                                 {:tax-liens (.. % -target -checked)})}]
            [:div
             [:span.text-gray-700.font-medium "Are there any outstanding tax liens against the business?"]]]
           
           (when (:tax-liens form-data)
             [:div.ml-8
              [form-field {:label "Please provide details"}
               [:textarea.form-input {:rows 2
                                      :placeholder "Amount, taxing authority, payment arrangement..."
                                      :value (:tax-lien-details form-data)
                                      :on-change (set-field :tax-lien-details)}]]])
           
           [:label.flex.items-start.gap-3.cursor-pointer.p-3.rounded-lg.hover:bg-gray-50
            [:input.w-5.h-5.mt-0.5 {:type "checkbox"
                                    :checked (:pending-lawsuits form-data)
                                    :on-change #(state/update-form-data!
                                                 {:pending-lawsuits (.. % -target -checked)})}]
            [:div
             [:span.text-gray-700.font-medium "Are there any pending lawsuits or judgments against the business?"]]]
           
           (when (:pending-lawsuits form-data)
             [:div.ml-8
              [form-field {:label "Please provide details"}
               [:textarea.form-input {:rows 2
                                      :placeholder "Nature of lawsuit, parties involved, status..."
                                      :value (:lawsuit-details form-data)
                                      :on-change (set-field :lawsuit-details)}]]])]]
         
         ;; Navigation
         [:div.flex.justify-between.gap-3
          [:button.font-semibold.py-3.px-6.rounded-lg.transition-all
           {:style {:border "2px solid #00857c" :color "#00857c"}
            :on-click state/go-back!}
           "← Back"]
          [:button.font-bold.py-3.px-8.rounded-lg.text-white.transition-all
           {:style {:background-color "#00857c"}
            :on-click handle-next}
           "Continue →"]]]))))
