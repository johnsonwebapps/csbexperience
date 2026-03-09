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
    [:h3.font-semibold.text-lg.mb-4.pb-2.border-b {:style {:color "#00857c"}}
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
   
   [:div.rounded-lg.p-4 {:style {:background-color "rgba(0, 133, 124, 0.08)"
                                 :border "1px solid rgba(0, 133, 124, 0.2)"}}
    [:div.flex.gap-3
     [:div.text-2xl "🏦"]
     [:div
      [:h4.font-semibold.mb-1 {:style {:color "#00857c"}} "Checking Account Required"]
      [:p.text-sm.text-gray-600
       "As part of your loan approval, we'll open a Business Checking account for your loan payments. "
       "This account has no monthly fees and includes free online banking."]]]]])

(defn denied-view [form-data]
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
     "This decision is based on various factors including credit history, business financials, and loan-to-revenue ratio."]
    [:p.text-gray-600
     "Since a loan was not approved, we will not be opening a checking account. "
     "However, you can still apply for a business account separately if you'd like."]]
   
   [:div.card
    [:h3.font-semibold.text-lg.mb-4 "Next Steps"]
    [:ul.space-y-3.text-gray-600
     [:li.flex.gap-3
      [:span {:style {:color "#00857c"}} "•"]
      "You may appeal this decision by calling our business banking team at 1-888-418-5626"]
     [:li.flex.gap-3
      [:span {:style {:color "#00857c"}} "•"]
      "You may reapply in 6 months if your financial situation changes"]
     [:li.flex.gap-3
      [:span {:style {:color "#00857c"}} "•"]
      "Consider speaking with a banker about other financing options"]]]])

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
        processing? (r/atom false)]
    
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
            decision (:loan-decision form-data)]
        [:div.space-y-6
         [:div.card
          [:div.flex.items-center.gap-2.mb-4
           [:div.w-8.h-8.rounded-full.flex.items-center.justify-center.text-white.text-sm.font-bold
            {:style {:background-color "#00857c"}} "6"]
           [:h2.text-xl.font-bold.text-gray-900 "Loan Decision"]]]
         
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
           [denied-view form-data]
           
           (= decision :pending-review)
           [pending-view form-data]
           
           :else
           [:div.card.text-center.py-8
            [:p.text-gray-600 "Processing..."]])
         
         ;; Navigation
         (when (some? decision)
           [:div.flex.justify-between.pt-4
            [:button.font-medium.py-3.px-6.rounded-lg.transition-all
             {:style {:color "#00857c" :border "2px solid #00857c"}
              :on-click state/go-back!}
             "← Back"]
            [:button.font-bold.py-3.px-8.rounded-lg.text-white.transition-all
             {:style {:background-color "#00857c"}
              :on-click state/go-next!}
             (if (= decision :denied)
               "Complete Application →"
               "Continue to Account Setup →")]])]))))
