(ns csb.components.steps.business-info
  (:require [reagent.core :as r]
            [clojure.string :as str]
            [csb.state :as state]
            [csb.utils :as utils]
            [csb.components.ui.form-field :refer [form-field]]))

(defn business-info-step [form-data]
  (let [errors (r/atom {})]
    (fn [form-data]
      (let [validate (fn []
                      (let [e (cond-> {}
                                (str/blank? (:business-name form-data))
                                (assoc :business-name "Business name is required.")
                                
                                (str/blank? (:business-type form-data))
                                (assoc :business-type "Please select a business type.")
                                
                                (str/blank? (:ein form-data))
                                (assoc :ein "EIN / Tax ID is required.")
                                
                                (and (not (str/blank? (:ein form-data)))
                                     (not (utils/valid-ein? (:ein form-data))))
                                (assoc :ein "Enter a valid EIN (e.g., 12-3456789).")
                                
                                (str/blank? (:state-of-formation form-data))
                                (assoc :state-of-formation "State of formation is required.")
                                
                                (str/blank? (:date-established form-data))
                                (assoc :date-established "Date established is required.")
                                
                                (str/blank? (:business-phone form-data))
                                (assoc :business-phone "Business phone is required.")
                                
                                (str/blank? (:business-address form-data))
                                (assoc :business-address "Street address is required.")
                                
                                (str/blank? (:business-city form-data))
                                (assoc :business-city "City is required.")
                                
                                (str/blank? (:business-state form-data))
                                (assoc :business-state "State is required.")
                                
                                (str/blank? (:business-zip form-data))
                                (assoc :business-zip "ZIP code is required.")
                                
                                (and (not (str/blank? (:business-zip form-data)))
                                     (not (utils/valid-zip? (:business-zip form-data))))
                                (assoc :business-zip "Enter a valid ZIP code.")
                                
                                (str/blank? (:business-description form-data))
                                (assoc :business-description "Please provide a brief description of your business."))]
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
          [:h2.section-header "Business Information"]
          [:p.text-gray-500.text-sm
           "Tell us about your business. All fields marked with "
           [:span.required-star "*"]
           " are required."]]

         ;; Business Identity
         [:div.card.space-y-5
          [:h3.font-bold.text-gray-700.border-b.pb-2 "Business Identity"]

          [form-field {:label "Legal Business Name" :required true :error (:business-name @errors)}
           [:input.form-input {:type "text"
                               :placeholder "e.g., Acme Widget Company LLC"
                               :value (:business-name form-data)
                               :on-change (set-field :business-name)}]]

          [form-field {:label "DBA / Trade Name" :error (:dba @errors)}
           [:input.form-input {:type "text"
                               :placeholder "Doing Business As (if different from legal name)"
                               :value (:dba form-data)
                               :on-change (set-field :dba)}]]

          [:div.grid.sm:grid-cols-2.gap-5
           [form-field {:label "Business Type / Entity Structure" :required true :error (:business-type @errors)}
            [:select.form-input {:value (:business-type form-data)
                                 :on-change (set-field :business-type)}
             (for [bt utils/business-types]
               ^{:key (:value bt)}
               [:option {:value (:value bt)} (:label bt)])]]

           [form-field {:label "State of Formation" :required true :error (:state-of-formation @errors)}
            [:select.form-input {:value (:state-of-formation form-data)
                                 :on-change (set-field :state-of-formation)}
             [:option {:value ""} "Select state..."]
             (for [s utils/us-states]
               ^{:key s}
               [:option {:value s} s])]]]

          [:div.grid.sm:grid-cols-2.gap-5
           [form-field {:label "EIN / Employer Identification Number" :required true :error (:ein @errors)}
            [:input.form-input {:type "text"
                                :placeholder "XX-XXXXXXX"
                                :value (:ein form-data)
                                :on-change #(state/update-form-data! 
                                            {:ein (utils/format-ein (.. % -target -value))})}]
            [:p.text-xs.text-gray-400.mt-1 "Format: 12-3456789"]]

           [form-field {:label "Date Established" :required true :error (:date-established @errors)}
            [:input.form-input {:type "date"
                                :value (:date-established form-data)
                                :max (subs (.toISOString (js/Date.)) 0 10)
                                :on-change (set-field :date-established)}]]]

          [form-field {:label "Business Phone" :required true :error (:business-phone @errors)}
           [:input.form-input {:type "tel"
                               :placeholder "(617) 555-1234"
                               :value (:business-phone form-data)
                               :on-change #(state/update-form-data!
                                           {:business-phone (utils/format-phone (.. % -target -value))})}]]

          [form-field {:label "NAICS Code (Optional)" :error (:naics @errors)}
           [:input.form-input {:type "text"
                               :placeholder "e.g., 541511 – Custom Computer Programming"
                               :value (:naics form-data)
                               :on-change (set-field :naics)}]
           [:p.text-xs.text-gray-400.mt-1
            [:a.underline {:href "https://www.census.gov/naics/"
                          :target "_blank"
                          :rel "noopener noreferrer"}
             "Look up your NAICS code"]]]

          [form-field {:label "Brief Business Description" :required true :error (:business-description @errors)}
           [:textarea.form-input {:rows 3
                                  :placeholder "Describe your primary business activities and products/services offered..."
                                  :value (:business-description form-data)
                                  :on-change (set-field :business-description)}]]]

         ;; Business Address
         [:div.card.space-y-5
          [:h3.font-bold.text-gray-700.border-b.pb-2 "Business Address"]
          [:div.info-box
           [:strong "Important:"]
           " This must be a physical address (no P.O. boxes). We may send correspondence here."]

          [form-field {:label "Street Address" :required true :error (:business-address @errors)}
           [:input.form-input {:type "text"
                               :placeholder "123 Main Street, Suite 100"
                               :value (:business-address form-data)
                               :on-change (set-field :business-address)}]]

          [:div.grid.sm:grid-cols-3.gap-5
           [form-field {:label "City" :required true :error (:business-city @errors)}
            [:input.form-input {:type "text"
                                :placeholder "Cambridge"
                                :value (:business-city form-data)
                                :on-change (set-field :business-city)}]]

           [form-field {:label "State" :required true :error (:business-state @errors)}
            [:select.form-input {:value (:business-state form-data)
                                 :on-change (set-field :business-state)}
             [:option {:value ""} "State"]
             (for [s utils/us-states]
               ^{:key s}
               [:option {:value s} s])]]

           [form-field {:label "ZIP Code" :required true :error (:business-zip @errors)}
            [:input.form-input {:type "text"
                                :placeholder "02138"
                                :value (:business-zip form-data)
                                :max-length 10
                                :on-change (set-field :business-zip)}]]]]

         [:div.flex.justify-between.gap-3
          [:button.btn-secondary {:on-click state/go-back!} "← Back"]
          [:button.btn-primary {:on-click handle-next} "Continue →"]]]))))
