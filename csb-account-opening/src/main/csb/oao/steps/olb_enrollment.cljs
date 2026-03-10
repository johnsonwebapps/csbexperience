(ns csb.oao.steps.olb-enrollment
  (:require [reagent.core :as r]
            [csb.oao.state :as state]))

;; Online Banking Enrollment step for Q2-BUS-035A
;; Creates OB credentials via Q2 Caliper API

(defn text-field [label field-key form-data & [{:keys [placeholder type]}]]
  [:div
   [:label.block.text-sm.font-medium.text-gray-700.mb-1 label]
   [:input.w-full.px-3.py-2.border.border-gray-300.rounded-lg.focus:outline-none
    {:type (or type "text")
     :placeholder (or placeholder "")
     :value (get form-data field-key "")
     :on-change #(state/update-form-data! {field-key (.. % -target -value)})}]])

(defn olb-enrollment-step []
  (let [checking-login (r/atom false)
        login-available (r/atom nil)
        mfa-sent (r/atom false)
        mfa-verified-local (r/atom false)
        enrolling (r/atom false)
        error (r/atom nil)]
    (fn []
      (let [{:keys [form-data]} @state/app-state]
        [:div.space-y-6
         ;; Header
         [:div.rounded-lg.overflow-hidden
          {:style {:background "linear-gradient(135deg, #00857c 0%, #006b64 100%)"}}
          [:div.p-6.text-white
           [:div.flex.items-center.gap-3.mb-2
            [:span {:style {:font-size "28px"}} "🌐"]
            [:h2.text-2xl.font-bold "Enroll in Online Banking"]]
           [:p {:style {:color "rgba(255,255,255,0.9)"}}
            "Set up your Online Banking access to manage your new account online."]]]
         
         ;; Step 1: Create Login ID
         [:div.bg-white.rounded-xl.p-6 {:style {:box-shadow "0 2px 12px rgba(0,0,0,0.08)"}}
          [:div.flex.items-center.gap-2.mb-4
           [:div.w-7.h-7.rounded-full.flex.items-center.justify-center.text-white.font-bold.text-sm
            {:style {:background-color "#00857c"}} "1"]
           [:h3.font-semibold {:style {:color "#333"}} "Create Your Login ID"]]
          
          [:div.space-y-4
           [:div
            [:label.block.text-sm.font-medium.text-gray-700.mb-1 "Choose a Login ID *"]
            [:div.flex.gap-2
             [:input.flex-1.px-3.py-2.border.border-gray-300.rounded-lg
              {:type "text"
               :value (:new-login-id form-data "")
               :placeholder "Enter a unique Login ID"
               :on-change #(do (state/update-form-data! {:new-login-id (.. % -target -value)})
                               (reset! login-available nil))}]
             [:button.px-4.py-2.rounded-lg.text-sm.font-medium
              {:style {:background-color "#00857c" :color "white"}
               :on-click #(when (seq (:new-login-id form-data))
                            (reset! checking-login true)
                            (js/setTimeout
                             (fn []
                               (reset! checking-login false)
                               (reset! login-available true))
                             1000))}
              (if @checking-login "Checking..." "Check Availability")]]
            (when (some? @login-available)
              (if @login-available
                [:p.text-sm.mt-1 {:style {:color "#16A34A"}} "✓ Login ID is available"]
                [:p.text-sm.mt-1.text-red-500 "✗ Login ID is not available. Please choose another."]))
            [:p.text-xs.text-gray-400.mt-1 "Must be unique. Allowed characters: letters, numbers, periods, underscores."]]]]
         
         ;; Step 2: Create Password
         [:div.bg-white.rounded-xl.p-6 {:style {:box-shadow "0 2px 12px rgba(0,0,0,0.08)"}}
          [:div.flex.items-center.gap-2.mb-4
           [:div.w-7.h-7.rounded-full.flex.items-center.justify-center.text-white.font-bold.text-sm
            {:style {:background-color "#00857c"}} "2"]
           [:h3.font-semibold {:style {:color "#333"}} "Create Your Password"]]
          
          [:div.space-y-4
           [text-field "Password *" :new-password form-data {:type "password" :placeholder "Enter a secure password"}]
           [text-field "Confirm Password *" :confirm-password form-data {:type "password" :placeholder "Re-enter your password"}]
           
           [:div.rounded-lg.p-3 {:style {:background-color "#F8FAFC" :border "1px solid #E2E8F0"}}
            [:p.text-xs.font-medium.text-gray-600.mb-2 "Password Requirements:"]
            [:ul.text-xs.text-gray-500.space-y-1
             [:li "• 10–24 characters"]
             [:li "• At least one uppercase letter"]
             [:li "• At least one special character"]
             [:li "• Cannot match your last 5 passwords"]]]]]
         
         ;; Step 3: MFA Setup
         [:div.bg-white.rounded-xl.p-6 {:style {:box-shadow "0 2px 12px rgba(0,0,0,0.08)"}}
          [:div.flex.items-center.gap-2.mb-4
           [:div.w-7.h-7.rounded-full.flex.items-center.justify-center.text-white.font-bold.text-sm
            {:style {:background-color "#00857c"}} "3"]
           [:h3.font-semibold {:style {:color "#333"}} "Set Up Multi-Factor Authentication"]]
          
          [:div.space-y-4
           [text-field "Mobile Phone (for SMS OTP) *" :mfa-phone form-data {:type "tel" :placeholder "(555) 555-5555"}]
           [text-field "Email (for backup verification) *" :mfa-email form-data {:type "email" :placeholder "your@email.com"}]
           
           (if @mfa-verified-local
             [:div.rounded-lg.p-3 {:style {:background-color "#F0FDF4" :border "1px solid #BBF7D0"}}
              [:div.flex.items-center.gap-2
               [:span {:style {:color "#16A34A"}} "✓"]
               [:span.text-sm.font-medium {:style {:color "#166534"}} "MFA verified successfully"]]]
             (if @mfa-sent
               [:div
                [:div.rounded-lg.p-3.mb-3 {:style {:background-color "#FFF7ED" :border "1px solid #FED7AA"}}
                 [:p.text-sm {:style {:color "#9A3412"}} "A verification code has been sent to your phone."]]
                [:div.flex.gap-2
                 [:input.flex-1.px-3.py-2.border.border-gray-300.rounded-lg
                  {:type "text"
                   :value (:mfa-code form-data "")
                   :placeholder "Enter 6-digit code"
                   :on-change #(state/update-form-data! {:mfa-code (.. % -target -value)})}]
                 [:button.px-4.py-2.rounded-lg.text-sm.font-medium
                  {:style {:background-color "#00857c" :color "white"}
                   :on-click #(do (reset! mfa-verified-local true)
                                  (state/update-form-data! {:mfa-verified true}))}
                  "Verify"]]]
               [:button.px-4.py-2.rounded-lg.text-sm.font-medium
                {:style {:background-color "#00857c" :color "white"}
                 :on-click #(when (seq (:mfa-phone form-data))
                              (reset! mfa-sent true))}
                "Send Verification Code"]))]]
         
         (when @error
           [:div.rounded-lg.p-4 {:style {:background-color "#FEF2F2" :border "1px solid #FECACA"}}
            [:p.text-sm.text-red-600 @error]])
         
         ;; Navigation
         [:div.flex.justify-between
          [:button.py-3.px-6.rounded-lg.font-semibold
           {:style {:color "#00857c" :border "1px solid #00857c"}
            :on-click #(state/go-back!)}
           "← Back"]
          (let [ready (and (seq (:new-login-id form-data))
                          (seq (:new-password form-data))
                          (= (:new-password form-data) (:confirm-password form-data))
                          @mfa-verified-local)]
            [:button.py-3.px-8.rounded-lg.font-semibold.text-white
             {:style {:background-color "#00857c"
                      :opacity (if ready 1 0.5)}
              :disabled (not ready)
              :on-click #(if ready
                           (do (reset! enrolling true)
                               (js/setTimeout
                                (fn []
                                  (reset! enrolling false)
                                  (state/submit!))
                                2000))
                           (reset! error "Please complete all fields and verify MFA before proceeding."))}
             (if @enrolling
               "Enrolling..."
               "Complete Enrollment & Submit →")])]]))))
