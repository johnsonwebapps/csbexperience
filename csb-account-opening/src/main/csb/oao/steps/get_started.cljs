(ns csb.oao.steps.get-started
  (:require [reagent.core :as r]
            [csb.oao.state :as state]))

;; Get Started step for Q2-BUS-035A (new customer, no OB account)

(def before-you-begin-items
  [["📄" "Business formation documents (Articles of Incorporation, LLC Agreement, etc.)"]
   ["🔢" "Employer Identification Number (EIN) or Tax ID"]
   ["🪪" "Government-issued photo ID for all owners"]
   ["📊" "SSN/ITIN for owners with 25%+ ownership"]
   ["🏠" "Business and personal addresses"]
   ["💳" "Initial deposit information"]])

(defn get-started-step []
  [:div.space-y-6
   ;; Hero banner
   [:div.rounded-lg.overflow-hidden
    {:style {:background "linear-gradient(135deg, #00857c 0%, #006b64 100%)"}}
    [:div.p-8.text-white
     [:h1.text-3xl.font-bold.mb-3 "Open a Business Account"]
     [:p.text-lg {:style {:color "rgba(255,255,255,0.9)"}}
      "Get started in minutes. Complete your application online and enroll in Online Banking — all in one place."]
     [:div.flex.flex-wrap.gap-6.mt-6.text-sm
      (for [item ["FDIC Insured" "No hidden fees" "Online Banking enrollment included" "15–20 minutes"]]
        ^{:key item}
        [:div.flex.items-center.gap-2
         [:span {:style {:color "#fff"}} "✓"]
         [:span item]])]]]
   
   ;; What you'll need
   [:div.bg-white.rounded-xl.p-6 {:style {:box-shadow "0 2px 12px rgba(0,0,0,0.08)"}}
    [:h2.text-lg.font-bold.mb-3 {:style {:color "#333"}} "Before You Begin"]
    [:p.text-gray-600.text-sm.mb-4
     "Please have the following information ready to complete your application:"]
    [:div.grid.sm:grid-cols-2.gap-3.text-sm
     (for [[icon text] before-you-begin-items]
       ^{:key text}
       [:div.flex.items-start.gap-2.bg-gray-50.rounded-lg.p-3
        [:span.text-lg.flex-shrink-0 icon]
        [:span.text-gray-700 text]])]]
   
   ;; What happens after
   [:div.bg-white.rounded-xl.p-6 {:style {:box-shadow "0 2px 12px rgba(0,0,0,0.08)"}}
    [:h2.text-lg.font-bold.mb-3 {:style {:color "#333"}} "What Happens After You Apply"]
    [:div.space-y-3.text-sm
     [:div.flex.items-start.gap-3
      [:div.w-8.h-8.rounded-full.flex.items-center.justify-center.text-white.font-bold.flex-shrink-0
       {:style {:background-color "#00857c"}} "1"]
      [:div
       [:span.font-medium "Identity Verification"]
       [:p.text-gray-500 "Your application is reviewed through our Identity & Fraud Detection service."]]]
     [:div.flex.items-start.gap-3
      [:div.w-8.h-8.rounded-full.flex.items-center.justify-center.text-white.font-bold.flex-shrink-0
       {:style {:background-color "#00857c"}} "2"]
      [:div
       [:span.font-medium "Account Created"]
       [:p.text-gray-500 "Your account is opened in our core banking system."]]]
     [:div.flex.items-start.gap-3
      [:div.w-8.h-8.rounded-full.flex.items-center.justify-center.text-white.font-bold.flex-shrink-0
       {:style {:background-color "#00857c"}} "3"]
      [:div
       [:span.font-medium "Online Banking Enrollment"]
       [:p.text-gray-500 "Create your Login ID and password to access your account online right away."]]]
     [:div.flex.items-start.gap-3
      [:div.w-8.h-8.rounded-full.flex.items-center.justify-center.text-white.font-bold.flex-shrink-0
       {:style {:background-color "#00857c"}} "4"]
      [:div
       [:span.font-medium "Confirmation"]
       [:p.text-gray-500 "Receive a confirmation email with your account details."]]]]]
   
   ;; Start button
   [:div.text-center
    [:button.py-4.px-12.rounded-lg.font-semibold.text-white.text-lg
     {:style {:background-color "#00857c"}
      :on-click #(state/go-next!)}
     "Start Application →"]]])
