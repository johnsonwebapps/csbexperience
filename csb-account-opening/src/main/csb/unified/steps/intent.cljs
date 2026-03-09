(ns csb.unified.steps.intent
  (:require [csb.unified.state :as state]))

(defn flow-card [flow-type selected? on-select]
  (let [{:keys [id label desc]} flow-type]
    [:div.p-6.rounded-xl.cursor-pointer.transition-all.border-2
     {:class (if selected?
               "border-2 shadow-lg"
               "border-gray-200 hover:border-gray-300 hover:shadow-md")
      :style (when selected?
               {:border-color "#00857c"
                :background-color "rgba(0, 133, 124, 0.05)"})
      :on-click #(on-select id)}
     [:div.flex.items-start.gap-4
      [:div.w-6.h-6.rounded-full.border-2.flex.items-center.justify-center.flex-shrink-0.mt-0.5
       {:style {:border-color (if selected? "#00857c" "#d1d5db")}}
       (when selected?
         [:div.w-3.h-3.rounded-full {:style {:background-color "#00857c"}}])]
      [:div.flex-1
       [:h3.font-bold.text-lg.mb-1 {:style {:color (if selected? "#00857c" "#333")}} label]
       [:p.text-gray-600.text-sm desc]]]]))

(defn intent-step []
  (let [form-data (:form-data @state/app-state)
        selected-flow (:flow-type form-data)]
    [:div.space-y-6
     [:div.card
      [:h2.text-xl.font-bold.text-gray-900.mb-2 "Welcome to Cambridge Savings Bank"]
      [:p.text-gray-600.mb-6
       "Let's get your business set up with the right banking solutions. "
       "What would you like to do today?"]]
     
     ;; Flow selection cards
     [:div.space-y-4
      (for [flow state/flow-types]
        ^{:key (:id flow)}
        [flow-card flow (= (:id flow) selected-flow) state/set-flow-type!])]
     
     ;; Info box about the process
     [:div.rounded-lg.p-4 {:style {:background-color "rgba(0, 133, 124, 0.08)"
                                   :border "1px solid rgba(0, 133, 124, 0.2)"}}
      [:div.flex.gap-3
       [:div.text-xl "ℹ️"]
       [:div
        [:h4.font-semibold.mb-1 {:style {:color "#00857c"}} "Streamlined Application"]
        [:p.text-sm.text-gray-600
         "We've simplified the process by collecting your business and personal information just once. "
         (case selected-flow
           :loan-only "If your loan is approved, we'll automatically open a checking account for your loan payments at no additional cost."
           :loan-and-account "You can apply for financing and open additional accounts in one easy application."
           :account-only "Open checking, savings, money market, or CD accounts for your business."
           "Select an option above to get started.")]]]]
     
     ;; Continue button
     [:div.flex.justify-end.pt-4
      [:button.font-bold.py-3.px-8.rounded-lg.text-white.transition-all
       {:style {:background-color (if selected-flow "#00857c" "#ccc")}
        :disabled (nil? selected-flow)
        :on-click #(when selected-flow (state/go-next!))}
       "Continue →"]]]))
