(ns csb.unified.steps.owner-info
  (:require [csb.unified.state :as state]
            [reagent.core :as r]))

(defn form-field [{:keys [label required]} & children]
  [:div.mb-4
   [:label.block.text-sm.font-medium.text-gray-700.mb-1
    label
    (when required [:span.text-red-500.ml-1 "*"])]
   (into [:div] children)])

(defn owner-info-step []
  (let [form-data (:form-data @state/app-state)
        flow-type (:flow-type form-data)
        needs-credit-check (not= flow-type :account-only)
        update-field! #(state/update-form-data! {%1 (-> %2 .-target .-value)})]
    [:div.space-y-6
     [:div.card
      [:div.flex.items-center.gap-3.mb-4
       [:div.step-dot.active "2"]
       [:h2.text-xl.font-bold.text-gray-900.uppercase.tracking-wide
        {:style {:letter-spacing "1px"}}
        "Owner Information"]]
      [:p.text-gray-600.mb-6
       "Tell us about the primary owner or authorized signer. "
       (when needs-credit-check
         "This information will be used for identity verification and credit assessment.")]]
     
     ;; Personal Information
     [:div.card
      [:h3.font-semibold.uppercase.tracking-wide.mb-4.pb-2.border-b 
       {:style {:color "#00857c" :letter-spacing "1px"}}
       "Personal Details"]
      
      [:div.grid.grid-cols-2.gap-4
       [form-field {:label "First Name" :required true}
        [:input.form-input.w-full.rounded.border-gray-300
         {:type "text"
          :value (:owner-first-name form-data)
          :on-change #(update-field! :owner-first-name %)
          :placeholder "John"}]]
       
       [form-field {:label "Last Name" :required true}
        [:input.form-input.w-full.rounded.border-gray-300
         {:type "text"
          :value (:owner-last-name form-data)
          :on-change #(update-field! :owner-last-name %)
          :placeholder "Smith"}]]]
      
      [:div.grid.grid-cols-2.gap-4
       [form-field {:label "Title/Position" :required true}
        [:input.form-input.w-full.rounded.border-gray-300
         {:type "text"
          :value (:owner-title form-data)
          :on-change #(update-field! :owner-title %)
          :placeholder "CEO, Owner, President, etc."}]]
       
       [form-field {:label "Ownership Percentage" :required true}
        [:input.form-input.w-full.rounded.border-gray-300
         {:type "number"
          :min 0
          :max 100
          :value (:owner-ownership-pct form-data)
          :on-change #(update-field! :owner-ownership-pct %)
          :placeholder "e.g., 51"}]]]]
     
     ;; Contact Information
     [:div.card
      [:h3.font-semibold.uppercase.tracking-wide.mb-4.pb-2.border-b {:style {:color "#00857c" :letter-spacing "1px"}}
       "Contact Information"]
      
      [:div.grid.grid-cols-2.gap-4
       [form-field {:label "Email Address" :required true}
        [:input.form-input.w-full.rounded.border-gray-300
         {:type "email"
          :value (:owner-email form-data)
          :on-change #(update-field! :owner-email %)
          :placeholder "john.smith@email.com"}]]
       
       [form-field {:label "Phone Number" :required true}
        [:input.form-input.w-full.rounded.border-gray-300
         {:type "tel"
          :value (:owner-phone form-data)
          :on-change #(update-field! :owner-phone %)
          :placeholder "(617) 555-0100"}]]]]
     
     ;; Identity Verification (SSN, DOB)
     [:div.card
      [:h3.font-semibold.uppercase.tracking-wide.mb-4.pb-2.border-b {:style {:color "#00857c" :letter-spacing "1px"}}
       "Identity Verification"]
      
      (when needs-credit-check
        [:div.rounded.p-3.mb-4 {:style {:background-color "#fef3c7"}}
         [:div.flex.gap-2
          [:span "⚠️"]
          [:p.text-sm.text-yellow-800
           "Your Social Security Number is required for credit verification. "
           "By continuing, you authorize Cambridge Savings Bank to obtain credit reports."]]])
      
      [:div.grid.grid-cols-2.gap-4
       [form-field {:label "Social Security Number" :required true}
        [:input.form-input.w-full.rounded.border-gray-300
         {:type "password"
          :value (:owner-ssn form-data)
          :on-change #(update-field! :owner-ssn %)
          :placeholder "XXX-XX-XXXX"
          :autoComplete "off"}]]
       
       [form-field {:label "Date of Birth" :required true}
        [:input.form-input.w-full.rounded.border-gray-300
         {:type "date"
          :value (:owner-dob form-data)
          :on-change #(update-field! :owner-dob %)}]]]]
     
     ;; Home Address
     [:div.card
      [:h3.font-semibold.uppercase.tracking-wide.mb-4.pb-2.border-b {:style {:color "#00857c" :letter-spacing "1px"}}
       "Home Address"]
      
      [form-field {:label "Street Address" :required true}
       [:input.form-input.w-full.rounded.border-gray-300
        {:type "text"
         :value (:owner-address form-data)
         :on-change #(update-field! :owner-address %)
         :placeholder "456 Oak Street"}]]
      
      [:div.grid.grid-cols-6.gap-4
       [:div.col-span-3
        [form-field {:label "City" :required true}
         [:input.form-input.w-full.rounded.border-gray-300
          {:type "text"
           :value (:owner-city form-data)
           :on-change #(update-field! :owner-city %)
           :placeholder "Boston"}]]]
       [:div.col-span-1
        [form-field {:label "State" :required true}
         [:input.form-input.w-full.rounded.border-gray-300
          {:type "text"
           :value (:owner-state form-data)
           :on-change #(update-field! :owner-state %)
           :placeholder "MA"
           :maxLength 2}]]]
       [:div.col-span-2
        [form-field {:label "ZIP Code" :required true}
         [:input.form-input.w-full.rounded.border-gray-300
          {:type "text"
           :value (:owner-zip form-data)
           :on-change #(update-field! :owner-zip %)
           :placeholder "02101"}]]]]]
     
     ;; ID Verification
     [:div.card
      [:h3.font-semibold.uppercase.tracking-wide.mb-4.pb-2.border-b {:style {:color "#00857c" :letter-spacing "1px"}}
       "Government-Issued ID"]
      
      [:div.grid.grid-cols-2.gap-4
       [form-field {:label "ID Type" :required true}
        [:select.form-select.w-full.rounded.border-gray-300
         {:value (:owner-id-type form-data)
          :on-change #(update-field! :owner-id-type %)}
         [:option {:value ""} "Select ID type..."]
         [:option {:value "drivers-license"} "Driver's License"]
         [:option {:value "state-id"} "State ID"]
         [:option {:value "passport"} "Passport"]
         [:option {:value "military-id"} "Military ID"]]]
       
       [form-field {:label "ID Number" :required true}
        [:input.form-input.w-full.rounded.border-gray-300
         {:type "text"
          :value (:owner-id-number form-data)
          :on-change #(update-field! :owner-id-number %)
          :placeholder "ID Number"}]]]
      
      [:div.grid.grid-cols-2.gap-4
       [form-field {:label "Issuing State"}
        [:input.form-input.w-full.rounded.border-gray-300
         {:type "text"
          :value (:owner-id-state form-data)
          :on-change #(update-field! :owner-id-state %)
          :placeholder "MA"
          :maxLength 2}]]
       
       [form-field {:label "Expiration Date" :required true}
        [:input.form-input.w-full.rounded.border-gray-300
         {:type "date"
          :value (:owner-id-expiry form-data)
          :on-change #(update-field! :owner-id-expiry %)}]]]]
     
     ;; Beneficial Ownership Certification
     [:div.card
      [:h3.font-semibold.uppercase.tracking-wide.mb-4.pb-2.border-b {:style {:color "#00857c" :letter-spacing "1px"}}
       "Beneficial Ownership"]
      
      [:div.rounded.p-4.mb-4 {:style {:background-color "#f0f5f4"}}
       [:p.text-sm.text-gray-600
        "Federal regulations require us to identify all individuals who own 25% or more of the business, "
        "or who have significant control over the business."]]
      
      [:label.flex.items-start.gap-3.cursor-pointer
       [:input.mt-1
        {:type "checkbox"
         :checked (:certify-beneficial-owners form-data)
         :on-change #(state/update-form-data! 
                      {:certify-beneficial-owners (-> % .-target .-checked)})}]
       [:span.text-sm.text-gray-700
        "I certify that I have provided information for all individuals who own 25% or more of the business, "
        "and that the information provided is true and accurate."]]]
     
     ;; Navigation
     [:div.flex.justify-between.pt-6
      [:button.btn-secondary
       {:on-click state/go-back!}
       "← Back"]
      [:button.btn-primary
       {:on-click state/go-next!}
       "Continue →"]]]))
