(ns csb.loan.steps.business-info
  (:require [reagent.core :as r]
            [clojure.string :as str]
            [csb.loan.state :as state]
            [csb.utils :as utils]
            [csb.components.ui.form-field :refer [form-field]]))

(defn business-info-step [form-data]
  (let [errors (r/atom {})]
    (fn [form-data]
      (let [validate (fn []
                       (cond-> {}
                         (str/blank? (:business-legal-name form-data))
                         (assoc :business-legal-name "Business legal name is required.")
                         
                         (str/blank? (:business-type form-data))
                         (assoc :business-type "Please select a business type.")
                         
                         (str/blank? (:ein form-data))
                         (assoc :ein "EIN / Tax ID is required.")
                         
                         (and (not (str/blank? (:ein form-data)))
                              (not (utils/valid-ein? (:ein form-data))))
                         (assoc :ein "Enter a valid EIN (e.g., 12-3456789).")
                         
                         (str/blank? (:state-of-incorporation form-data))
                         (assoc :state-of-incorporation "State of incorporation is required.")
                         
                         (str/blank? (:date-established form-data))
                         (assoc :date-established "Date established is required.")
                         
                         (str/blank? (:business-phone form-data))
                         (assoc :business-phone "Business phone is required.")
                         
                         (str/blank? (:business-address form-data))
                         (assoc :business-address "Business address is required.")
                         
                         (str/blank? (:business-city form-data))
                         (assoc :business-city "City is required.")
                         
                         (str/blank? (:business-state form-data))
                         (assoc :business-state "State is required.")
                         
                         (str/blank? (:business-zip form-data))
                         (assoc :business-zip "ZIP code is required.")
                         
                         (str/blank? (:industry form-data))
                         (assoc :industry "Industry is required.")
                         
                         (str/blank? (:business-description form-data))
                         (assoc :business-description "Business description is required.")))
            handle-next (fn []
                          (let [e (validate)]
                            (reset! errors e)
                            (when (empty? e)
                              (state/go-next!))))
            set-field (fn [field]
                        (fn [e]
                          (state/update-form-data! {field (.. e -target -value)})))]
        [:div.space-y-6
         ;; Header
         [:div.card
          [:h2.text-xl.font-bold.mb-2 {:style {:color "#333"}} "Business Information"]
          [:p.text-gray-500.text-sm
           "We need to verify your business identity (KYB - Know Your Business). This information will be validated against public records. "
           "Fields marked with " [:span.text-red-500 "*"] " are required."]]
         
         ;; Business Identity
         [:div.card.space-y-5
          [:h3.font-bold.text-gray-700.border-b.pb-2 "Business Identity"]
          
          [form-field {:label "Legal Business Name" :required true :error (:business-legal-name @errors)}
           [:input.form-input {:type "text"
                               :placeholder "Exact name as registered with the state"
                               :value (:business-legal-name form-data)
                               :on-change (set-field :business-legal-name)}]]
          
          [form-field {:label "DBA / Trade Name" :error (:dba-name @errors)
                       :hint "If different from legal name"}
           [:input.form-input {:type "text"
                               :placeholder "Doing Business As"
                               :value (:dba-name form-data)
                               :on-change (set-field :dba-name)}]]
          
          [:div.grid.sm:grid-cols-2.gap-5
           [form-field {:label "Business Type / Entity Structure" :required true :error (:business-type @errors)}
            [:select.form-input {:value (:business-type form-data)
                                 :on-change (set-field :business-type)}
             (for [bt utils/business-types]
               ^{:key (:value bt)}
               [:option {:value (:value bt)} (:label bt)])]]
           
           [form-field {:label "EIN / Tax ID" :required true :error (:ein @errors)}
            [:input.form-input {:type "text"
                                :placeholder "XX-XXXXXXX"
                                :value (:ein form-data)
                                :on-change #(state/update-form-data!
                                             {:ein (utils/format-ein (.. % -target -value))})}]]]
          
          [:div.grid.sm:grid-cols-2.gap-5
           [form-field {:label "State of Incorporation" :required true :error (:state-of-incorporation @errors)}
            [:select.form-input {:value (:state-of-incorporation form-data)
                                 :on-change (set-field :state-of-incorporation)}
             [:option {:value ""} "Select state..."]
             (for [s utils/us-states]
               ^{:key s}
               [:option {:value s} s])]]
           
           [form-field {:label "Date Established" :required true :error (:date-established @errors)}
            [:input.form-input {:type "date"
                                :value (:date-established form-data)
                                :on-change (set-field :date-established)}]]]
          
          [:div.grid.sm:grid-cols-2.gap-5
           [form-field {:label "Years in Business"}
            [:input.form-input {:type "number"
                                :placeholder "e.g., 5"
                                :min 0
                                :value (:years-in-business form-data)
                                :on-change (set-field :years-in-business)}]]
           
           [form-field {:label "Number of Employees"}
            [:input.form-input {:type "number"
                                :placeholder "Full-time equivalent"
                                :min 0
                                :value (:number-of-employees form-data)
                                :on-change (set-field :number-of-employees)}]]]]
         
         ;; Contact Information
         [:div.card.space-y-5
          [:h3.font-bold.text-gray-700.border-b.pb-2 "Business Contact"]
          
          [:div.grid.sm:grid-cols-2.gap-5
           [form-field {:label "Business Phone" :required true :error (:business-phone @errors)}
            [:input.form-input {:type "tel"
                                :placeholder "(555) 555-5555"
                                :value (:business-phone form-data)
                                :on-change (set-field :business-phone)}]]
           
           [form-field {:label "Business Email"}
            [:input.form-input {:type "email"
                                :placeholder "info@yourbusiness.com"
                                :value (:business-email form-data)
                                :on-change (set-field :business-email)}]]]
          
          [form-field {:label "Business Website"}
           [:input.form-input {:type "url"
                               :placeholder "https://www.yourbusiness.com"
                               :value (:business-website form-data)
                               :on-change (set-field :business-website)}]]]
         
         ;; Business Address
         [:div.card.space-y-5
          [:h3.font-bold.text-gray-700.border-b.pb-2 "Business Address"]
          
          [form-field {:label "Street Address" :required true :error (:business-address @errors)}
           [:input.form-input {:type "text"
                               :placeholder "123 Main Street"
                               :value (:business-address form-data)
                               :on-change (set-field :business-address)}]]
          
          [:div.grid.sm:grid-cols-3.gap-5
           [form-field {:label "City" :required true :error (:business-city @errors)}
            [:input.form-input {:type "text"
                                :placeholder "City"
                                :value (:business-city form-data)
                                :on-change (set-field :business-city)}]]
           
           [form-field {:label "State" :required true :error (:business-state @errors)}
            [:select.form-input {:value (:business-state form-data)
                                 :on-change (set-field :business-state)}
             [:option {:value ""} "State..."]
             (for [s utils/us-states]
               ^{:key s}
               [:option {:value s} s])]]
           
           [form-field {:label "ZIP Code" :required true :error (:business-zip @errors)}
            [:input.form-input {:type "text"
                                :placeholder "ZIP"
                                :value (:business-zip form-data)
                                :on-change (set-field :business-zip)}]]]
          
          [:label.flex.items-center.gap-3.cursor-pointer.mt-2
           [:input.w-5.h-5 {:type "checkbox"
                           :checked (:mailing-same-as-business form-data)
                           :on-change #(state/update-form-data! 
                                        {:mailing-same-as-business (.. % -target -checked)})}]
           [:span.text-gray-700 "Mailing address is the same as business address"]]
          
          (when (not (:mailing-same-as-business form-data))
            [:div.mt-4.pt-4.border-t.space-y-4
             [:h4.font-semibold.text-gray-600 "Mailing Address"]
             [form-field {:label "Street Address"}
              [:input.form-input {:type "text"
                                  :placeholder "Mailing address"
                                  :value (:mailing-address form-data)
                                  :on-change (set-field :mailing-address)}]]
             [:div.grid.sm:grid-cols-3.gap-5
              [form-field {:label "City"}
               [:input.form-input {:type "text"
                                   :value (:mailing-city form-data)
                                   :on-change (set-field :mailing-city)}]]
              [form-field {:label "State"}
               [:select.form-input {:value (:mailing-state form-data)
                                    :on-change (set-field :mailing-state)}
                [:option {:value ""} "State..."]
                (for [s utils/us-states]
                  ^{:key s}
                  [:option {:value s} s])]]
              [form-field {:label "ZIP"}
               [:input.form-input {:type "text"
                                   :value (:mailing-zip form-data)
                                   :on-change (set-field :mailing-zip)}]]]])]
         
         ;; Industry & Description
         [:div.card.space-y-5
          [:h3.font-bold.text-gray-700.border-b.pb-2 "Business Operations"]
          
          [:div.grid.sm:grid-cols-2.gap-5
           [form-field {:label "Industry / Sector" :required true :error (:industry @errors)}
            [:select.form-input {:value (:industry form-data)
                                 :on-change (set-field :industry)}
             [:option {:value ""} "Select industry..."]
             [:option {:value "retail"} "Retail Trade"]
             [:option {:value "wholesale"} "Wholesale Trade"]
             [:option {:value "manufacturing"} "Manufacturing"]
             [:option {:value "construction"} "Construction"]
             [:option {:value "professional-services"} "Professional Services"]
             [:option {:value "healthcare"} "Healthcare & Social Assistance"]
             [:option {:value "food-service"} "Food Service & Restaurants"]
             [:option {:value "real-estate"} "Real Estate"]
             [:option {:value "transportation"} "Transportation & Logistics"]
             [:option {:value "technology"} "Technology / Software"]
             [:option {:value "finance"} "Finance & Insurance"]
             [:option {:value "education"} "Education"]
             [:option {:value "agriculture"} "Agriculture"]
             [:option {:value "other"} "Other"]]]
           
           [form-field {:label "NAICS Code" :hint "If known (6-digit code)"}
            [:input.form-input {:type "text"
                                :placeholder "e.g., 541511"
                                :value (:naics-code form-data)
                                :on-change (set-field :naics-code)}]]]
          
          [form-field {:label "Business Description" :required true :error (:business-description @errors)
                       :hint "Describe your products, services, and operations"}
           [:textarea.form-input {:rows 4
                                  :placeholder "Describe what your business does, your main products or services, and your target market..."
                                  :value (:business-description form-data)
                                  :on-change (set-field :business-description)}]]]
         
         ;; Navigation
         [:div.flex.justify-between.gap-3
          [:button.font-semibold.py-3.px-6.rounded-lg.transition-all
           {:style {:border "2px solid #00857c" :color "#00857c"}
            :on-click state/go-back!}
           "← Back"]
          [:button.font-bold.py-3.px-8.rounded-lg.text-white.transition-all
           {:style {:background-color "#00857c"}
            :on-click handle-next}
           "Continue →"]]]))))
