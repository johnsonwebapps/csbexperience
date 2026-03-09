(ns csb.components.steps.applicant-info
  (:require [reagent.core :as r]
            [clojure.string :as str]
            [csb.state :as state]
            [csb.utils :as utils]
            [csb.components.ui.form-field :refer [form-field]]))

(defn applicant-info-step [form-data]
  (let [errors (r/atom {})
        show-ssn (r/atom false)]
    (fn [form-data]
      (let [validate (fn []
                      (let [e (cond-> {}
                                (str/blank? (:first-name form-data))
                                (assoc :first-name "First name is required.")
                                
                                (str/blank? (:last-name form-data))
                                (assoc :last-name "Last name is required.")
                                
                                (str/blank? (:title form-data))
                                (assoc :title "Please select your title/role.")
                                
                                (str/blank? (:email form-data))
                                (assoc :email "Email address is required.")
                                
                                (and (not (str/blank? (:email form-data)))
                                     (not (utils/valid-email? (:email form-data))))
                                (assoc :email "Enter a valid email address.")
                                
                                (str/blank? (:phone form-data))
                                (assoc :phone "Phone number is required.")
                                
                                (str/blank? (:dob form-data))
                                (assoc :dob "Date of birth is required.")
                                
                                (str/blank? (:ssn form-data))
                                (assoc :ssn "Social Security Number is required.")
                                
                                (and (not (str/blank? (:ssn form-data)))
                                     (not (utils/valid-ssn? (:ssn form-data))))
                                (assoc :ssn "Enter a valid SSN (e.g., 123-45-6789).")
                                
                                (str/blank? (:address form-data))
                                (assoc :address "Street address is required.")
                                
                                (str/blank? (:city form-data))
                                (assoc :city "City is required.")
                                
                                (str/blank? (:state form-data))
                                (assoc :state "State is required.")
                                
                                (str/blank? (:zip form-data))
                                (assoc :zip "ZIP code is required.")
                                
                                (and (not (str/blank? (:zip form-data)))
                                     (not (utils/valid-zip? (:zip form-data))))
                                (assoc :zip "Enter a valid ZIP code.")
                                
                                (str/blank? (:id-type form-data))
                                (assoc :id-type "Please select an ID type.")
                                
                                (str/blank? (:id-number form-data))
                                (assoc :id-number "ID number is required.")
                                
                                (str/blank? (:id-expiry form-data))
                                (assoc :id-expiry "ID expiration date is required.")
                                
                                (str/blank? (:ownership-pct form-data))
                                (assoc :ownership-pct "Ownership percentage is required.")
                                
                                (and (not (str/blank? (:ownership-pct form-data)))
                                     (let [pct (js/parseFloat (:ownership-pct form-data))]
                                       (or (js/isNaN pct) (< pct 0) (> pct 100))))
                                (assoc :ownership-pct "Enter a valid percentage (0–100)."))]
                        e))
            handle-next (fn []
                         (let [e (validate)]
                           (reset! errors e)
                           (when (empty? e)
                             (state/go-next!))))
            set-field (fn [field]
                       (fn [e]
                         (state/update-form-data! {field (.. e -target -value)})))]
        [:div.space-y-6
         [:div.card
          [:h2.section-header "Your Information"]
          [:p.text-gray-500.text-sm
           "As the primary applicant, we need to verify your identity and role in the business. All fields marked with "
           [:span.required-star "*"]
           " are required."]]

         ;; Personal Details
         [:div.card.space-y-5
          [:h3.font-bold.text-gray-700.border-b.pb-2 "Personal Details"]

          [:div.grid.sm:grid-cols-2.gap-5
           [form-field {:label "First Name" :required true :error (:first-name @errors)}
            [:input.form-input {:type "text"
                                :placeholder "Jane"
                                :value (:first-name form-data)
                                :on-change (set-field :first-name)}]]
           [form-field {:label "Last Name" :required true :error (:last-name @errors)}
            [:input.form-input {:type "text"
                                :placeholder "Smith"
                                :value (:last-name form-data)
                                :on-change (set-field :last-name)}]]]

          [:div.grid.sm:grid-cols-2.gap-5
           [form-field {:label "Title / Role in Business" :required true :error (:title @errors)}
            [:select.form-input {:value (:title form-data)
                                 :on-change (set-field :title)}
             (for [t utils/titles]
               ^{:key (:value t)}
               [:option {:value (:value t)} (:label t)])]]
           [form-field {:label "Ownership Percentage" :required true :error (:ownership-pct @errors)
                       :hint "Enter your ownership stake in the business (0–100%)"}
            [:div.relative
             [:input.form-input.pr-8 {:type "number"
                                       :placeholder "e.g., 51"
                                       :min 0
                                       :max 100
                                       :value (:ownership-pct form-data)
                                       :on-change (set-field :ownership-pct)}]
             [:span.absolute.right-3.top-1.2.transform.-translate-y-1.2.text-gray-400.font-medium
              {:style {:top "50%" :transform "translateY(-50%)"}}
              "%"]]]]

          [:div.grid.sm:grid-cols-2.gap-5
           [form-field {:label "Email Address" :required true :error (:email @errors)}
            [:input.form-input {:type "email"
                                :placeholder "jane@example.com"
                                :value (:email form-data)
                                :on-change (set-field :email)}]]
           [form-field {:label "Mobile / Phone Number" :required true :error (:phone @errors)}
            [:input.form-input {:type "tel"
                                :placeholder "(617) 555-1234"
                                :value (:phone form-data)
                                :on-change #(state/update-form-data!
                                            {:phone (utils/format-phone (.. % -target -value))})}]]]

          [:div.grid.sm:grid-cols-2.gap-5
           [form-field {:label "Date of Birth" :required true :error (:dob @errors)}
            [:input.form-input {:type "date"
                                :value (:dob form-data)
                                :max (utils/max-dob)
                                :on-change (set-field :dob)}]]
           [form-field {:label "Social Security Number (SSN)" :required true :error (:ssn @errors)
                       :hint "Your SSN is encrypted and used only for identity verification."}
            [:div.relative
             [:input.form-input.pr-12 {:type (if @show-ssn "text" "password")
                                        :placeholder "XXX-XX-XXXX"
                                        :value (:ssn form-data)
                                        :on-change #(state/update-form-data!
                                                    {:ssn (utils/format-ssn (.. % -target -value))})}]
             [:button.absolute.right-3.text-gray-400.hover:text-gray-600.text-xs.font-medium
              {:type "button"
               :style {:top "50%" :transform "translateY(-50%)"}
               :on-click #(swap! show-ssn not)}
              (if @show-ssn "Hide" "Show")]]]]]

         ;; Home Address
         [:div.card.space-y-5
          [:h3.font-bold.text-gray-700.border-b.pb-2 "Home Address"]
          [form-field {:label "Street Address" :required true :error (:address @errors)}
           [:input.form-input {:type "text"
                               :placeholder "456 Oak Avenue, Apt 2B"
                               :value (:address form-data)
                               :on-change (set-field :address)}]]
          [:div.grid.sm:grid-cols-3.gap-5
           [form-field {:label "City" :required true :error (:city @errors)}
            [:input.form-input {:type "text"
                                :placeholder "Cambridge"
                                :value (:city form-data)
                                :on-change (set-field :city)}]]
           [form-field {:label "State" :required true :error (:state @errors)}
            [:select.form-input {:value (:state form-data)
                                 :on-change (set-field :state)}
             [:option {:value ""} "State"]
             (for [s utils/us-states]
               ^{:key s}
               [:option {:value s} s])]]
           [form-field {:label "ZIP Code" :required true :error (:zip @errors)}
            [:input.form-input {:type "text"
                                :placeholder "02138"
                                :value (:zip form-data)
                                :max-length 10
                                :on-change (set-field :zip)}]]]]

         ;; Government ID
         [:div.card.space-y-5
          [:h3.font-bold.text-gray-700.border-b.pb-2 "Government-Issued Identification"]
          [:div.info-box
           "Federal law requires us to verify the identity of all persons associated with the account. Please provide a valid, unexpired government-issued photo ID."]

          [:div.grid.sm:grid-cols-2.gap-5
           [form-field {:label "ID Type" :required true :error (:id-type @errors)}
            [:select.form-input {:value (:id-type form-data)
                                 :on-change (set-field :id-type)}
             (for [t utils/id-types]
               ^{:key (:value t)}
               [:option {:value (:value t)} (:label t)])]]
           (when (or (= (:id-type form-data) "drivers-license")
                    (= (:id-type form-data) "state-id"))
             [form-field {:label "Issuing State" :error (:id-state @errors)}
              [:select.form-input {:value (:id-state form-data)
                                   :on-change (set-field :id-state)}
               [:option {:value ""} "Select state..."]
               (for [s utils/us-states]
                 ^{:key s}
                 [:option {:value s} s])]])]

          [:div.grid.sm:grid-cols-2.gap-5
           [form-field {:label "ID Number" :required true :error (:id-number @errors)}
            [:input.form-input {:type "text"
                                :placeholder "ID number"
                                :value (:id-number form-data)
                                :on-change (set-field :id-number)}]]
           [form-field {:label "Expiration Date" :required true :error (:id-expiry @errors)}
            [:input.form-input {:type "date"
                                :value (:id-expiry form-data)
                                :min (subs (.toISOString (js/Date.)) 0 10)
                                :on-change (set-field :id-expiry)}]]]]

         [:div.flex.justify-between.gap-3
          [:button.btn-secondary {:on-click state/go-back!} "← Back"]
          [:button.btn-primary {:on-click handle-next} "Continue →"]]]))))
