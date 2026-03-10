(ns csb.oao.steps.ob-auth
  (:require [reagent.core :as r]
            [csb.oao.state :as state]))

;; Simulated Online Banking credential login for Q2-BUS-035B
;; In production, this authenticates via Q2 Caliper API

(def simulated-customer
  {:first-name "Robert"
   :last-name "Chen"
   :email "robert.chen@chendesign.com"
   :phone "(617) 555-0389"
   :ssn "***-**-6721"
   :dob "1978-11-22"
   :address "485 Massachusetts Ave"
   :city "Cambridge"
   :state "MA"
   :zip "02139"
   :business-legal-name "Chen Design Group LLC"
   :dba-name "Chen Design"
   :business-type "llc"
   :ein "**-***3456"
   :state-of-formation "MA"
   :date-established "2015-02-10"
   :business-phone "(617) 555-0400"
   :business-address "485 Massachusetts Ave, Suite 200"
   :business-city "Cambridge"
   :business-state "MA"
   :business-zip "02139"
   :business-description "Architecture and interior design services"
   :naics-code "541310"})

(defn ob-auth-step []
  (let [login-id (r/atom "")
        password (r/atom "")
        authenticating (r/atom false)
        auth-complete (r/atom false)
        auth-error (r/atom nil)]
    (fn []
      [:div.space-y-6
       ;; Hero
       [:div.rounded-lg.overflow-hidden
        {:style {:background "linear-gradient(135deg, #00857c 0%, #006b64 100%)"}}
        [:div.p-8.text-white
         [:h1.text-3xl.font-bold.mb-3 "Open a New Account"]
         [:p.text-lg {:style {:color "rgba(255,255,255,0.9)"}}
          "Already have Online Banking? Sign in to pre-fill your application with your existing information."]]]
       
       ;; Login Card
       [:div.bg-white.rounded-xl.p-8 {:style {:box-shadow "0 2px 12px rgba(0,0,0,0.08)"}}
        [:h2.text-xl.font-bold.mb-2 {:style {:color "#333"}} "Sign In with Online Banking"]
        [:p.text-sm.text-gray-500.mb-6 "Use your existing Online Banking credentials to securely access your account information."]
        
        (cond
          @auth-error
          [:div
           [:div.rounded-lg.p-4.mb-6 {:style {:background-color "#FEF2F2" :border "1px solid #FECACA"}}
            [:div.flex.items-center.gap-2.mb-2
             [:span.text-red-500 "⚠"]
             [:span.font-semibold.text-red-700 "Authentication Failed"]]
            [:p.text-sm.text-red-600 @auth-error]]
           [:button.w-full.py-3.rounded-lg.font-semibold.text-white
            {:style {:background-color "#00857c"}
             :on-click #(do (reset! auth-error nil)
                            (reset! authenticating false))}
            "Try Again"]]
          
          @auth-complete
          [:div
           [:div.rounded-lg.p-4.mb-6 {:style {:background-color "#F0FDF4" :border "1px solid #BBF7D0"}}
            [:div.flex.items-center.gap-2.mb-2
             [:span {:style {:color "#16A34A"}} "✓"]
             [:span.font-semibold {:style {:color "#166534"}} "Signed In Successfully"]]
            [:p.text-sm {:style {:color "#166534"}}
             "Your identity has been verified through Q2 authentication. Your information is ready to pre-fill."]]
           
           ;; Show customer info
           [:div.rounded-lg.p-4.mb-6 {:style {:background-color "#F8FAFC" :border "1px solid #E2E8F0"}}
            [:h3.font-semibold.mb-3 {:style {:color "#333"}} "Signed in as:"]
            [:div.grid.grid-cols-2.gap-4.text-sm
             [:div [:span.text-gray-500 "Name: "] [:span.font-medium (:first-name simulated-customer) " " (:last-name simulated-customer)]]
             [:div [:span.text-gray-500 "Email: "] [:span.font-medium (:email simulated-customer)]]
             [:div [:span.text-gray-500 "Business: "] [:span.font-medium (:business-legal-name simulated-customer)]]
             [:div [:span.text-gray-500 "EIN: "] [:span.font-medium (:ein simulated-customer)]]]]
           
           [:button.w-full.py-3.rounded-lg.font-semibold.text-white
            {:style {:background-color "#00857c"}
             :on-click #(do (state/update-form-data! simulated-customer)
                            (state/go-next!))}
            "Continue to Application →"]]
          
          @authenticating
          [:div.text-center.py-12
           [:div.mb-4
            [:div.inline-block.w-8.h-8.border-4.rounded-full.animate-spin
             {:style {:border-color "#e5e7eb" :border-top-color "#00857c"}}]]
           [:p.font-semibold.text-gray-700 "Authenticating..."]
           [:p.text-sm.text-gray-500.mt-2 "Verifying credentials via Q2 Caliper API"]]
          
          :else
          [:div
           [:div.space-y-4
            [:div
             [:label.block.text-sm.font-medium.text-gray-700.mb-1 "Login ID"]
             [:input.w-full.px-4.py-3.border.border-gray-300.rounded-lg
              {:type "text"
               :value @login-id
               :placeholder "Enter your Online Banking Login ID"
               :on-change #(reset! login-id (.. % -target -value))}]]
            [:div
             [:label.block.text-sm.font-medium.text-gray-700.mb-1 "Password"]
             [:input.w-full.px-4.py-3.border.border-gray-300.rounded-lg
              {:type "password"
               :value @password
               :placeholder "Enter your password"
               :on-change #(reset! password (.. % -target -value))}]]]
           
           [:button.w-full.py-3.rounded-lg.font-semibold.text-white.mt-6
            {:style {:background-color "#00857c"}
             :on-click #(if (or (empty? @login-id) (empty? @password))
                          (reset! auth-error "Please enter both your Login ID and Password.")
                          (do (reset! authenticating true)
                              (js/setTimeout
                               (fn []
                                 (reset! authenticating false)
                                 (reset! auth-complete true)
                                 (state/update-form-data! {:ob-session-valid true
                                                          :ob-login-id @login-id}))
                               2000)))}
            "Sign In"]
           
           [:div.mt-6.pt-6 {:style {:border-top "1px solid #e5e7eb"}}
            [:p.text-sm.text-gray-500.text-center
             "Your credentials are verified through the Q2 authentication service. "
             "We never store your Online Banking password."]]])]])))
