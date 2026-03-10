(ns csb.oao.steps.sso-auth
  (:require [reagent.core :as r]
            [csb.oao.state :as state]))

;; Simulated SSO authentication from Online Banking
;; In production, this would validate a JWT token from the OB session

(def simulated-customer
  {:first-name "Jane"
   :last-name "Mitchell"
   :email "jane.mitchell@techstartup.com"
   :phone "(617) 555-0142"
   :ssn "***-**-4589"
   :dob "1985-03-15"
   :address "250 Main Street"
   :city "Cambridge"
   :state "MA"
   :zip "02142"
   :business-legal-name "Mitchell Tech Solutions LLC"
   :dba-name "Mitchell Tech"
   :business-type "llc"
   :ein "**-***7890"
   :state-of-formation "MA"
   :date-established "2019-06-01"
   :business-phone "(617) 555-0200"
   :business-address "100 CambridgePark Drive"
   :business-city "Cambridge"
   :business-state "MA"
   :business-zip "02140"
   :business-description "Technology consulting and software development"
   :naics-code "541512"})

(defn sso-auth-step []
  (let [authenticating (r/atom false)
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
          "Welcome back! We're securely connecting to your Online Banking session to pre-fill your application."]]]
       
       ;; SSO Status Card
       [:div.bg-white.rounded-xl.p-8 {:style {:box-shadow "0 2px 12px rgba(0,0,0,0.08)"}}
        [:h2.text-xl.font-bold.mb-6 {:style {:color "#333"}} "Single Sign-On Authentication"]
        
        (cond
          @auth-error
          [:div
           [:div.rounded-lg.p-4.mb-6 {:style {:background-color "#FEF2F2" :border "1px solid #FECACA"}}
            [:div.flex.items-center.gap-2.mb-2
             [:span.text-red-500 "⚠"]
             [:span.font-semibold.text-red-700 "Authentication Error"]]
            [:p.text-sm.text-red-600 @auth-error]]
           [:button.w-full.py-3.rounded-lg.font-semibold.text-white
            {:style {:background-color "#00857c"}
             :on-click #(do (reset! auth-error nil)
                            (reset! authenticating false)
                            (reset! auth-complete false))}
            "Retry Authentication"]]
          
          @auth-complete
          [:div
           [:div.rounded-lg.p-4.mb-6 {:style {:background-color "#F0FDF4" :border "1px solid #BBF7D0"}}
            [:div.flex.items-center.gap-2.mb-2
             [:span {:style {:color "#16A34A"}} "✓"]
             [:span.font-semibold {:style {:color "#166534"}} "Session Verified"]]
            [:p.text-sm {:style {:color "#166534"}}
             "Your Online Banking session has been authenticated. Your information has been securely transferred."]]
           
           ;; Show verified identity
           [:div.rounded-lg.p-4.mb-6 {:style {:background-color "#F8FAFC" :border "1px solid #E2E8F0"}}
            [:h3.font-semibold.mb-3 {:style {:color "#333"}} "Verified Customer Identity"]
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
           [:div.mb-4 {:style {:font-size "48px"}} "🔐"]
           [:div.mb-4
            [:div.inline-block.w-8.h-8.border-4.rounded-full.animate-spin
             {:style {:border-color "#e5e7eb" :border-top-color "#00857c"}}]]
           [:p.font-semibold.text-gray-700 "Validating SSO Token..."]
           [:p.text-sm.text-gray-500.mt-2 "Securely verifying your Online Banking session"]
           [:div.mt-6.space-y-2.text-sm
            [:div.flex.items-center.gap-2.text-gray-600
             [:span {:style {:color "#00857c"}} "✓"] "Session token received"]
            [:div.flex.items-center.gap-2.text-gray-600
             [:span {:style {:color "#00857c"}} "✓"] "JWT signature validated (RS256)"]
            [:div.flex.items-center.gap-2.text-gray-400
             [:span "○"] "Extracting customer attributes..."]]]
          
          :else
          [:div.text-center.py-8
           [:div.mb-6 {:style {:font-size "64px"}} "🏦"]
           [:p.text-gray-600.mb-6
            "Click below to simulate the SSO authentication from your Online Banking session. "
            "In production, this occurs automatically when you select 'Open New Account' from the OB menu."]
           
           [:div.rounded-lg.p-4.mb-6 {:style {:background-color "#FFF7ED" :border "1px solid #FED7AA"}}
            [:div.flex.items-start.gap-2
             [:span "ℹ️"]
             [:div.text-sm {:style {:color "#9A3412"}}
              [:p.font-medium "How SSO Works:"]
              [:ul.list-disc.ml-4.mt-1.space-y-1
               [:li "Your authenticated OB session generates a secure JWT token"]
               [:li "The token contains your verified identity attributes"]
               [:li "Token is digitally signed, encrypted, expires in 60 seconds, and is single-use"]
               [:li "Your information is securely pre-filled into the application"]]]]]
           
           [:button.w-full.py-4.rounded-lg.font-semibold.text-white.text-lg
            {:style {:background-color "#00857c"}
             :on-click #(do (reset! authenticating true)
                            (js/setTimeout
                             (fn []
                               (reset! authenticating false)
                               (reset! auth-complete true)
                               (state/update-form-data! {:ob-session-valid true}))
                             2500))}
            "Authenticate via Online Banking SSO"]])]])))
