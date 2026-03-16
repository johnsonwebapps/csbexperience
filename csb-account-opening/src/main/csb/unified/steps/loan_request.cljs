(ns csb.unified.steps.loan-request
  (:require [csb.unified.state :as state]))

(defn form-field [{:keys [label required]} & children]
  [:div.mb-4
   [:label.block.text-sm.font-medium.text-gray-700.mb-1
    label
    (when required [:span.text-red-500.ml-1 "*"])]
   (into [:div] children)])

(defn loan-request-step []
  (let [form-data (:form-data @state/app-state)
        update-field! #(state/update-form-data! {%1 (-> %2 .-target .-value)})]
    [:div.space-y-6
     [:div.card
      [:div.flex.items-center.gap-3.mb-4
       [:div.step-dot.active "3"]
       [:h2.text-xl.font-bold.text-gray-900.uppercase.tracking-wide
        {:style {:letter-spacing "1px"}}
        "Loan Request"]]
      [:p.text-gray-600.mb-6
       "Tell us about the financing you need. We offer a variety of loan products to help your business grow."]]
     
     ;; Loan Type Selection
     [:div.card
      [:h3.font-semibold.uppercase.tracking-wide.mb-4.pb-2.border-b {:style {:color "#00857c" :letter-spacing "1px"}}
       "What type of financing do you need?"]
      
      [form-field {:label "Loan Type" :required true}
       [:select.form-select.w-full.rounded.border-gray-300
        {:value (:loan-type form-data)
         :on-change #(update-field! :loan-type %)}
        (for [{:keys [value label]} state/loan-types]
          ^{:key value}
          [:option {:value value} label])]]
      
      ;; Loan type descriptions
      (when (not= "" (:loan-type form-data))
        [:div.rounded.p-4.mt-4 {:style {:background-color "#f0f5f4"}}
         (case (:loan-type form-data)
           "term-loan" 
           [:p.text-sm.text-gray-600 "Fixed payments over a set period. Ideal for major purchases or expansion projects."]
           "line-of-credit"
           [:p.text-sm.text-gray-600 "Flexible borrowing up to your limit. Only pay interest on what you use."]
           "sba-7a"
           [:p.text-sm.text-gray-600 "SBA-guaranteed loans up to $5M for various business purposes. Competitive rates and longer terms."]
           "sba-504"
           [:p.text-sm.text-gray-600 "SBA-guaranteed loans for major fixed assets like real estate or equipment. Up to 90% financing."]
           "equipment"
           [:p.text-sm.text-gray-600 "Finance equipment purchases with the equipment as collateral. 100% financing available."]
           "commercial-real-estate"
           [:p.text-sm.text-gray-600 "Purchase, refinance, or renovate commercial property. Competitive rates up to 25-year terms."]
           "working-capital"
           [:p.text-sm.text-gray-600 "Short-term financing for day-to-day operations, payroll, or inventory."]
           nil)])]
     
     ;; Loan Details
     [:div.card
      [:h3.font-semibold.uppercase.tracking-wide.mb-4.pb-2.border-b {:style {:color "#00857c" :letter-spacing "1px"}}
       "Loan Details"]
      
      [:div.grid.grid-cols-2.gap-4
       [form-field {:label "Requested Loan Amount" :required true}
        [:div.relative
         [:span.absolute.left-3.text-gray-500 {:style {:top "50%" :transform "translateY(-50%)"}} "$"]
         [:input.form-input.w-full.rounded.border-gray-300.pl-7
          {:type "number"
           :value (:loan-amount form-data)
           :on-change #(update-field! :loan-amount %)
           :placeholder "100,000"}]]]
       
       [form-field {:label "Desired Loan Term" :required true}
        [:select.form-select.w-full.rounded.border-gray-300
         {:value (:loan-term form-data)
          :on-change #(update-field! :loan-term %)}
         [:option {:value ""} "Select term..."]
         [:option {:value "12"} "12 months (1 year)"]
         [:option {:value "24"} "24 months (2 years)"]
         [:option {:value "36"} "36 months (3 years)"]
         [:option {:value "60"} "60 months (5 years)"]
         [:option {:value "84"} "84 months (7 years)"]
         [:option {:value "120"} "120 months (10 years)"]
         [:option {:value "180"} "180 months (15 years)"]
         [:option {:value "240"} "240 months (20 years)"]
         [:option {:value "300"} "300 months (25 years)"]]]]
      
      [form-field {:label "Primary Purpose" :required true}
       [:select.form-select.w-full.rounded.border-gray-300
        {:value (:loan-purpose form-data)
         :on-change #(update-field! :loan-purpose %)}
        (for [{:keys [value label]} state/loan-purposes]
          ^{:key value}
          [:option {:value value} label])]]
      
      [form-field {:label "Please describe how you will use the funds"}
       [:textarea.form-textarea.w-full.rounded.border-gray-300
        {:rows 3
         :value (:loan-purpose-detail form-data)
         :on-change #(update-field! :loan-purpose-detail %)
         :placeholder "Describe your specific use of funds..."}]]]
     
     ;; Timeline
     [:div.card
      [:h3.font-semibold.uppercase.tracking-wide.mb-4.pb-2.border-b {:style {:color "#00857c" :letter-spacing "1px"}}
       "Timeline"]
      
      [form-field {:label "When do you need the funds?"}
       [:select.form-select.w-full.rounded.border-gray-300
        {:value (:urgency form-data)
         :on-change #(update-field! :urgency %)}
        [:option {:value ""} "Select timeline..."]
        [:option {:value "immediate"} "As soon as possible"]
        [:option {:value "30-days"} "Within 30 days"]
        [:option {:value "60-days"} "Within 60 days"]
        [:option {:value "90-days"} "Within 90 days"]
        [:option {:value "exploring"} "Just exploring options"]]]]
     
     ;; Info about checking account requirement
     [:div.rounded.p-4 {:style {:background-color "rgba(0, 133, 124, 0.08)"
                                   :border "1px solid rgba(0, 133, 124, 0.2)"}}
      [:div.flex.gap-3
       [:div.text-xl "ℹ️"]
       [:div
        [:h4.font-semibold.mb-1 {:style {:color "#00857c"}} "Account Requirement"]
        [:p.text-sm.text-gray-600
         "If your loan is approved, a Cambridge Savings Bank Small Business Checking account will be required for loan payments. "
         "We'll set this up for you automatically at no additional cost."]]]]
     
     ;; Navigation
     [:div.flex.justify-between.pt-6
      [:button.btn-secondary
       {:on-click state/go-back!}
       "← Back"]
      [:button.btn-primary
       {:on-click state/go-next!}
       "Continue →"]]]))
