(ns csb.unified.steps.intent
  (:require [csb.unified.state :as state]))

(defn flow-card [flow-type selected? on-select]
  (let [{:keys [id label desc]} flow-type]
    [:div.p-6.cursor-pointer.transition-all.border-2
     {:class (str "rounded "
                  (if selected?
                    "border-2 shadow-lg"
                    "border-gray-200 hover:border-gray-300 hover:shadow-md"))
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
        selected-flow (:flow-type form-data)
        sso-authenticated? (:sso-authenticated form-data)]
    [:div.space-y-6
     [:div.card
      [:h2.text-2xl.font-bold.text-gray-900.mb-2.uppercase.tracking-wide
       {:style {:letter-spacing "1px"}}
       (if sso-authenticated?
         (str "Welcome back, " (:owner-first-name form-data) "!")
         "Welcome to Cambridge Savings Bank")]
      [:p.text-gray-600.mb-6
       (if sso-authenticated?
         "We've pre-filled your information from your Online Banking profile. What would you like to do today?"
         "Let's get your business set up with the right banking solutions. What would you like to do today?")]]
     
     ;; SSO Pre-filled data summary
     (when sso-authenticated?
       [:div.rounded.p-4.mb-4 {:style {:background-color "#e6f4f2"
                                        :border-left "4px solid #00857c"}}
        [:div.flex.items-start.gap-3
         [:div.text-xl "✓"]
         [:div.flex-1
          [:h4.font-semibold.mb-2.uppercase.text-sm.tracking-wide {:style {:color "#00857c"}} 
           "Your Pre-filled Information"]
          [:div.grid.grid-cols-2.gap-x-8.gap-y-1.text-sm
           [:div
            [:span.text-gray-500 "Business: "]
            [:span.font-medium (:business-legal-name form-data)]]
           [:div
            [:span.text-gray-500 "EIN: "]
            [:span.font-medium (:ein form-data)]]
           [:div
            [:span.text-gray-500 "Owner: "]
            [:span.font-medium (str (:owner-first-name form-data) " " (:owner-last-name form-data))]]
           [:div
            [:span.text-gray-500 "Email: "]
            [:span.font-medium (:owner-email form-data)]]]
          [:p.text-xs.text-gray-500.mt-2 
           "You can review and edit this information in the next steps."]]]])
     
     ;; Flow selection cards
     [:div.space-y-4
      (for [flow state/flow-types]
        ^{:key (:id flow)}
        [flow-card flow (= (:id flow) selected-flow) state/set-flow-type!])]
     
     ;; Info box about the process
     [:div.rounded.p-4 {:style {:background-color "rgba(0, 133, 124, 0.08)"
                                 :border-left "4px solid #00857c"}}
      [:div.flex.gap-3
       [:div.text-xl "ℹ️"]
       [:div
        [:h4.font-semibold.mb-1.uppercase.text-sm.tracking-wide {:style {:color "#00857c"}} 
         (if sso-authenticated? "Faster Application" "Streamlined Application")]
        [:p.text-sm.text-gray-600
         (if sso-authenticated?
           "Since you're signed in, most of your information is already on file. "
           "We've simplified the process by collecting your business and personal information just once. ")
         (case selected-flow
           :loan-only "If your loan is approved, we'll automatically open a checking account for your loan payments at no additional cost."
           :loan-and-account "You can apply for financing and open additional accounts in one easy application."
           :account-only "Open checking, savings, money market, or CD accounts for your business."
           "Select an option above to get started.")]]]]
     
     ;; Continue button
     [:div.flex.justify-end.pt-6
      [:button.btn-primary
       {:disabled (nil? selected-flow)
        :on-click #(when selected-flow (state/go-next!))}
       "Continue →"]]]))
