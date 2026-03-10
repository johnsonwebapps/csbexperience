(ns csb.components.landing
  (:require [csb.state :as state]))

(defn landing-page []
  [:div.min-h-screen.flex.flex-col {:style {:background-color "#f5f5f5"}}
   ;; Header with logo
   [:header.bg-white.border-b.border-gray-200
    [:div.mx-auto.px-4 {:style {:max-width "1200px"}}
     [:div.flex.items-center.justify-between {:style {:height "80px"}}
      [:a {:href "https://www.cambridgesavings.com"}
       [:img {:src "/images/header.png"
              :alt "Cambridge Savings Bank"
              :style {:max-height "680px" :width "2080px"}}]]
      [:div.flex.items-center.gap-4
       [:a.text-sm.font-semibold {:href "tel:888-418-5626"
                                   :style {:color "#00857c"}}
        "888.418.5626"]
       [:a.text-sm.font-semibold.px-4.py-2.rounded
        {:href "#" :style {:color "#00857c"}}
        "Log In"]]]]]
   
   ;; Hero Section
   [:div {:style {:background "linear-gradient(135deg, #00857c 0%, #006b64 100%)"
                  :padding "60px 0"}}
    [:div.mx-auto.px-4.text-center {:style {:max-width "900px"}}
     [:h1.text-white.font-bold.mb-4 {:style {:font-size "42px" :line-height "1.2"}}
      "Small Business Banking"]
     [:p.mb-6 {:style {:color "rgba(255,255,255,0.9)" :font-size "20px"}}
      "Cambridge Savings Bank is here to help your business grow with the right banking solutions."]]]
   
   ;; Main Content
   [:div.flex-1
    [:div.mx-auto.px-4.py-12 {:style {:max-width "1100px"}}
     [:h2.text-center.font-bold.mb-8 {:style {:color "#333" :font-size "28px"}}
      "How can we help your business today?"]
     
     ;; Featured: Unified Application
     [:div.mb-8
      [:div.bg-white.rounded-xl.p-8
       {:style {:box-shadow "0 4px 20px rgba(0,0,0,0.12)"
                :border "2px solid #00857c"
                :background "linear-gradient(135deg, rgba(0,133,124,0.03) 0%, rgba(0,133,124,0.08) 100%)"}}
       [:div.flex.items-start.gap-6
        [:div.text-6xl "⭐"]
        [:div.flex-1
         [:div.flex.items-center.gap-2.mb-2
          [:span.px-3.py-1.rounded-full.text-xs.font-bold.text-white
           {:style {:background-color "#00857c"}} "RECOMMENDED"]
          [:h3.font-bold {:style {:color "#333" :font-size "24px"}}
           "Complete Business Banking Application"]]
         [:p.mb-4 {:style {:color "#666" :font-size "15px" :line-height "1.6"}}
          "Apply for accounts and loans in one streamlined application. "
          "We'll collect your business and personal information once, then guide you through the right products for your needs."]
         [:div.flex.flex-wrap.gap-4.mb-4
          [:span.flex.items-center.gap-1.text-sm.text-gray-600
           [:span {:style {:color "#00857c"}} "✓"] "No duplicate data entry"]
          [:span.flex.items-center.gap-1.text-sm.text-gray-600
           [:span {:style {:color "#00857c"}} "✓"] "Smart loan decisioning"]
          [:span.flex.items-center.gap-1.text-sm.text-gray-600
           [:span {:style {:color "#00857c"}} "✓"] "Account opens only if loan approved"]]
         
         ;; Statistics Box
         [:div.rounded-lg.p-4.mt-4 {:style {:background-color "rgba(0, 133, 124, 0.1)"
                                            :border "1px dashed rgba(0, 133, 124, 0.3)"}}
          [:div.flex.items-center.gap-2.mb-3
           [:span "📊"]
           [:span.font-semibold.text-sm {:style {:color "#00857c"}} "Streamlined Application Benefits"]]
          [:div.grid.grid-cols-4.gap-4.text-center
           [:div
            [:div.text-2xl.font-bold {:style {:color "#00857c"}} "26"]
            [:div.text-xs.text-gray-600 "Duplicate fields eliminated"]]
           [:div
            [:div.text-2xl.font-bold {:style {:color "#00857c"}} "42%"]
            [:div.text-xs.text-gray-600 "Fewer questions"]]
           [:div
            [:div.text-2xl.font-bold {:style {:color "#00857c"}} "~12 min"]
            [:div.text-xs.text-gray-600 "Time saved"]]
           [:div
            [:div.text-2xl.font-bold {:style {:color "#00857c"}} "1"]
            [:div.text-xs.text-gray-600 "Single application"]]]
          [:p.text-xs.text-gray-500.mt-3.text-center
           "vs. completing separate Account Opening (30 fields) + Loan Application (60 fields) = 90 total fields"]]]
        [:button.font-bold.py-4.px-8.rounded-lg.text-white.transition-all.flex-shrink-0
         {:style {:background-color "#00857c" :font-size "16px"}
          :on-click #(state/go-to-unified-application!)}
         "Start Application →"]]]]
     
     ;; Or divider
     [:div.flex.items-center.gap-4.mb-8
      [:div.flex-1.h-px {:style {:background-color "#ddd"}}]
      [:span.text-gray-400.text-sm.font-medium "OR CHOOSE A SPECIFIC PATH"]
      [:div.flex-1.h-px {:style {:background-color "#ddd"}}]]
     
     ;; Cards Grid
     [:div {:style {:display "grid"
                    :grid-template-columns "repeat(2, 1fr)"
                    :gap "2rem"
                    :max-width "800px"
                    :margin "0 auto"}}
      
      ;; Open Business Account Card
      [:div.bg-white.rounded-xl.p-8.text-center
       {:style {:box-shadow "0 4px 20px rgba(0,0,0,0.08)"
                :border "1px solid #e5e5e5"}}
       [:div.mb-4 {:style {:font-size "48px"}} "🏦"]
       [:h3.font-bold.mb-3 {:style {:color "#333" :font-size "22px"}}
        "Open a Business Account"]
       [:p.mb-6 {:style {:color "#666" :font-size "15px" :line-height "1.6"}}
        "Checking, Money Market & CD accounts tailored for your business needs."]
       [:ul.text-left.mb-6.space-y-2 {:style {:font-size "14px" :color "#555"}}
        [:li.flex.items-center.gap-2
         [:span {:style {:color "#00857c"}} "✓"] "Small Business Checking"]
        [:li.flex.items-center.gap-2
         [:span {:style {:color "#00857c"}} "✓"] "Business Growth Checking"]
        [:li.flex.items-center.gap-2
         [:span {:style {:color "#00857c"}} "✓"] "Small Business Money Market Special"]
        [:li.flex.items-center.gap-2
         [:span {:style {:color "#00857c"}} "✓"] "4 Month Certificate of Deposit Special"]]
       [:button.w-full.font-bold.py-4.px-6.rounded-lg.text-white.transition-all
        {:style {:background-color "#00857c" :font-size "16px"}
         :on-click #(state/go-to-application!)}
        "Open Business Account →"]]
      
      ;; Small Business Loan Card
      [:div.bg-white.rounded-xl.p-8.text-center
       {:style {:box-shadow "0 4px 20px rgba(0,0,0,0.08)"
                :border "1px solid #e5e5e5"}}
       [:div.mb-4 {:style {:font-size "48px"}} "💼"]
       [:h3.font-bold.mb-3 {:style {:color "#333" :font-size "22px"}}
        "Small Business Loans"]
       [:p.mb-6 {:style {:color "#666" :font-size "15px" :line-height "1.6"}}
        "Get the funding you need to start, grow, or expand your business with competitive rates."]
       [:ul.text-left.mb-6.space-y-2 {:style {:font-size "14px" :color "#555"}}
        [:li.flex.items-center.gap-2
         [:span {:style {:color "#00857c"}} "✓"] "Term Loans"]
        [:li.flex.items-center.gap-2
         [:span {:style {:color "#00857c"}} "✓"] "Lines of Credit"]
        [:li.flex.items-center.gap-2
         [:span {:style {:color "#00857c"}} "✓"] "SBA Loans"]
        [:li.flex.items-center.gap-2
         [:span {:style {:color "#00857c"}} "✓"] "Equipment Financing"]]
       [:button.w-full.font-bold.py-4.px-6.rounded-lg.transition-all
        {:style {:background-color "#fff" 
                 :color "#00857c" 
                 :font-size "16px"
                 :border "2px solid #00857c"}
         :on-click #(state/go-to-loan-application!)}
        "Apply for a Loan →"]]]
     
     ;; OAO & OLB Integration Section
     [:div.flex.items-center.gap-4.mt-12.mb-8
      [:div.flex-1.h-px {:style {:background-color "#ddd"}}]
      [:span.text-gray-400.text-sm.font-medium "OAO & ONLINE BANKING INTEGRATION"]
      [:div.flex-1.h-px {:style {:background-color "#ddd"}}]]
     
     [:div {:style {:display "grid"
                    :grid-template-columns "repeat(3, 1fr)"
                    :gap "1.5rem"
                    :max-width "1100px"
                    :margin "0 auto"}}
      
      ;; Q2-BUS-035: SSO from Online Banking
      [:div.bg-white.rounded-xl.p-6.text-center.flex.flex-col
       {:style {:box-shadow "0 4px 20px rgba(0,0,0,0.08)"
                :border "1px solid #e5e5e5"}}
       [:div.mb-4 {:style {:font-size "48px"}} "🔐"]
       [:div.mb-3
        [:span.px-2.py-1.rounded-full.text-xs.font-bold
         {:style {:background-color "#EFF6FF" :color "#1D4ED8"}} "Q2-BUS-035"]]
       [:h3.font-bold.mb-3 {:style {:color "#333" :font-size "18px"}}
        "Open Account via Online Banking SSO"]
       [:p.mb-4.flex-1 {:style {:color "#666" :font-size "14px" :line-height "1.6"}}
        "Already signed in to Online Banking? Open a new account with your information pre-filled — no re-entry required."]
       [:ul.text-left.mb-6.space-y-2 {:style {:font-size "13px" :color "#555"}}
        [:li.flex.items-center.gap-2
         [:span {:style {:color "#00857c"}} "✓"] "SSO from authenticated OB session"]
        [:li.flex.items-center.gap-2
         [:span {:style {:color "#00857c"}} "✓"] "JWT token with verified attributes"]
        [:li.flex.items-center.gap-2
         [:span {:style {:color "#00857c"}} "✓"] "Pre-filled application"]
        [:li.flex.items-center.gap-2
         [:span {:style {:color "#00857c"}} "✓"] "Account linked to OB profile"]]
       [:button.w-full.font-bold.py-3.px-6.rounded-lg.text-white.transition-all
        {:style {:background-color "#00857c" :font-size "15px"}
         :on-click #(state/go-to-oao-sso!)}
        "Open Account (SSO) →"]]
      
      ;; Q2-BUS-035A: New Customer + OLB Enrollment
      [:div.bg-white.rounded-xl.p-6.text-center.flex.flex-col
       {:style {:box-shadow "0 4px 20px rgba(0,0,0,0.08)"
                :border "1px solid #e5e5e5"}}
       [:div.mb-4 {:style {:font-size "48px"}} "🌐"]
       [:div.mb-3
        [:span.px-2.py-1.rounded-full.text-xs.font-bold
         {:style {:background-color "#F0FDF4" :color "#166534"}} "Q2-BUS-035A"]]
       [:h3.font-bold.mb-3 {:style {:color "#333" :font-size "18px"}}
        "Open Account + Enroll in Online Banking"]
       [:p.mb-4.flex-1 {:style {:color "#666" :font-size "14px" :line-height "1.6"}}
        "New to CSB? Open your business account and set up Online Banking access — all in one application."]
       [:ul.text-left.mb-6.space-y-2 {:style {:font-size "13px" :color "#555"}}
        [:li.flex.items-center.gap-2
         [:span {:style {:color "#00857c"}} "✓"] "Full account application"]
        [:li.flex.items-center.gap-2
         [:span {:style {:color "#00857c"}} "✓"] "Identity verification via Alloy"]
        [:li.flex.items-center.gap-2
         [:span {:style {:color "#00857c"}} "✓"] "OLB credential creation (Q2 Caliper)"]
        [:li.flex.items-center.gap-2
         [:span {:style {:color "#00857c"}} "✓"] "MFA setup included"]]
       [:button.w-full.font-bold.py-3.px-6.rounded-lg.text-white.transition-all
        {:style {:background-color "#00857c" :font-size "15px"}
         :on-click #(state/go-to-oao-new-enroll!)}
        "Open Account + Enroll →"]]
      
      ;; Q2-BUS-035B: Existing Customer via OB Credentials
      [:div.bg-white.rounded-xl.p-6.text-center.flex.flex-col
       {:style {:box-shadow "0 4px 20px rgba(0,0,0,0.08)"
                :border "1px solid #e5e5e5"}}
       [:div.mb-4 {:style {:font-size "48px"}} "🔑"]
       [:div.mb-3
        [:span.px-2.py-1.rounded-full.text-xs.font-bold
         {:style {:background-color "#FFF7ED" :color "#9A3412"}} "Q2-BUS-035B"]]
       [:h3.font-bold.mb-3 {:style {:color "#333" :font-size "18px"}}
        "Open Account with OB Credentials"]
       [:p.mb-4.flex-1 {:style {:color "#666" :font-size "14px" :line-height "1.6"}}
        "Already have Online Banking? Sign in from the public website to pre-fill your application. No SSO needed."]
       [:ul.text-left.mb-6.space-y-2 {:style {:font-size "13px" :color "#555"}}
        [:li.flex.items-center.gap-2
         [:span {:style {:color "#00857c"}} "✓"] "Login via OB credentials"]
        [:li.flex.items-center.gap-2
         [:span {:style {:color "#00857c"}} "✓"] "Q2 Caliper API authentication"]
        [:li.flex.items-center.gap-2
         [:span {:style {:color "#00857c"}} "✓"] "Pre-filled from identity token"]
        [:li.flex.items-center.gap-2
         [:span {:style {:color "#00857c"}} "✓"] "Account linked to existing profile"]]
       [:button.w-full.font-bold.py-3.px-6.rounded-lg.transition-all
        {:style {:background-color "#fff" 
                 :color "#00857c" 
                 :font-size "15px"
                 :border "2px solid #00857c"}
         :on-click #(state/go-to-oao-ob-login!)}
        "Sign In & Open Account →"]]]
     
     ;; Additional Info
     [:div.mt-12.text-center
      [:p {:style {:color "#666" :font-size "15px"}}
       "Questions? Call us at "
       [:a.font-bold {:href "tel:888-418-5626" :style {:color "#00857c"}} "888.418.5626"]
       " or visit any "
       [:a.font-bold {:href "https://www.cambridgesavings.com/locations" 
                      :style {:color "#00857c"}} "CSB branch"]
       "."]]]]
   
   ;; Footer
   [:footer.py-8 {:style {:background-color "#333"}}
    [:div.mx-auto.px-4.text-center {:style {:max-width "1200px"}}
     [:div {:style {:color "#ccc" :font-size "14px"}}
      [:p "Cambridge Savings Bank · 1374 Massachusetts Avenue · Cambridge, MA 02138"]
      [:p.mt-2
       [:a.hover:text-white {:href "tel:888-418-5626"} "888.418.5626"]
       " · "
       [:a.hover:text-white {:href "https://www.cambridgesavings.com"} "cambridgesavings.com"]]
      [:p.mt-4 {:style {:color "#999" :font-size "12px"}}
       "Member FDIC · NMLS ID# 543370 · Equal Housing Lender"
       [:br]
       "©2026 Cambridge Savings Bank. All Rights Reserved."]]]]])
