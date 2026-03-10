(ns csb.oao.steps.confirmation
  (:require [reagent.core :as r]
            [csb.oao.state :as state]))

;; Confirmation step — shared across all three flows with entry-type-specific content

(defn confirmation-step []
  (let [{:keys [form-data confirmation-data]} @state/app-state
        entry-type (:entry-type form-data)
        product (->> state/account-products
                     (filter #(= (:id %) (:selected-account form-data)))
                     first)]
    [:div.space-y-6
     ;; Success banner
     [:div.rounded-lg.overflow-hidden.text-center
      {:style {:background "linear-gradient(135deg, #16A34A 0%, #15803D 100%)"}}
      [:div.p-8.text-white
       [:div.mb-4 {:style {:font-size "64px"}} "🎉"]
       [:h1.text-3xl.font-bold.mb-2 "Account Opened Successfully!"]
       [:p.text-lg {:style {:color "rgba(255,255,255,0.9)"}}
        (str "Welcome, " (:first-name form-data) "! Your new account is ready.")]]]
     
     ;; Account Details
     [:div.bg-white.rounded-xl.p-6 {:style {:box-shadow "0 2px 12px rgba(0,0,0,0.08)"}}
      [:h2.text-xl.font-bold.mb-4 {:style {:color "#333"}} "Account Details"]
      [:div.space-y-2
       [:div.flex.justify-between.py-2 {:style {:border-bottom "1px solid #f0f0f0"}}
        [:span.text-sm.text-gray-500 "Account Number"]
        [:span.text-sm.font-bold {:style {:color "#00857c"}} (:account-number confirmation-data)]]
       [:div.flex.justify-between.py-2 {:style {:border-bottom "1px solid #f0f0f0"}}
        [:span.text-sm.text-gray-500 "Account Type"]
        [:span.text-sm.font-medium (or (:name product) (:selected-account form-data))]]
       [:div.flex.justify-between.py-2 {:style {:border-bottom "1px solid #f0f0f0"}}
        [:span.text-sm.text-gray-500 "Business"]
        [:span.text-sm.font-medium (:business-legal-name form-data)]]
       (when (seq (:deposit-amount form-data))
         [:div.flex.justify-between.py-2 {:style {:border-bottom "1px solid #f0f0f0"}}
          [:span.text-sm.text-gray-500 "Initial Deposit"]
          [:span.text-sm.font-medium (str "$" (:deposit-amount form-data))]])
       [:div.flex.justify-between.py-2 {:style {:border-bottom "1px solid #f0f0f0"}}
        [:span.text-sm.text-gray-500 "Opened"]
        [:span.text-sm.font-medium (:timestamp confirmation-data)]]]]
     
     ;; Entry-type specific content
     (case entry-type
       :sso
       [:div.bg-white.rounded-xl.p-6 {:style {:box-shadow "0 2px 12px rgba(0,0,0,0.08)"}}
        [:div.flex.items-center.gap-2.mb-3
         [:span {:style {:font-size "24px"}} "🔗"]
         [:h3.font-semibold {:style {:color "#333"}} "Linked to Online Banking"]]
        [:p.text-sm.text-gray-600.mb-3
         "Your new account has been automatically linked to your Online Banking profile and is visible in your accounts list."]
        [:div.rounded-lg.p-3 {:style {:background-color "#F0FDF4" :border "1px solid #BBF7D0"}}
         [:div.flex.items-center.gap-2
          [:span {:style {:color "#16A34A"}} "✓"]
          [:span.text-sm {:style {:color "#166534"}} "Account linked to your existing OB profile"]]]]
       
       :new-enroll
       [:div.bg-white.rounded-xl.p-6 {:style {:box-shadow "0 2px 12px rgba(0,0,0,0.08)"}}
        [:div.flex.items-center.gap-2.mb-3
         [:span {:style {:font-size "24px"}} "🌐"]
         [:h3.font-semibold {:style {:color "#333"}} "Online Banking Enrolled"]]
        [:p.text-sm.text-gray-600.mb-3
         "Your Online Banking access has been set up. You can log in right away to manage your account."]
        [:div.space-y-2
         [:div.rounded-lg.p-3 {:style {:background-color "#F0FDF4" :border "1px solid #BBF7D0"}}
          [:div.flex.items-center.gap-2
           [:span {:style {:color "#16A34A"}} "✓"]
           [:span.text-sm {:style {:color "#166534"}} (str "Login ID: " (:new-login-id form-data))]]]
         [:div.rounded-lg.p-3 {:style {:background-color "#F0FDF4" :border "1px solid #BBF7D0"}}
          [:div.flex.items-center.gap-2
           [:span {:style {:color "#16A34A"}} "✓"]
           [:span.text-sm {:style {:color "#166534"}} "MFA configured"]]]]]
       
       :ob-login
       [:div.bg-white.rounded-xl.p-6 {:style {:box-shadow "0 2px 12px rgba(0,0,0,0.08)"}}
        [:div.flex.items-center.gap-2.mb-3
         [:span {:style {:font-size "24px"}} "🔗"]
         [:h3.font-semibold {:style {:color "#333"}} "Linked to Online Banking"]]
        [:p.text-sm.text-gray-600.mb-3
         "Your new account has been linked to the customer profile associated with your Online Banking credentials."]
        [:div.rounded-lg.p-3 {:style {:background-color "#F0FDF4" :border "1px solid #BBF7D0"}}
         [:div.flex.items-center.gap-2
          [:span {:style {:color "#16A34A"}} "✓"]
          [:span.text-sm {:style {:color "#166534"}} "Account visible in your OB profile"]]]]
       
       nil)
     
     ;; Email confirmation
     [:div.rounded-lg.p-4 {:style {:background-color "#EFF6FF" :border "1px solid #BFDBFE"}}
      [:div.flex.items-center.gap-2
       [:span "📧"]
       [:p.text-sm {:style {:color "#1E40AF"}}
        (str "A confirmation email has been sent to " (:email form-data) " with your account details.")]]]
     
     ;; What's Next
     [:div.bg-white.rounded-xl.p-6 {:style {:box-shadow "0 2px 12px rgba(0,0,0,0.08)"}}
      [:h3.font-semibold.mb-3 {:style {:color "#333"}} "What's Next"]
      [:div.space-y-3.text-sm
       [:div.flex.items-start.gap-2
        [:span {:style {:color "#00857c"}} "→"]
        [:span.text-gray-600 "Your account is ready to use immediately"]]
       [:div.flex.items-start.gap-2
        [:span {:style {:color "#00857c"}} "→"]
        [:span.text-gray-600 "Set up bill pay, online transfers, and alerts from Online Banking"]]
       [:div.flex.items-start.gap-2
        [:span {:style {:color "#00857c"}} "→"]
        [:span.text-gray-600 "Download the CSB mobile app for banking on the go"]]
       [:div.flex.items-start.gap-2
        [:span {:style {:color "#00857c"}} "→"]
        [:span.text-gray-600 "Visit any CSB branch for your debit card and checks"]]]]
     
     ;; Actions
     [:div.flex.gap-4.justify-center
      (when (= entry-type :new-enroll)
        [:a.py-3.px-6.rounded-lg.font-semibold.text-white
         {:style {:background-color "#00857c"}
          :href "#"}
         "Log In to Online Banking"])
      [:button.py-3.px-6.rounded-lg.font-semibold
       {:style {:color "#00857c" :border "1px solid #00857c"}
        :on-click #(do (state/start-over!)
                       ((resolve 'csb.state/go-to-landing!)))}
       "Return to Home"]]]))
