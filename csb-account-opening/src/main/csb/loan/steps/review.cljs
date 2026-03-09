(ns csb.loan.steps.review
  (:require [clojure.string :as str]
            [csb.loan.state :as state]
            [csb.components.ui.form-field :refer [form-field]]))

(defn data-row [label value]
  [:div.flex.justify-between.py-2.border-b.border-gray-100.last:border-0
   [:span.text-gray-500.text-sm label]
   [:span.font-medium.text-gray-800.text-sm.text-right (if (str/blank? value) "—" value)]])

(defn section-card [title on-edit children]
  [:div.card
   [:div.flex.items-center.justify-between.border-b.pb-3.mb-4
    [:h3.font-bold.text-gray-700 title]
    [:button.text-sm.font-semibold.flex.items-center.gap-1
     {:style {:color "#00857c"}
      :on-click on-edit}
     "Edit"]]
   children])

(defn format-currency [val]
  (if (str/blank? val)
    "—"
    (str "$" val)))

(defn get-loan-type-label [val]
  (let [types {"term-loan" "Term Loan"
               "line-of-credit" "Business Line of Credit"
               "sba-7a" "SBA 7(a) Loan"
               "sba-504" "SBA 504 Loan"
               "equipment" "Equipment Financing"
               "commercial-real-estate" "Commercial Real Estate Loan"
               "working-capital" "Working Capital Loan"}]
    (get types val val)))

(defn get-loan-purpose-label [val]
  (let [purposes {"working-capital" "Working Capital / Cash Flow"
                  "equipment" "Equipment Purchase"
                  "real-estate" "Real Estate Purchase"
                  "expansion" "Business Expansion"
                  "inventory" "Inventory Purchase"
                  "refinance" "Debt Refinancing"
                  "acquisition" "Business Acquisition"
                  "startup" "Startup Costs"
                  "renovation" "Renovation / Construction"
                  "other" "Other"}]
    (get purposes val val)))

(def agreements
  [{:key :agree-credit-check
    :label "Credit Check Authorization"
    :text "I authorize Cambridge Savings Bank and its partners to obtain credit reports for myself and any guarantors listed in this application."}
   {:key :agree-terms
    :label "Terms and Conditions"
    :text "I have read and agree to Cambridge Savings Bank's loan terms and conditions."}
   {:key :agree-esign
    :label "E-Sign Consent"
    :text "I consent to receive documents and disclosures electronically."}
   {:key :agree-privacy
    :label "Privacy Policy"
    :text "I acknowledge that I have received and reviewed the Privacy Policy."}
   {:key :certify-accurate
    :label "Certification of Accuracy"
    :text "I certify that all information provided in this application is true, complete, and accurate to the best of my knowledge."}])

(defn review-step [form-data]
  (let [all-agree (every? #(get form-data (:key %)) agreements)]
    [:div.space-y-6
     ;; Header
     [:div.card
      [:h2.text-xl.font-bold.mb-2 {:style {:color "#333"}} "Review Your Application"]
      [:p.text-gray-500.text-sm
       "Please review all information below before submitting. Click \"Edit\" on any section to make changes."]]
     
     ;; Loan Request Summary
     [section-card "Loan Request" #(state/go-to-step! 1)
      [:div.space-y-1
       [data-row "Loan Type" (get-loan-type-label (:loan-type form-data))]
       [data-row "Requested Amount" (format-currency (:loan-amount form-data))]
       [data-row "Loan Purpose" (get-loan-purpose-label (:loan-purpose form-data))]
       [data-row "Term" (when-not (str/blank? (:loan-term form-data))
                          (str (:loan-term form-data) " months"))]
       [data-row "Funding Timeline" (:urgency form-data)]]]
     
     ;; Business Information Summary
     [section-card "Business Information" #(state/go-to-step! 2)
      [:div.space-y-1
       [data-row "Legal Name" (:business-legal-name form-data)]
       [data-row "DBA" (:dba-name form-data)]
       [data-row "Entity Type" (:business-type form-data)]
       [data-row "EIN" (:ein form-data)]
       [data-row "State of Incorporation" (:state-of-incorporation form-data)]
       [data-row "Years in Business" (:years-in-business form-data)]
       [data-row "Industry" (:industry form-data)]
       [data-row "Business Address" (str (:business-address form-data) ", "
                                         (:business-city form-data) ", "
                                         (:business-state form-data) " "
                                         (:business-zip form-data))]]]
     
     ;; Financial Information Summary
     [section-card "Financial Information" #(state/go-to-step! 3)
      [:div.space-y-1
       [data-row "Annual Revenue" (format-currency (:annual-revenue form-data))]
       [data-row "Net Income" (format-currency (:net-income form-data))]
       [data-row "Total Assets" (format-currency (:total-assets form-data))]
       [data-row "Total Liabilities" (format-currency (:total-liabilities form-data))]
       [data-row "Cash on Hand" (format-currency (:cash-on-hand form-data))]
       [data-row "Monthly Debt Payments" (format-currency (:current-debt-payments form-data))]]]
     
     ;; Guarantor Information Summary
     [section-card "Primary Guarantor" #(state/go-to-step! 4)
      [:div.space-y-1
       [data-row "Name" (str (:primary-first-name form-data) " " (:primary-last-name form-data))]
       [data-row "Title" (:primary-title form-data)]
       [data-row "Ownership %" (when-not (str/blank? (:primary-ownership-pct form-data))
                                 (str (:primary-ownership-pct form-data) "%"))]
       [data-row "SSN" (when-not (str/blank? (:primary-ssn form-data)) "•••-••-••••")]
       [data-row "Date of Birth" (:primary-dob form-data)]
       [data-row "Home Address" (str (:primary-address form-data) ", "
                                     (:primary-city form-data) ", "
                                     (:primary-state form-data) " "
                                     (:primary-zip form-data))]]]
     
     ;; Additional Guarantors
     (when (seq (:guarantors form-data))
       [:div.card
        [:h3.font-bold.text-gray-700.border-b.pb-3.mb-4 "Additional Guarantors"]
        (for [[i g] (map-indexed vector (:guarantors form-data))]
          ^{:key i}
          [:div.mb-3.pb-3.border-b.border-gray-100.last:border-0
           [:p.font-semibold.text-sm.mb-2 {:style {:color "#00857c"}} (str "Guarantor #" (inc i))]
           [data-row "Name" (str (:first-name g) " " (:last-name g))]
           [data-row "Ownership %" (str (:ownership-pct g) "%")]])])
     
     ;; Collateral Summary
     [section-card "Collateral" #(state/go-to-step! 5)
      [:div.space-y-1
       [data-row "Collateral Offered" (if (:collateral-offered form-data) "Yes" "No (Unsecured)")]
       (when (:collateral-offered form-data)
         [:<>
          [data-row "Type" (:collateral-type form-data)]
          [data-row "Description" (:collateral-description form-data)]
          [data-row "Estimated Value" (format-currency (:collateral-estimated-value form-data))]])]]
     
     ;; Documents Summary
     [section-card "Documents" #(state/go-to-step! 6)
      [:div.space-y-1
       [data-row "Documents Uploaded" (str (count (filter some? (vals (or (:uploaded-documents form-data) {})))) 
                                           " of " (count state/required-documents))]
       (when (seq (:document-notes form-data))
         [data-row "Notes" (:document-notes form-data)])]]
     
     ;; Agreements
     [:div.card.space-y-4
      [:h3.font-bold.text-gray-700.border-b.pb-3 "Agreements & Authorizations"]
      [:p.text-sm.text-gray-500.mb-4
       "Please read and agree to the following to submit your application."]
      
      (for [{:keys [key label text]} agreements]
        ^{:key key}
        [:label.flex.items-start.gap-3.cursor-pointer.p-3.rounded-lg.transition-colors
         {:class (if (key form-data) "bg-green-50" "hover:bg-gray-50")}
         [:input.mt-0.5.w-5.h-5.flex-shrink-0
          {:type "checkbox"
           :checked (key form-data)
           :style {:accent-color "#00857c"}
           :on-change #(state/update-form-data! {key (.. % -target -checked)})}]
         [:div
          [:div.font-semibold.text-sm.text-gray-800 label]
          [:div.text-sm.text-gray-600.mt-0.5 text]]])
      
      (when-not all-agree
        [:div.bg-yellow-50.border.border-yellow-200.rounded-lg.p-3.text-sm.text-yellow-800
         "⚠ Please agree to all terms above to submit your application."])]
     
     ;; FDIC / Legal
     [:div.text-xs.text-gray-400.leading-relaxed.text-center
      "Cambridge Savings Bank · Member FDIC · Equal Housing Lender"
      [:br]
      "1374 Massachusetts Ave, Cambridge, MA 02138 · 1-888-418-5626"]
     
     ;; Navigation
     [:div.flex.justify-between.gap-3
      [:button.font-semibold.py-3.px-6.rounded-lg.transition-all
       {:style {:border "2px solid #00857c" :color "#00857c"}
        :on-click state/go-back!}
       "← Back"]
      [:button.font-bold.py-3.px-8.rounded-lg.text-white.transition-all
       {:style {:background-color (if all-agree "#00857c" "#ccc")
                :cursor (if all-agree "pointer" "not-allowed")}
        :disabled (not all-agree)
        :on-click state/submit!}
       "Submit Application ✓"]]]))
