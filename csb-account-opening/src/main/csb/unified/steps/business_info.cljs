(ns csb.unified.steps.business-info
  (:require [csb.unified.state :as state]))

(defn verification-badge [kyc? kyb?]
  (cond
    (and kyc? kyb?) [:span.badge-kyc-kyb "KYC+KYB"]
    kyc? [:span.badge-kyc "KYC"]
    kyb? [:span.badge-kyb "KYB"]
    :else nil))

(defn form-field [{:keys [label required kyc kyb]} & children]
  [:div.mb-4
   [:label.block.text-sm.font-medium.text-gray-700.mb-1
    label
    (when required [:span.text-red-500.ml-1 "*"])
    [verification-badge kyc kyb]]
   (into [:div] children)])

(defn business-info-step []
  (let [form-data (:form-data @state/app-state)
        update-field! #(state/update-form-data! {%1 (-> %2 .-target .-value)})]
    [:div.space-y-6
     [:div.card
      [:div.flex.items-center.gap-3.mb-4
       [:div.step-dot.active "1"]
       [:h2.text-xl.font-bold.text-gray-900.uppercase.tracking-wide 
        {:style {:letter-spacing "1px"}}
        "Business Information"]]
      [:p.text-gray-600.mb-4
       "Tell us about your business. This information is required for all business banking services."]
      
      ;; KYC/KYB Legend
      [:div.rounded.p-3.text-xs {:style {:background-color "#f8fafc" :border "1px solid #e2e8f0"}}
       [:span.font-semibold.text-gray-700 "Verification Legend: "]
       [:span.badge-kyc "KYC"]
       [:span.text-gray-500.mx-2 "Know Your Customer (Identity)"]
       [:span.badge-kyb "KYB"]
       [:span.text-gray-500.mx-2 "Know Your Business (Entity)"]]]
     
     ;; Basic Business Info
     [:div.card
      [:h3.font-semibold.uppercase.tracking-wide.mb-4.pb-2.border-b 
       {:style {:color "#00857c" :letter-spacing "1px"}} 
       "Business Details"]
      
      [:div.grid.grid-cols-2.gap-4
       [form-field {:label "Legal Business Name" :required true :kyb true}
        [:input.form-input.w-full.rounded.border-gray-300
         {:type "text"
          :value (:business-legal-name form-data)
          :on-change #(update-field! :business-legal-name %)
          :placeholder "Acme Corporation"}]]
       
       [form-field {:label "DBA (Doing Business As)" :kyb true}
        [:input.form-input.w-full.rounded.border-gray-300
         {:type "text"
          :value (:dba-name form-data)
          :on-change #(update-field! :dba-name %)
          :placeholder "If different from legal name"}]]]
      
      [:div.grid.grid-cols-2.gap-4
       [form-field {:label "Business Type" :required true :kyb true}
        [:select.form-select.w-full.rounded.border-gray-300
         {:value (:business-type form-data)
          :on-change #(update-field! :business-type %)}
         (for [{:keys [value label]} state/business-types]
           ^{:key value}
           [:option {:value value} label])]]
       
       [form-field {:label "EIN / Tax ID" :required true :kyb true}
        [:input.form-input.w-full.rounded.border-gray-300
         {:type "text"
          :value (:ein form-data)
          :on-change #(update-field! :ein %)
          :placeholder "XX-XXXXXXX"}]]]
      
      [:div.grid.grid-cols-2.gap-4
       [form-field {:label "State of Incorporation" :required true :kyb true}
        [:input.form-input.w-full.rounded.border-gray-300
         {:type "text"
          :value (:state-of-incorporation form-data)
          :on-change #(update-field! :state-of-incorporation %)
          :placeholder "e.g., Massachusetts"}]]
       
       [form-field {:label "Date Established" :required true :kyb true}
        [:input.form-input.w-full.rounded.border-gray-300
         {:type "date"
          :value (:date-established form-data)
          :on-change #(update-field! :date-established %)}]]]]
     
     ;; Contact Info
     [:div.card
      [:h3.font-semibold.uppercase.tracking-wide.mb-4.pb-2.border-b 
       {:style {:color "#00857c" :letter-spacing "1px"}}
       "Business Contact"]
      
      [:div.grid.grid-cols-2.gap-4
       [form-field {:label "Business Phone" :required true :kyb true}
        [:input.form-input.w-full.rounded.border-gray-300
         {:type "tel"
          :value (:business-phone form-data)
          :on-change #(update-field! :business-phone %)
          :placeholder "(617) 555-0100"}]]
       
       [form-field {:label "Business Email" :required true}
        [:input.form-input.w-full.rounded.border-gray-300
         {:type "email"
          :value (:business-email form-data)
          :on-change #(update-field! :business-email %)
          :placeholder "contact@yourbusiness.com"}]]]
      
      [form-field {:label "Business Website"}
       [:input.form-input.w-full.rounded.border-gray-300
        {:type "url"
         :value (:business-website form-data)
         :on-change #(update-field! :business-website %)
         :placeholder "https://www.yourbusiness.com"}]]]
     
     ;; Address
     [:div.card
      [:h3.font-semibold.uppercase.tracking-wide.mb-4.pb-2.border-b 
       {:style {:color "#00857c" :letter-spacing "1px"}}
       "Business Address"]
      
      [form-field {:label "Street Address" :required true :kyb true}
       [:input.form-input.w-full.rounded.border-gray-300
        {:type "text"
         :value (:business-address form-data)
         :on-change #(update-field! :business-address %)
         :placeholder "123 Main Street, Suite 100"}]]
      
      [:div.grid.grid-cols-6.gap-4
       [:div.col-span-3
        [form-field {:label "City" :required true :kyb true}
         [:input.form-input.w-full.rounded.border-gray-300
          {:type "text"
           :value (:business-city form-data)
           :on-change #(update-field! :business-city %)
           :placeholder "Cambridge"}]]]
       [:div.col-span-1
        [form-field {:label "State" :required true :kyb true}
         [:input.form-input.w-full.rounded.border-gray-300
          {:type "text"
           :value (:business-state form-data)
           :on-change #(update-field! :business-state %)
           :placeholder "MA"
           :maxLength 2}]]]
       [:div.col-span-2
        [form-field {:label "ZIP Code" :required true :kyb true}
         [:input.form-input.w-full.rounded.border-gray-300
          {:type "text"
           :value (:business-zip form-data)
           :on-change #(update-field! :business-zip %)
           :placeholder "02138"}]]]]]
     
     ;; Industry Info
     [:div.card
      [:h3.font-semibold.uppercase.tracking-wide.mb-4.pb-2.border-b 
       {:style {:color "#00857c" :letter-spacing "1px"}}
       "Industry Information"]
      
      [:div.grid.grid-cols-2.gap-4
       [form-field {:label "Industry" :kyb true}
        [:input.form-input.w-full.rounded.border-gray-300
         {:type "text"
          :value (:industry form-data)
          :on-change #(update-field! :industry %)
          :placeholder "e.g., Technology, Retail, Healthcare"}]]
       
       [form-field {:label "NAICS Code" :kyb true}
        [:input.form-input.w-full.rounded.border-gray-300
         {:type "text"
          :value (:naics-code form-data)
          :on-change #(update-field! :naics-code %)
          :placeholder "6-digit code (optional)"}]]]
      
      [form-field {:label "Business Description"}
       [:textarea.form-textarea.w-full.rounded.border-gray-300
        {:rows 3
         :value (:business-description form-data)
         :on-change #(update-field! :business-description %)
         :placeholder "Briefly describe what your business does..."}]]
      
      [form-field {:label "Number of Employees"}
       [:input.form-input.w-full.rounded.border-gray-300
        {:type "number"
         :value (:number-of-employees form-data)
         :on-change #(update-field! :number-of-employees %)
         :placeholder "e.g., 10"}]]]
     
     ;; Navigation
     [:div.flex.justify-between.pt-6
      [:button.btn-secondary
       {:on-click state/go-back!}
       "← Back"]
      [:button.btn-primary
       {:on-click state/go-next!}
       "Continue →"]]]))
