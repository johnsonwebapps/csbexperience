(ns csb.unified.steps.loan-decision
  (:require [csb.unified.state :as state]
            [reagent.core :as r]))

(defn approved-view [form-data]
  [:div.space-y-6
   [:div.card.text-center.py-8
    [:div.w-20.h-20.rounded-full.bg-green-100.flex.items-center.justify-center.mx-auto.mb-4
     [:svg.w-10.h-10.text-green-600 {:fill "none" :viewBox "0 0 24 24" :stroke "currentColor"}
      [:path {:stroke-linecap "round" :stroke-linejoin "round" :stroke-width 2.5
              :d "M5 13l4 4L19 7"}]]]
    [:h2.text-2xl.font-bold.text-gray-900.mb-2 "Congratulations! You're Pre-Approved!"]
    [:p.text-gray-600 "Based on the information provided, your loan application has been pre-approved."]]
   
   [:div.card
    [:h3.font-semibold.uppercase.tracking-wide.mb-4.pb-2.border-b {:style {:color "#00857c" :letter-spacing "1px"}}
     "Your Pre-Approved Terms"]
    [:div.grid.grid-cols-3.gap-6.text-center
     [:div
      [:p.text-sm.text-gray-500.mb-1 "Approved Amount"]
      [:p.text-2xl.font-bold {:style {:color "#00857c"}} 
       (str "$" (:approved-amount form-data))]]
     [:div
      [:p.text-sm.text-gray-500.mb-1 "Interest Rate"]
      [:p.text-2xl.font-bold {:style {:color "#00857c"}} 
       (:approved-rate form-data)]]
     [:div
      [:p.text-sm.text-gray-500.mb-1 "Term"]
      [:p.text-2xl.font-bold {:style {:color "#00857c"}} 
       (str (:approved-term form-data) " months")]]]]
   
   [:div.rounded.p-4 {:style {:background-color "rgba(0, 133, 124, 0.08)"
                                 :border "1px solid rgba(0, 133, 124, 0.2)"}}
    [:div.flex.gap-3
     [:div.text-2xl "🏦"]
     [:div
      [:h4.font-semibold.mb-1 {:style {:color "#00857c"}} "Checking Account Required"]
      [:p.text-sm.text-gray-600
       "As part of your loan approval, we'll open a Small Business Checking account for your loan payments. "
       "This account has no monthly fees and includes free online banking."]]]]])

(defn denied-view [_form-data _on-continue-with-account]
  (let [wants-account? (r/atom false)]
    (fn [form-data on-continue-with-account]
      [:div.space-y-6
       [:div.card.text-center.py-8
        [:div.w-20.h-20.rounded-full.bg-red-100.flex.items-center.justify-center.mx-auto.mb-4
         [:svg.w-10.h-10.text-red-600 {:fill "none" :viewBox "0 0 24 24" :stroke "currentColor"}
          [:path {:stroke-linecap "round" :stroke-linejoin "round" :stroke-width 2.5
                  :d "M6 18L18 6M6 6l12 12"}]]]
        [:h2.text-2xl.font-bold.text-gray-900.mb-2 "We're Unable to Approve Your Loan"]
        [:p.text-gray-600 (:loan-decision-notes form-data)]]
       
       [:div.card
        [:h3.font-semibold.text-lg.mb-4 "What This Means"]
        [:p.text-gray-600.mb-4
         "Based on the information provided, we're unable to approve your loan application at this time. "
         "This decision is based on various factors including credit history, business financials, and loan-to-revenue ratio."]]
       
       ;; Option to open an account anyway
       [:div.card.border-2 {:style {:border-color (if @wants-account? "#00857c" "#e5e7eb")
                                     :background-color (when @wants-account? "rgba(0, 133, 124, 0.05)")}}
        [:div.flex.items-start.gap-4
         [:div.flex-shrink-0.mt-1
          [:input {:type "checkbox"
                   :checked @wants-account?
                   :on-change #(do (reset! wants-account? (not @wants-account?))
                                   (on-continue-with-account @wants-account?))
                   :class "w-5 h-5 rounded border-gray-300"
                   :style {:accent-color "#00857c"}}]]
         [:div
          [:h3.font-semibold.text-lg.mb-2 {:style {:color (if @wants-account? "#00857c" "#333")}}
           "Would you still like to open a business account?"]
          [:p.text-gray-600.text-sm.mb-3
           "Even though your loan wasn't approved, you can still open a business checking, savings, "
           "or money market account with Cambridge Savings Bank."]
          [:div.flex.flex-wrap.gap-4.text-sm
           [:span.flex.items-center.gap-1
            [:span {:style {:color "#00857c"}} "✓"] "No monthly fees available"]
           [:span.flex.items-center.gap-1
            [:span {:style {:color "#00857c"}} "✓"] "Free online banking"]
           [:span.flex.items-center.gap-1
            [:span {:style {:color "#00857c"}} "✓"] "Debit card included"]]]]]
       
       [:div.card
        [:h3.font-semibold.text-lg.mb-4 "Other Options"]
        [:ul.space-y-3.text-gray-600
         [:li.flex.gap-3
          [:span {:style {:color "#00857c"}} "•"]
          "You may appeal this decision by calling our business banking team at 1-888-418-5626"]
         [:li.flex.gap-3
          [:span {:style {:color "#00857c"}} "•"]
          "You may reapply for a loan in 6 months if your financial situation changes"]
         [:li.flex.gap-3
          [:span {:style {:color "#00857c"}} "•"]
          "Consider speaking with a banker about other financing options"]]]])))

(defn pending-view [form-data]
  [:div.space-y-6
   [:div.card.text-center.py-8
    [:div.w-20.h-20.rounded-full.bg-yellow-100.flex.items-center.justify-center.mx-auto.mb-4
     [:span.text-4xl "⏳"]]
    [:h2.text-2xl.font-bold.text-gray-900.mb-2 "Additional Review Required"]
    [:p.text-gray-600 (:loan-decision-notes form-data)]]
   
   [:div.card
    [:h3.font-semibold.text-lg.mb-4 "What Happens Next"]
    [:ul.space-y-3.text-gray-600
     [:li.flex.gap-3
      [:span {:style {:color "#00857c"}} "1."]
      "Our underwriting team will review your complete application"]
     [:li.flex.gap-3
      [:span {:style {:color "#00857c"}} "2."]
      "We may contact you for additional documentation"]
     [:li.flex.gap-3
      [:span {:style {:color "#00857c"}} "3."]
      "You'll receive a final decision within 3-5 business days"]
     [:li.flex.gap-3
      [:span {:style {:color "#00857c"}} "4."]
      "If approved, we'll proceed with opening your checking account"]]]])

(defn loan-decision-step []
  (let [form-data (:form-data @state/app-state)
        decision (:loan-decision form-data)
        processing? (r/atom false)
        continue-with-account? (r/atom false)]
    
    ;; Simulate decision if not yet made
    (when (nil? decision)
      (reset! processing? true)
      (js/setTimeout 
       (fn []
         (state/simulate-loan-decision!)
         (reset! processing? false))
       2000))
    
    (fn []
      (let [form-data (:form-data @state/app-state)
            decision (:loan-decision form-data)
            on-continue-with-account (fn [wants-account]
                                        (reset! continue-with-account? wants-account)
                                        (state/update-form-data! {:continue-with-account-after-denial wants-account}))]
        [:div.space-y-6
         [:div.card
          [:div.flex.items-center.gap-3.mb-4
           [:div.step-dot.active "7"]
           [:h2.text-xl.font-bold.text-gray-900.uppercase.tracking-wide 
            {:style {:letter-spacing "1px"}}
            "Loan Decision"]]]
         
         (cond
           @processing?
           [:div.card.text-center.py-12
            [:div.animate-spin.w-12.h-12.border-4.border-gray-200.rounded-full.mx-auto.mb-4
             {:style {:border-top-color "#00857c"}}]
            [:h3.text-lg.font-semibold.text-gray-900.mb-2 "Processing Your Application"]
            [:p.text-gray-600 "Please wait while we review your information..."]]
           
           (= decision :approved)
           [approved-view form-data]
           
           (= decision :denied)
           [denied-view form-data on-continue-with-account]
           
           (= decision :pending-review)
           [pending-view form-data]
           
           :else
           [:div.card.text-center.py-8
            [:p.text-gray-600 "Processing..."]])
         
         ;; Navigation
         (when (some? decision)
           [:div.flex.justify-between.pt-6
            [:button.btn-secondary
             {:on-click state/go-back!}
             "← Back"]
            [:button.btn-primary
             {:on-click state/go-next!}
             (cond
               (= decision :denied)
               (if @continue-with-account?
                 "Continue to Account Selection →"
                 "Complete Application →")
               :else
               "Continue to Account Setup →")]])]))))
