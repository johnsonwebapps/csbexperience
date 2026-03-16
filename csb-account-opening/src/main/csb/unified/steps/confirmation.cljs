(ns csb.unified.steps.confirmation
  (:require [csb.unified.state :as state]))

(defn generate-confirmation-number [prefix]
  (str prefix "-" (.substring (.toUpperCase (.toString (random-uuid))) 0 8)))

(defn confirmation-step []
  (let [form-data (:form-data @state/app-state)
        flow-type (:flow-type form-data)
        loan-decision (:loan-decision form-data)
        continue-with-account? (:continue-with-account-after-denial form-data)
        is-loan-flow (not= flow-type :account-only)
        loan-approved? (= loan-decision :approved)
        loan-denied? (= loan-decision :denied)
        ;; Has accounts if: not denied, OR denied but chose to continue with account
        has-accounts? (and (or (not loan-denied?) continue-with-account?)
                           (seq (:selected-accounts form-data)))
        
        today (.toLocaleDateString (js/Date.)
                                   "en-US"
                                   #js {:weekday "long"
                                        :year "numeric"
                                        :month "long"
                                        :day "numeric"})]
    
    [:div.space-y-6
     ;; Success Header with Pending Review Status
     [:div.card.text-center.py-8
      [:div.w-20.h-20.rounded-full.flex.items-center.justify-center.mx-auto.mb-4.bg-amber-100
       [:span.text-4xl "📋"]]
      [:h1.text-2xl.font-bold.text-gray-900.mb-2.uppercase.tracking-wide
       {:style {:letter-spacing "1px"}}
       "Application Submitted"]
      [:p.text-gray-600.mb-4
       "Thank you! Your application has been received and is pending review by our business banking team."]
      
      ;; Pending Review Badge
      [:div.inline-flex.items-center.gap-2.px-4.py-2.rounded.bg-amber-50.border.border-amber-200
       [:div.w-2.h-2.rounded-full.bg-amber-500.animate-pulse]
       [:span.text-amber-700.font-semibold.uppercase.text-sm.tracking-wide "Pending Human Review"]]
      
      [:p.text-sm.text-gray-400.mt-4 today]]
     
     ;; Review Timeline Card
     [:div.card
      [:div.flex.items-center.gap-3.mb-4
       [:div.text-2xl "⏱️"]
       [:h2.font-bold.uppercase.tracking-wide {:style {:color "#00857c" :letter-spacing "1px"}}
        "What Happens Next?"]]
      [:div.space-y-4
       ;; Step 1 - Current
       [:div.flex.gap-4
        [:div.flex.flex-col.items-center
         [:div.step-dot {:style {:background-color "#f59e0b" :color "white"}}
          "1"]
         [:div.w-0.5.h-full.bg-gray-200.mt-2]]
        [:div.pb-6
         [:p.font-semibold.text-amber-700 "Application Under Review"]
         [:p.text-sm.text-gray-600 "A business banker will review your application within 1-2 business days."]]]
       ;; Step 2
       [:div.flex.gap-4
        [:div.flex.flex-col.items-center
         [:div.step-dot.pending "2"]
         [:div.w-0.5.h-full.bg-gray-200.mt-2]]
        [:div.pb-6
         [:p.font-semibold.text-gray-700 "Verification & Processing"]
         [:p.text-sm.text-gray-600 "We may contact you if additional information is needed."]]]
       ;; Step 3
       [:div.flex.gap-4
        [:div.flex.flex-col.items-center
         [:div.step-dot.pending "3"]]
        [:div
         [:p.font-semibold.text-gray-700 "Decision & Next Steps"]
         [:p.text-sm.text-gray-600 "You'll receive an email with our decision and any required next steps."]]]]]
     
     ;; Loan Denial Notice (if denied but continuing with account)
     (when (and loan-denied? continue-with-account?)
       [:div.card
        [:div.flex.items-center.gap-3.mb-4
         [:div.text-2xl "ℹ️"]
         [:h2.font-bold.uppercase.tracking-wide.text-gray-700 {:style {:letter-spacing "1px"}}
          "Loan Application Status"]]
        [:div.rounded.p-4 {:style {:background-color "#fef2f2" :border-left "4px solid #c41230"}}
         [:p.text-red-700.font-medium.mb-2 "Loan Pre-Qualification: Not Approved"]
         [:p.text-sm.text-gray-600 
          "Based on the initial review, we were unable to pre-qualify your loan application. "
          "However, your business account application is being processed."]
         [:p.text-sm.text-gray-500.mt-2
          "You may appeal this decision or reapply in 6 months. Call us at 1-888-418-5626 for more information."]]])
     
     ;; Loan Confirmation (if applicable)
     (when (and is-loan-flow (not loan-denied?))
       (let [loan-conf (generate-confirmation-number "LOAN")]
         [:div.card
          [:div.flex.items-center.gap-3.mb-4
           [:div.text-2xl "💰"]
           [:h2.font-bold.uppercase.tracking-wide {:style {:color "#00857c" :letter-spacing "1px"}} 
            (if loan-approved? "Loan Pre-Qualified" "Loan Application")]]
          [:div.rounded.p-4.mb-4 {:style {:background-color "rgba(245, 158, 11, 0.1)"
                                           :border-left "4px solid #f59e0b"}}
           [:div.text-center
            [:div.text-xs.uppercase.tracking-wider.text-gray-500.mb-1 
             {:style {:letter-spacing "1px"}}
             "Reference Number"]
            [:div.text-xl.font-bold {:style {:color "#00857c"}} loan-conf]
            [:div.mt-2
             [:span.inline-flex.items-center.gap-1.px-2.py-1.rounded.text-xs.font-semibold.bg-amber-100.text-amber-700.uppercase
              {:style {:letter-spacing "0.5px"}}
              [:span.w-1.5.h-1.5.rounded-full.bg-amber-500]
              "Pending Review"]]]]
          (when loan-approved?
            [:div.grid.grid-cols-3.gap-4.text-center.text-sm.mb-4
             [:div
              [:p.text-gray-500.text-xs.uppercase {:style {:letter-spacing "0.5px"}} "Pre-Qualified Amount"]
              [:p.font-bold {:style {:color "#00857c"}} (str "$" (:approved-amount form-data))]]
             [:div
              [:p.text-gray-500.text-xs.uppercase {:style {:letter-spacing "0.5px"}} "Est. Rate"]
              [:p.font-bold {:style {:color "#00857c"}} (:approved-rate form-data)]]
             [:div
              [:p.text-gray-500.text-xs.uppercase {:style {:letter-spacing "0.5px"}} "Term"]
              [:p.font-bold {:style {:color "#00857c"}} (str (:approved-term form-data) " mo")]]])
          [:div.mt-4.pt-4.border-t.border-gray-100
           [:h4.font-semibold.text-gray-700.mb-2.uppercase.text-sm {:style {:letter-spacing "0.5px"}}
            "Loan Review Process:"]
           [:ol.text-sm.text-gray-600.space-y-1
            [:li "Business banker reviews your application"]
            [:li "Final underwriting and document verification"]
            [:li "Loan documents sent for signature upon approval"]
            [:li "Funds disbursed after closing"]]]]))
     
     ;; Account Confirmation (if applicable)
     (when has-accounts?
       (let [acct-conf (generate-confirmation-number "ACCT")]
         [:div.card
          [:div.flex.items-center.gap-3.mb-4
           [:div.text-2xl "🏦"]
           [:h2.font-bold.uppercase.tracking-wide {:style {:color "#00857c" :letter-spacing "1px"}} 
            "Account Application"]]
          [:div.rounded.p-4.mb-4 {:style {:background-color "rgba(245, 158, 11, 0.1)"
                                           :border-left "4px solid #f59e0b"}}
           [:div.text-center
            [:div.text-xs.uppercase.tracking-wider.text-gray-500.mb-1
             {:style {:letter-spacing "1px"}}
             "Reference Number"]
            [:div.text-xl.font-bold {:style {:color "#00857c"}} acct-conf]
            [:div.mt-2
             [:span.inline-flex.items-center.gap-1.px-2.py-1.rounded.text-xs.font-semibold.bg-amber-100.text-amber-700.uppercase
              {:style {:letter-spacing "0.5px"}}
              [:span.w-1.5.h-1.5.rounded-full.bg-amber-500]
              "Pending Review"]]]]
          [:div
           [:h4.font-semibold.text-gray-700.mb-2.uppercase.text-sm {:style {:letter-spacing "0.5px"}}
            "Accounts Requested:"]
           [:ul.space-y-2
            (for [account-id (:selected-accounts form-data)]
              (let [product (first (filter #(= (:id %) account-id) state/account-products))]
                ^{:key account-id}
                [:li.flex.items-center.gap-2.text-gray-700
                 [:span.text-amber-500 "○"]
                 [:span.font-medium (:name product)]
                 (when (and loan-approved? (= account-id "small-business-checking"))
                   [:span.text-xs.text-gray-500 "(for loan payments)"])]))]]
          [:div.mt-4.pt-4.border-t.border-gray-100
           [:h4.font-semibold.text-gray-700.mb-2.uppercase.text-sm {:style {:letter-spacing "0.5px"}}
            "Account Review Process:"]
           [:ol.text-sm.text-gray-600.space-y-1
            [:li "Business banker verifies business information"]
            [:li "Account approval notification sent via email"]
            [:li "Welcome email with online banking access"]
            [:li "Debit card mailed within 7-10 days of approval"]]]]))
     
     ;; Denied loan - no accounts opened (only if user declined to continue with account)
     (when (and loan-denied? (not continue-with-account?))
       [:div.card
        [:div.flex.items-center.gap-3.mb-4
         [:div.text-2xl "ℹ️"]
         [:h2.font-bold.uppercase.tracking-wide.text-gray-700 {:style {:letter-spacing "1px"}}
          "Application Status"]]
        [:p.text-gray-600.mb-4
         "Your loan application has been submitted for final review. Since you chose not to open a business account, only the loan application is being processed."]
        [:div.rounded.p-4 {:style {:background-color "#fef3c7" :border-left "4px solid #f59e0b"}}
         [:h4.font-semibold.text-yellow-800.mb-2.uppercase.text-sm {:style {:letter-spacing "0.5px"}}
          "Your Options:"]
         [:ul.text-sm.text-yellow-700.space-y-1
          [:li "• Wait for our team to complete the review"]
          [:li "• Call us to discuss your application: 1-888-418-5626"]
          [:li "• Apply for a business account separately"]]]])
     
     ;; Contact Information
     [:div.rounded.overflow-hidden
      {:style {:background "linear-gradient(135deg, #00857c 0%, #006b64 100%)"}}
      [:div.p-6.text-white.text-center
       [:h3.font-bold.text-xl.mb-2.uppercase {:style {:letter-spacing "1px"}}
        "Questions About Your Application?"]
       [:p.text-sm.mb-4 {:style {:color "rgba(255,255,255,0.85)"}}
        "Our business banking team is here to help with your application status."]
       [:div.flex.flex-wrap.justify-center.gap-3
        [:a.bg-white.font-bold.px-6.py-3.rounded.text-sm.uppercase.tracking-wide
         {:href "tel:1-888-418-5626"
          :style {:color "#00857c" :letter-spacing "1px"}}
         "📞 888.418.5626"]
        [:a.bg-transparent.border-2.border-white.text-white.font-bold.px-6.py-3.rounded.text-sm.uppercase.tracking-wide
         {:href "mailto:businessbanking@cambridgesavings.com"
          :style {:letter-spacing "1px"}}
         "✉️ Email Us"]]]]
     
     ;; Start Over
     [:div.text-center.pt-4
      [:button.btn-text
       {:on-click state/start-over!}
       "Start a new application"]]]))
