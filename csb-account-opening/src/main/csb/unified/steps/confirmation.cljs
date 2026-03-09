(ns csb.unified.steps.confirmation
  (:require [csb.unified.state :as state]))

(defn generate-confirmation-number [prefix]
  (str prefix "-" (.substring (.toUpperCase (.toString (random-uuid))) 0 8)))

(defn confirmation-step []
  (let [form-data (:form-data @state/app-state)
        flow-type (:flow-type form-data)
        loan-decision (:loan-decision form-data)
        is-loan-flow (not= flow-type :account-only)
        loan-approved? (= loan-decision :approved)
        has-accounts? (and (not= loan-decision :denied)
                           (seq (:selected-accounts form-data)))
        
        today (.toLocaleDateString (js/Date.)
                                   "en-US"
                                   #js {:weekday "long"
                                        :year "numeric"
                                        :month "long"
                                        :day "numeric"})]
    
    [:div.space-y-6
     ;; Success Header
     [:div.card.text-center.py-8
      [:div.w-20.h-20.rounded-full.flex.items-center.justify-center.mx-auto.mb-4
       {:class (if (= loan-decision :denied) "bg-yellow-100" "bg-green-100")}
       (if (= loan-decision :denied)
         [:span.text-4xl "📋"]
         [:svg.w-10.h-10.text-green-600 {:fill "none" :viewBox "0 0 24 24" :stroke "currentColor"}
          [:path {:stroke-linecap "round" :stroke-linejoin "round" :stroke-width 2.5
                  :d "M5 13l4 4L19 7"}]])]
      [:h1.text-2xl.font-bold.text-gray-900.mb-2 
       (cond
         (= loan-decision :denied) "Application Received"
         loan-approved? "Congratulations!"
         :else "Application Submitted!")]
      [:p.text-gray-600
       (cond
         (= loan-decision :denied)
         "Thank you for applying. We were unable to approve your loan at this time."
         
         loan-approved?
         (str "Your loan has been pre-approved and your accounts are being opened!")
         
         :else
         "Your application has been received and is being processed.")]
      [:p.text-sm.text-gray-400.mt-3 today]]
     
     ;; Loan Confirmation (if applicable)
     (when (and is-loan-flow loan-approved?)
       (let [loan-conf (generate-confirmation-number "LOAN")]
         [:div.card
          [:div.flex.items-center.gap-3.mb-4
           [:div.text-2xl "💰"]
           [:h2.font-bold.text-lg {:style {:color "#00857c"}} "Loan Pre-Approval"]]
          [:div.rounded-lg.p-4.mb-4 {:style {:background-color "rgba(0, 133, 124, 0.1)"}}
           [:div.text-center
            [:div.text-xs.uppercase.tracking-wider.text-gray-500.mb-1 "Confirmation Number"]
            [:div.text-xl.font-bold {:style {:color "#00857c"}} loan-conf]]]
          [:div.grid.grid-cols-3.gap-4.text-center.text-sm
           [:div
            [:p.text-gray-500 "Amount"]
            [:p.font-semibold (str "$" (:approved-amount form-data))]]
           [:div
            [:p.text-gray-500 "Rate"]
            [:p.font-semibold (:approved-rate form-data)]]
           [:div
            [:p.text-gray-500 "Term"]
            [:p.font-semibold (str (:approved-term form-data) " mo")]]]
          [:div.mt-4.pt-4.border-t.border-gray-100
           [:h4.font-medium.text-gray-700.mb-2 "Next Steps for Your Loan:"]
           [:ol.text-sm.text-gray-600.space-y-1
            [:li "1. Final underwriting review (1-2 business days)"]
            [:li "2. Loan documents will be sent for signature"]
            [:li "3. Funds disbursed after closing"]]]]))
     
     ;; Account Confirmation (if applicable)
     (when has-accounts?
       (let [acct-conf (generate-confirmation-number "ACCT")]
         [:div.card
          [:div.flex.items-center.gap-3.mb-4
           [:div.text-2xl "🏦"]
           [:h2.font-bold.text-lg {:style {:color "#00857c"}} "Account Opening"]]
          [:div.rounded-lg.p-4.mb-4 {:style {:background-color "rgba(0, 133, 124, 0.1)"}}
           [:div.text-center
            [:div.text-xs.uppercase.tracking-wider.text-gray-500.mb-1 "Confirmation Number"]
            [:div.text-xl.font-bold {:style {:color "#00857c"}} acct-conf]]]
          [:div
           [:h4.font-medium.text-gray-700.mb-2 "Accounts Being Opened:"]
           [:ul.space-y-2
            (for [account-id (:selected-accounts form-data)]
              (let [product (first (filter #(= (:id %) account-id) state/account-products))]
                ^{:key account-id}
                [:li.flex.items-center.gap-2.text-gray-700
                 [:span {:style {:color "#00857c"}} "✓"]
                 [:span.font-medium (:name product)]
                 (when (and loan-approved? (= account-id "business-checking"))
                   [:span.text-xs.text-gray-500 "(for loan payments)"])]))]]
          [:div.mt-4.pt-4.border-t.border-gray-100
           [:h4.font-medium.text-gray-700.mb-2 "Next Steps for Your Accounts:"]
           [:ol.text-sm.text-gray-600.space-y-1
            [:li "1. Account verification (same business day)"]
            [:li "2. Welcome email with online banking access"]
            [:li "3. Debit card mailed within 7-10 days"]]]]))
     
     ;; Denied loan - no accounts opened
     (when (= loan-decision :denied)
       [:div.card
        [:div.flex.items-center.gap-3.mb-4
         [:div.text-2xl "ℹ️"]
         [:h2.font-bold.text-lg.text-gray-700 "Application Status"]]
        [:p.text-gray-600.mb-4
         "Since your loan application was not approved, no accounts have been opened at this time."]
        [:div.rounded-lg.p-4 {:style {:background-color "#fef3c7"}}
         [:h4.font-medium.text-yellow-800.mb-2 "Your Options:"]
         [:ul.text-sm.text-yellow-700.space-y-1
          [:li "• Call us to discuss your loan decision: 1-888-418-5626"]
          [:li "• Apply for a business account separately"]
          [:li "• Reapply for a loan in 6 months"]]]])
     
     ;; Contact Information
     [:div.rounded-xl.overflow-hidden
      {:style {:background "linear-gradient(135deg, #00857c 0%, #006b64 100%)"}}
      [:div.p-6.text-white.text-center
       [:h3.font-bold.text-xl.mb-2 "Questions?"]
       [:p.text-sm.mb-4 {:style {:color "rgba(255,255,255,0.85)"}}
        "Our business banking team is here to help."]
       [:div.flex.flex-wrap.justify-center.gap-3
        [:a.bg-white.font-bold.px-5.py-2.5.rounded-lg.text-sm
         {:href "tel:1-888-418-5626"
          :style {:color "#00857c"}}
         "📞 1-888-418-5626"]
        [:a.bg-transparent.border-2.border-white.text-white.font-bold.px-5.py-2.5.rounded-lg.text-sm
         {:href "mailto:businessbanking@cambridgesavings.com"}
         "✉️ Email Us"]]]]
     
     ;; Start Over
     [:div.text-center
      [:button.font-semibold.text-sm.hover:underline
       {:style {:color "#00857c"}
        :on-click state/start-over!}
       "Start a new application"]]]))
