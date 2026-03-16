(ns csb.unified.steps.review
  (:require [csb.unified.state :as state]))

(defn section-card [title edit-step children]
  [:div.card.mb-4
   [:div.flex.items-center.justify-between.mb-4.pb-2.border-b
    [:h3.font-semibold.uppercase.tracking-wide {:style {:color "#00857c" :letter-spacing "1px"}} title]
    [:button.btn-text.text-sm
     {:on-click #(state/go-to-step! edit-step)}
     "Edit"]]
   children])

(defn data-row [label value]
  [:div.flex.justify-between.py-2.border-b.border-gray-100.last:border-0
   [:span.text-gray-500.text-sm label]
   [:span.font-medium.text-sm.text-right {:style {:max-width "60%"}} 
    (if (empty? (str value)) "—" value)]])

(defn review-step []
  (let [form-data (:form-data @state/app-state)
        flow-type (:flow-type form-data)
        loan-decision (:loan-decision form-data)
        continue-with-account? (:continue-with-account-after-denial form-data)
        is-loan-flow (not= flow-type :account-only)
        has-accounts? (or (not= loan-decision :denied) continue-with-account?)
        update-bool! #(state/update-form-data! {%1 (-> %2 .-target .-checked)})
        
        all-agreed? (and (:agree-terms form-data)
                         (:agree-esign form-data)
                         (:agree-privacy form-data)
                         (:certify-accurate form-data)
                         (if is-loan-flow (:agree-credit-check form-data) true))]
    
    [:div.space-y-6
     [:div.card
      [:div.flex.items-center.gap-3.mb-4
       [:div.step-dot.active
        (cond
          (= flow-type :account-only) "5"
          (and (= loan-decision :denied) (not continue-with-account?)) "8"
          :else "9")]
       [:h2.text-xl.font-bold.text-gray-900.uppercase.tracking-wide 
        {:style {:letter-spacing "1px"}}
        "Review & Submit"]]
      [:p.text-gray-600.mb-6
       "Please review your information carefully before submitting."]]
     
     ;; Business Information
     [section-card "Business Information" :business-info
      [:div
       [data-row "Legal Name" (:business-legal-name form-data)]
       [data-row "DBA" (:dba-name form-data)]
       [data-row "Business Type" (:business-type form-data)]
       [data-row "EIN" (:ein form-data)]
       [data-row "Phone" (:business-phone form-data)]
       [data-row "Address" (str (:business-address form-data) ", " 
                                (:business-city form-data) ", " 
                                (:business-state form-data) " " 
                                (:business-zip form-data))]]]
     
     ;; Owner Information
     [section-card "Owner Information" :owner-info
      [:div
       [data-row "Name" (str (:owner-first-name form-data) " " (:owner-last-name form-data))]
       [data-row "Title" (:owner-title form-data)]
       [data-row "Ownership" (str (:owner-ownership-pct form-data) "%")]
       [data-row "Email" (:owner-email form-data)]
       [data-row "Phone" (:owner-phone form-data)]
       [data-row "SSN" "•••-••-••••"]
       [data-row "Address" (str (:owner-address form-data) ", "
                                (:owner-city form-data) ", "
                                (:owner-state form-data) " "
                                (:owner-zip form-data))]]]
     
     ;; Loan Information (if applicable)
     (when is-loan-flow
       [section-card "Loan Request" :loan-request
        [:div
         [data-row "Loan Type" (:loan-type form-data)]
         [data-row "Amount Requested" (str "$" (:loan-amount form-data))]
         [data-row "Term" (str (:loan-term form-data) " months")]
         [data-row "Purpose" (:loan-purpose form-data)]
         (when (= loan-decision :approved)
           [:div.mt-4.p-3.rounded {:style {:background-color "rgba(0, 133, 124, 0.1)"
                                            :border-left "4px solid #00857c"}}
            [:p.font-semibold.text-sm {:style {:color "#00857c"}} "✓ Pre-Approved"]
            [:p.text-sm.text-gray-600 
             (str "Amount: $" (:approved-amount form-data) 
                  " | Rate: " (:approved-rate form-data)
                  " | Term: " (:approved-term form-data) " months")]])
         (when (= loan-decision :denied)
           [:div.mt-4.p-3.rounded {:style {:background-color "#fef2f2"
                                            :border-left "4px solid #c41230"}}
            [:p.font-semibold.text-sm.text-red-700 "✗ Loan Not Approved"]
            [:p.text-sm.text-gray-600 
             (if continue-with-account?
               "You've chosen to continue with opening a business account."
               "You've chosen not to open a business account at this time.")]])]])
     
     ;; Account Selection - show if approved OR if denied but continuing with account
     (when (and has-accounts? (seq (:selected-accounts form-data)))
       [section-card "Selected Accounts" :account-selection
        [:div
         (for [account-id (:selected-accounts form-data)]
           (let [product (first (filter #(= (:id %) account-id) state/account-products))]
             ^{:key account-id}
             [:div.flex.items-center.gap-2.py-2
              [:span {:style {:color "#00857c"}} "✓"]
              [:span.font-medium (:name product)]]))]])
     
     ;; Agreements
     [:div.card
      [:h3.font-semibold.uppercase.tracking-wide.mb-4.pb-2.border-b 
       {:style {:color "#00857c" :letter-spacing "1px"}}
       "Agreements & Disclosures"]
      
      [:div.space-y-4
       (when is-loan-flow
         [:label.flex.items-start.gap-3.cursor-pointer.p-2.rounded.hover:bg-gray-50.transition-colors
          [:input.mt-1.form-checkbox
           {:type "checkbox"
            :checked (:agree-credit-check form-data)
            :on-change #(update-bool! :agree-credit-check %)}]
          [:span.text-sm.text-gray-700
           "I authorize Cambridge Savings Bank to obtain credit reports and verify my identity and the information I have provided."]])
       
       [:label.flex.items-start.gap-3.cursor-pointer.p-2.rounded.hover:bg-gray-50.transition-colors
        [:input.mt-1.form-checkbox
         {:type "checkbox"
          :checked (:agree-terms form-data)
          :on-change #(update-bool! :agree-terms %)}]
        [:span.text-sm.text-gray-700
         "I have read and agree to the "
         [:a.font-semibold.hover:underline {:href "#" :style {:color "#00857c"}} "Terms and Conditions"]
         " and "
         [:a.font-semibold.hover:underline {:href "#" :style {:color "#00857c"}} "Account Agreement"]
         "."]]
       
       [:label.flex.items-start.gap-3.cursor-pointer.p-2.rounded.hover:bg-gray-50.transition-colors
        [:input.mt-1.form-checkbox
         {:type "checkbox"
          :checked (:agree-esign form-data)
          :on-change #(update-bool! :agree-esign %)}]
        [:span.text-sm.text-gray-700
         "I consent to receive documents and communications electronically (E-Sign Act consent)."]]
       
       [:label.flex.items-start.gap-3.cursor-pointer.p-2.rounded.hover:bg-gray-50.transition-colors
        [:input.mt-1.form-checkbox
         {:type "checkbox"
          :checked (:agree-privacy form-data)
          :on-change #(update-bool! :agree-privacy %)}]
        [:span.text-sm.text-gray-700
         "I acknowledge receipt of the "
         [:a.font-semibold.hover:underline {:href "#" :style {:color "#00857c"}} "Privacy Policy"]
         "."]]
       
       [:label.flex.items-start.gap-3.cursor-pointer.p-2.rounded.hover:bg-gray-50.transition-colors
        [:input.mt-1.form-checkbox
         {:type "checkbox"
          :checked (:certify-accurate form-data)
          :on-change #(update-bool! :certify-accurate %)}]
        [:span.text-sm.text-gray-700
         "I certify that all information provided in this application is true, accurate, and complete."]]]]
     
     ;; Navigation
     [:div.flex.justify-between.pt-6
      [:button.btn-secondary
       {:on-click state/go-back!}
       "← Back"]
      [:button.btn-primary
       {:disabled (not all-agreed?)
        :on-click state/go-next!}
       "Submit Application"]]]))
