(ns csb.loan.steps.loan-request
  (:require [reagent.core :as r]
            [clojure.string :as str]
            [csb.loan.state :as state]
            [csb.components.ui.form-field :refer [form-field]]))

(defn loan-request-step [form-data]
  (let [errors (r/atom {})]
    (fn [form-data]
      (let [validate (fn []
                       (cond-> {}
                         (str/blank? (:loan-type form-data))
                         (assoc :loan-type "Please select a loan type.")
                         
                         (str/blank? (:loan-amount form-data))
                         (assoc :loan-amount "Please enter the requested loan amount.")
                         
                         (str/blank? (:loan-purpose form-data))
                         (assoc :loan-purpose "Please select the primary purpose for this loan.")
                         
                         (str/blank? (:loan-term form-data))
                         (assoc :loan-term "Please select a loan term.")
                         
                         (str/blank? (:urgency form-data))
                         (assoc :urgency "Please indicate when you need funding.")))
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
          [:h2.text-xl.font-bold.mb-2 {:style {:color "#333"}} "Tell Us About Your Loan Request"]
          [:p.text-gray-500.text-sm
           "Help us understand your financing needs so we can match you with the right loan product. "
           "Fields marked with " [:span.text-red-500 "*"] " are required."]]
         
         ;; Loan Type & Amount
         [:div.card.space-y-5
          [:h3.font-bold.text-gray-700.border-b.pb-2 "Loan Details"]
          
          [:div.grid.sm:grid-cols-2.gap-5
           [form-field {:label "Loan Type" :required true :error (:loan-type @errors)}
            [:select.form-input {:value (:loan-type form-data)
                                 :on-change (set-field :loan-type)}
             (for [lt state/loan-types]
               ^{:key (:value lt)}
               [:option {:value (:value lt)} (:label lt)])]]
           
           [form-field {:label "Requested Loan Amount" :required true :error (:loan-amount @errors)}
            [:div.relative
             [:span.absolute.left-3.text-gray-500.font-medium
              {:style {:top "50%" :transform "translateY(-50%)"}}
              "$"]
             [:input.form-input.pl-7 {:type "text"
                                       :placeholder "e.g., 250,000"
                                       :value (:loan-amount form-data)
                                       :on-change (set-field :loan-amount)}]]]]
          
          [:div.grid.sm:grid-cols-2.gap-5
           [form-field {:label "Loan Purpose" :required true :error (:loan-purpose @errors)}
            [:select.form-input {:value (:loan-purpose form-data)
                                 :on-change (set-field :loan-purpose)}
             (for [lp state/loan-purposes]
               ^{:key (:value lp)}
               [:option {:value (:value lp)} (:label lp)])]]
           
           [form-field {:label "Desired Loan Term" :required true :error (:loan-term @errors)}
            [:select.form-input {:value (:loan-term form-data)
                                 :on-change (set-field :loan-term)}
             [:option {:value ""} "Select term..."]
             [:option {:value "12"} "12 months (1 year)"]
             [:option {:value "24"} "24 months (2 years)"]
             [:option {:value "36"} "36 months (3 years)"]
             [:option {:value "60"} "60 months (5 years)"]
             [:option {:value "84"} "84 months (7 years)"]
             [:option {:value "120"} "120 months (10 years)"]
             [:option {:value "180"} "180 months (15 years)"]
             [:option {:value "240"} "240 months (20 years)"]
             [:option {:value "300"} "300 months (25 years)"]]]]
          
          (when (= (:loan-purpose form-data) "other")
            [form-field {:label "Please describe the loan purpose" :required true}
             [:textarea.form-input {:rows 3
                                    :placeholder "Describe how you plan to use the loan funds..."
                                    :value (:loan-purpose-detail form-data)
                                    :on-change (set-field :loan-purpose-detail)}]])
          
          [:div.grid.sm:grid-cols-2.gap-5
           [form-field {:label "Preferred Interest Rate Type"}
            [:select.form-input {:value (:requested-rate-type form-data)
                                 :on-change (set-field :requested-rate-type)}
             [:option {:value ""} "No preference"]
             [:option {:value "fixed"} "Fixed Rate"]
             [:option {:value "variable"} "Variable Rate"]]]
           
           [form-field {:label "When do you need funding?" :required true :error (:urgency @errors)}
            [:select.form-input {:value (:urgency form-data)
                                 :on-change (set-field :urgency)}
             [:option {:value ""} "Select timeline..."]
             [:option {:value "immediate"} "As soon as possible"]
             [:option {:value "30-days"} "Within 30 days"]
             [:option {:value "60-days"} "Within 60 days"]
             [:option {:value "90-days"} "Within 90 days"]
             [:option {:value "exploring"} "Just exploring options"]]]]]
         
         ;; Existing Customer
         [:div.card.space-y-5
          [:h3.font-bold.text-gray-700.border-b.pb-2 "Existing Relationship"]
          
          [:label.flex.items-center.gap-3.cursor-pointer
           [:input.w-5.h-5 {:type "checkbox"
                           :checked (:existing-customer form-data)
                           :on-change #(state/update-form-data! 
                                        {:existing-customer (.. % -target -checked)})}]
           [:span.text-gray-700 "I am an existing Cambridge Savings Bank customer"]]
          
          (when (:existing-customer form-data)
            [:div.mt-4
             [form-field {:label "Account Number (if known)"}
              [:input.form-input {:type "text"
                                  :placeholder "Enter your CSB account number"
                                  :value (:existing-account-number form-data)
                                  :on-change (set-field :existing-account-number)}]]])]
         
         ;; Info Box
         [:div.rounded-lg.p-4 {:style {:background-color "rgba(0, 133, 124, 0.08)"
                                       :border "1px solid rgba(0, 133, 124, 0.2)"}}
          [:div.flex.gap-3
           [:div.text-2xl "💡"]
           [:div
            [:h4.font-semibold.mb-1 {:style {:color "#00857c"}} "What to Expect"]
            [:p.text-sm.text-gray-600
             "After completing this application, a loan officer will review your request and may contact you for additional information. "
             "The approval process typically takes 3-5 business days for standard loans, or 2-4 weeks for SBA loans."]]]]
         
         ;; Navigation
         [:div.flex.justify-end
          [:button.font-bold.py-3.px-8.rounded-lg.text-white.transition-all
           {:style {:background-color "#00857c"}
            :on-click handle-next}
           "Continue →"]]]))))
