(ns csb.loan.steps.collateral
  (:require [reagent.core :as r]
            [clojure.string :as str]
            [csb.loan.state :as state]
            [csb.utils :as utils]
            [csb.components.ui.form-field :refer [form-field]]))

(defn collateral-step [form-data]
  (let [errors (r/atom {})]
    (fn [form-data]
      (let [validate (fn []
                       (cond-> {}
                         (and (:collateral-offered form-data)
                              (str/blank? (:collateral-type form-data)))
                         (assoc :collateral-type "Please select a collateral type.")
                         
                         (and (:collateral-offered form-data)
                              (str/blank? (:collateral-description form-data)))
                         (assoc :collateral-description "Please describe the collateral.")
                         
                         (and (:collateral-offered form-data)
                              (str/blank? (:collateral-estimated-value form-data)))
                         (assoc :collateral-estimated-value "Please provide an estimated value.")))
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
          [:h2.text-xl.font-bold.mb-2 {:style {:color "#333"}} "Collateral Information"]
          [:p.text-gray-500.text-sm
           "Secured loans typically offer better rates and terms. Tell us about any collateral you can offer to secure this loan."]]
         
         ;; Collateral Option
         [:div.card.space-y-5
          [:h3.font-bold.text-gray-700.border-b.pb-2 "Will you be offering collateral?"]
          
          [:div.space-y-3
           [:label.flex.items-start.gap-3.cursor-pointer.p-4.rounded-lg.border-2.transition-all
            {:class (if (:collateral-offered form-data) "border-[#00857c] bg-[#00857c]/5" "border-gray-200 hover:border-gray-300")}
            [:input.w-5.h-5.mt-0.5 {:type "radio"
                                    :name "collateral-option"
                                    :checked (:collateral-offered form-data)
                                    :on-change #(state/update-form-data! {:collateral-offered true})}]
            [:div
             [:span.font-semibold.text-gray-800 "Yes, I will offer collateral"]
             [:p.text-sm.text-gray-500.mt-1 "Secured loans typically have lower interest rates and better terms"]]]
           
           [:label.flex.items-start.gap-3.cursor-pointer.p-4.rounded-lg.border-2.transition-all
            {:class (if (false? (:collateral-offered form-data)) "border-[#00857c] bg-[#00857c]/5" "border-gray-200 hover:border-gray-300")}
            [:input.w-5.h-5.mt-0.5 {:type "radio"
                                    :name "collateral-option"
                                    :checked (false? (:collateral-offered form-data))
                                    :on-change #(state/update-form-data! {:collateral-offered false})}]
            [:div
             [:span.font-semibold.text-gray-800 "No, I am requesting an unsecured loan"]
             [:p.text-sm.text-gray-500.mt-1 "Approval will be based on creditworthiness and business financials"]]]]]
         
         ;; Collateral Details (if offering)
         (when (:collateral-offered form-data)
           [:<>
            [:div.card.space-y-5
             [:h3.font-bold.text-gray-700.border-b.pb-2 "Primary Collateral Details"]
             
             [form-field {:label "Collateral Type" :required true :error (:collateral-type @errors)}
              [:select.form-input {:value (:collateral-type form-data)
                                   :on-change (set-field :collateral-type)}
               (for [ct state/collateral-types]
                 ^{:key (:value ct)}
                 [:option {:value (:value ct)} (:label ct)])]]
             
             [form-field {:label "Description of Collateral" :required true :error (:collateral-description @errors)
                          :hint "Provide details about the asset (make, model, year, condition, etc.)"}
              [:textarea.form-input {:rows 3
                                     :placeholder "e.g., 2022 Caterpillar 320 Excavator, excellent condition, 500 hours..."
                                     :value (:collateral-description form-data)
                                     :on-change (set-field :collateral-description)}]]
             
             [:div.grid.sm:grid-cols-2.gap-5
              [form-field {:label "Estimated Value" :required true :error (:collateral-estimated-value @errors)}
               [:div.relative
                [:span.absolute.left-3.text-gray-500 {:style {:top "50%" :transform "translateY(-50%)"}} "$"]
                [:input.form-input.pl-7 {:type "text"
                                          :placeholder "e.g., 150,000"
                                          :value (:collateral-estimated-value form-data)
                                          :on-change (set-field :collateral-estimated-value)}]]]
              
              [form-field {:label "Current Lien Holder (if any)"}
               [:input.form-input {:type "text"
                                   :placeholder "Name of bank or lender"
                                   :value (:collateral-lien-holder form-data)
                                   :on-change (set-field :collateral-lien-holder)}]]]
             
             (when (seq (:collateral-lien-holder form-data))
               [form-field {:label "Outstanding Lien Amount"}
                [:div.relative
                 [:span.absolute.left-3.text-gray-500 {:style {:top "50%" :transform "translateY(-50%)"}} "$"]
                 [:input.form-input.pl-7 {:type "text"
                                           :placeholder "Amount still owed"
                                           :value (:collateral-lien-amount form-data)
                                           :on-change (set-field :collateral-lien-amount)}]]])]
            
            ;; Real Estate Collateral Address
            (when (= (:collateral-type form-data) "real-estate")
              [:div.card.space-y-5
               [:h3.font-bold.text-gray-700.border-b.pb-2 "Property Address"]
               
               [form-field {:label "Street Address"}
                [:input.form-input {:type "text"
                                    :value (:collateral-address form-data)
                                    :on-change (set-field :collateral-address)}]]
               
               [:div.grid.sm:grid-cols-3.gap-5
                [form-field {:label "City"}
                 [:input.form-input {:type "text"
                                     :value (:collateral-city form-data)
                                     :on-change (set-field :collateral-city)}]]
                [form-field {:label "State"}
                 [:select.form-input {:value (:collateral-state form-data)
                                      :on-change (set-field :collateral-state)}
                  [:option {:value ""} "State..."]
                  (for [s utils/us-states]
                    ^{:key s}
                    [:option {:value s} s])]]
                [form-field {:label "ZIP"}
                 [:input.form-input {:type "text"
                                     :value (:collateral-zip form-data)
                                     :on-change (set-field :collateral-zip)}]]]])])
         
         ;; Info Box
         [:div.rounded-lg.p-4 {:style {:background-color "rgba(0, 133, 124, 0.08)"
                                       :border "1px solid rgba(0, 133, 124, 0.2)"}}
          [:div.flex.gap-3
           [:div.text-2xl "💡"]
           [:div
            [:h4.font-semibold.mb-1 {:style {:color "#00857c"}} "About Collateral"]
            [:p.text-sm.text-gray-600
             "Collateral helps secure the loan and may result in better terms. Common types include real estate, equipment, inventory, or accounts receivable. "
             "We may require an appraisal or valuation for certain collateral types."]]]]
         
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
