(ns csb.components.steps.review
  (:require [csb.state :as state]
            [csb.utils :as utils]))

(defn review-section [{:keys [title step on-edit]} & children]
  [:div.card
   [:div.flex.items-center.justify-between.border-b.pb-3.mb-4
    [:h3.font-bold.text-gray-800.text-lg title]
    [:button.text-csb-teal.text-sm.font-semibold.hover:underline.flex.items-center.gap-1
     {:on-click #(on-edit step)}
     [:svg.w-4.h-4 {:fill "none" :viewBox "0 0 24 24" :stroke "currentColor"}
      [:path {:stroke-linecap "round" :stroke-linejoin "round" :stroke-width 2
             :d "M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"}]]
     "Edit"]]
   (into [:<>] children)])

(defn data-row [label value]
  (when value
    [:div.flex.justify-between.py-1.5.text-sm.border-b.border-gray-50.last:border-0
     [:span.text-gray-500.font-medium label]
     [:span.text-gray-900.font-semibold.text-right.max-w-xs value]]))

(defn review-step [form-data]
  (let [all-agree (and (:agree-terms form-data)
                       (:agree-esign form-data)
                       (:agree-privacy form-data))
        
        agreements [{:key :agree-terms
                    :label "Account Terms & Conditions"
                    :text "I have read and agree to the Cambridge Savings Bank Business Account Terms and Conditions, including the Business Account Agreement and Fee Schedule."}
                   {:key :agree-esign
                    :label "Electronic Communications (E-Sign)"
                    :text "I consent to receive account disclosures, statements, and communications electronically. I confirm I have the ability to access electronic documents."}
                   {:key :agree-privacy
                    :label "Privacy Notice"
                    :text "I acknowledge receipt of the Cambridge Savings Bank Privacy Notice explaining how we collect, share, and protect your personal information."}]]
    
    [:div.space-y-6
     [:div.card
      [:h2.section-header "Review Your Application"]
      [:p.text-gray-500.text-sm
       "Please review all information carefully. Click \"Edit\" on any section to make changes, or click \"Submit Application\" when everything looks correct."]]

     ;; Account selection
     [review-section {:title "Account Selection" :step 2 :on-edit state/go-to-step!}
      [data-row "Purpose" (get utils/purpose-map (:account-purpose form-data))]
      [data-row "Account Type" (get utils/products-map (:selected-product form-data))]]

     ;; Business info
     [review-section {:title "Business Information" :step 3 :on-edit state/go-to-step!}
      [data-row "Legal Business Name" (:business-name form-data)]
      (when (seq (:dba form-data))
        [data-row "DBA" (:dba form-data)])
      [data-row "Entity Type" (get utils/business-type-map (:business-type form-data))]
      [data-row "EIN" (:ein form-data)]
      [data-row "State of Formation" (:state-of-formation form-data)]
      [data-row "Date Established" (:date-established form-data)]
      [data-row "Business Phone" (:business-phone form-data)]
      [data-row "Business Address" (str (:business-address form-data) ", "
                                        (:business-city form-data) ", "
                                        (:business-state form-data) " "
                                        (:business-zip form-data))]
      (when (seq (:naics form-data))
        [data-row "NAICS Code" (:naics form-data)])
      [data-row "Business Description" (:business-description form-data)]]

     ;; Applicant info
     [review-section {:title "Primary Applicant" :step 4 :on-edit state/go-to-step!}
      [data-row "Full Name" (str (:first-name form-data) " " (:last-name form-data))]
      [data-row "Title" (:title form-data)]
      [data-row "Ownership" (when (seq (:ownership-pct form-data))
                             (str (:ownership-pct form-data) "%"))]
      [data-row "Email" (:email form-data)]
      [data-row "Phone" (:phone form-data)]
      [data-row "Date of Birth" (:dob form-data)]
      [data-row "SSN" (utils/mask-ssn (:ssn form-data))]
      [data-row "Home Address" (str (:address form-data) ", "
                                    (:city form-data) ", "
                                    (:state form-data) " "
                                    (:zip form-data))]
      [data-row "ID Type" (:id-type form-data)]
      [data-row "ID Number" (when (seq (:id-number form-data))
                             (str "•••••" (subs (:id-number form-data)
                                               (max 0 (- (count (:id-number form-data)) 4)))))]
      [data-row "ID Expiry" (:id-expiry form-data)]]

     ;; Beneficial owners
     [review-section {:title "Beneficial Ownership" :step 5 :on-edit state/go-to-step!}
      (if (empty? (:beneficial-owners form-data))
        [:p.text-sm.text-gray-500 "No additional beneficial owners added."]
        (for [[i owner] (map-indexed vector (:beneficial-owners form-data))]
          ^{:key i}
          [:div.mb-3.pb-3.border-b.border-gray-100.last:border-0
           [:p.font-semibold.text-csb-teal.text-sm.mb-2 (str "Owner #" (inc i))]
           [data-row "Name" (str (:first-name owner) " " (:last-name owner))]
           [data-row "Title" (:title owner)]
           [data-row "Ownership" (when (seq (:ownership-pct owner))
                                  (str (:ownership-pct owner) "%"))]
           [data-row "SSN" (utils/mask-ssn (:ssn owner))]]))
      [data-row "Certification" (if (:certify-beneficial-owners form-data)
                                  "✓ Certified"
                                  "⚠ Not certified")]]

     ;; Agreements
     [:div.card.space-y-4
      [:h3.font-bold.text-gray-800.text-lg.border-b.pb-3 "Agreements & Disclosures"]
      [:p.text-sm.text-gray-500
       "Please read and agree to the following to complete your application."]

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
         "⚠ Please agree to all disclosures above to submit your application."])]

     ;; FDIC / Legal
     [:div.text-xs.text-gray-400.leading-relaxed.text-center
      "Cambridge Savings Bank · Member FDIC · Equal Housing Lender"
      [:br]
      "1374 Massachusetts Ave, Cambridge, MA 02138 · 1-888-418-5626"
      [:br]
      "Your deposits are federally insured to at least $250,000 by the FDIC."]

     [:div.flex.justify-between.gap-3
      [:button.btn-secondary {:on-click state/go-back!} "← Back"]
      [:button.btn-primary.px-8
       {:on-click state/submit!
        :disabled (not all-agree)
        :style {:opacity (if all-agree 1 0.5)
                :cursor (if all-agree "pointer" "not-allowed")}}
       "Submit Application ✓"]]]))
