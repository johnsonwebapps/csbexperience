(ns csb.unified.app
  (:require [reagent.core :as r]
            [csb.state :as main-state]
            [csb.unified.state :as state]
            [csb.unified.steps.intent :refer [intent-step]]
            [csb.unified.steps.business-info :refer [business-info-step]]
            [csb.unified.steps.owner-info :refer [owner-info-step]]
            [csb.unified.steps.loan-request :refer [loan-request-step]]
            [csb.unified.steps.financials :refer [financials-step]]
            [csb.unified.steps.documents :refer [documents-step]]
            [csb.unified.steps.loan-decision :refer [loan-decision-step]]
            [csb.unified.steps.account-selection :refer [account-selection-step]]
            [csb.unified.steps.review :refer [review-step]]
            [csb.unified.steps.confirmation :refer [confirmation-step]]))

(defn get-visible-steps [flow-type loan-decision continue-with-account-after-denial]
  (cond
    (nil? flow-type)
    [{:id :intent :label "Get Started" :number 1}]
    
    (= flow-type :account-only)
    [{:id :intent :label "Get Started" :number 1}
     {:id :business-info :label "Business Information" :number 2}
     {:id :owner-info :label "Owner Information" :number 3}
     {:id :account-selection :label "Select Accounts" :number 4}
     {:id :review :label "Review & Submit" :number 5}]
    
    ;; Loan denied but user wants to continue with account
    (and (= loan-decision :denied) continue-with-account-after-denial)
    [{:id :intent :label "Get Started" :number 1}
     {:id :business-info :label "Business Information" :number 2}
     {:id :owner-info :label "Owner Information" :number 3}
     {:id :loan-request :label "Loan Request" :number 4}
     {:id :financials :label "Financial Details" :number 5}
     {:id :documents :label "Documents" :number 6}
     {:id :loan-decision :label "Loan Decision" :number 7}
     {:id :account-selection :label "Select Accounts" :number 8}
     {:id :review :label "Review & Submit" :number 9}]
    
    ;; Loan denied and user does NOT want to continue with account
    (= loan-decision :denied)
    [{:id :intent :label "Get Started" :number 1}
     {:id :business-info :label "Business Information" :number 2}
     {:id :owner-info :label "Owner Information" :number 3}
     {:id :loan-request :label "Loan Request" :number 4}
     {:id :financials :label "Financial Details" :number 5}
     {:id :documents :label "Documents" :number 6}
     {:id :loan-decision :label "Loan Decision" :number 7}
     {:id :review :label "Review & Submit" :number 8}]
    
    :else ; loan flow (pending or approved)
    [{:id :intent :label "Get Started" :number 1}
     {:id :business-info :label "Business Information" :number 2}
     {:id :owner-info :label "Owner Information" :number 3}
     {:id :loan-request :label "Loan Request" :number 4}
     {:id :financials :label "Financial Details" :number 5}
     {:id :documents :label "Documents" :number 6}
     {:id :loan-decision :label "Loan Decision" :number 7}
     {:id :account-selection :label "Select Accounts" :number 8}
     {:id :review :label "Review & Submit" :number 9}]))

(defn get-step-index [step-id steps]
  (->> steps
       (map-indexed (fn [idx s] [idx s]))
       (filter #(= step-id (:id (second %))))
       first
       first))

(defn progress-sidebar []
  (let [{:keys [current-step form-data]} @state/app-state
        flow-type (:flow-type form-data)
        loan-decision (:loan-decision form-data)
        continue-with-account? (:continue-with-account-after-denial form-data)
        steps (get-visible-steps flow-type loan-decision continue-with-account?)
        current-idx (get-step-index current-step steps)]
    [:div.w-72.flex-shrink-0
     [:div {:style {:background-color "#00857c"}
            :class "rounded p-6 sticky top-6"}
      [:h2.text-white.font-bold.uppercase.tracking-wide.mb-6 
       {:style {:letter-spacing "1px"}}
       "Application Progress"]
      [:div.space-y-1
       (map-indexed
        (fn [idx step]
          (let [is-current (= current-step (:id step))
                is-completed (and current-idx (< idx current-idx))]
            ^{:key (:id step)}
            [:div.flex.items-center.gap-3.py-2.px-2.rounded.transition-colors
             {:class (when is-current "bg-white bg-opacity-20")
              :style {:cursor (if is-completed "pointer" "default")}
              :on-click #(when is-completed (state/go-to-step! (:id step)))}
             [:div.w-7.h-7.rounded-full.flex.items-center.justify-center.text-sm.font-bold.flex-shrink-0
              {:class (cond
                        is-completed "bg-white"
                        is-current "bg-white"
                        :else "bg-white bg-opacity-30")
               :style {:color (if (or is-completed is-current) "#00857c" "#fff")}}
              (if is-completed "✓" (inc idx))]
             [:span.text-sm.uppercase
              {:style {:color (cond
                                is-current "white"
                                is-completed "rgba(255,255,255,0.9)"
                                :else "rgba(255,255,255,0.6)")
                       :font-weight (if (or is-current is-completed) "600" "400")
                       :letter-spacing "0.5px"
                       :font-size "0.8125rem"}}
              (:label step)]]))
        steps)]
      
      ;; Flow type indicator
      (when flow-type
        [:div.mt-6.pt-4.text-xs.uppercase
         {:style {:border-top "1px solid rgba(255,255,255,0.2)" 
                  :color "rgba(255,255,255,0.7)"
                  :letter-spacing "0.5px"}}
         [:p.font-semibold.mb-1 "Application Type:"]
         [:p (case flow-type
               :account-only "Business Account"

               :loan-and-account "Business Loan (Requires Account)"
               "")]])]]))

(defn render-current-step []
  (let [{:keys [current-step submitted]} @state/app-state]
    (if submitted
      [confirmation-step]
      (case current-step
        :intent [intent-step]
        :business-info [business-info-step]
        :owner-info [owner-info-step]
        :loan-request [loan-request-step]
        :financials [financials-step]
        :documents [documents-step]
        :loan-decision [loan-decision-step]
        :account-selection [account-selection-step]
        :review [review-step]
        [intent-step]))))

(defn go-to-landing! []
  (state/start-over!)
  (main-state/go-to-landing!))

(defn save-draft-button []
  (let [saving? (r/atom false)
        saved? (r/atom false)]
    (fn []
      [:button.px-4.py-2.text-sm.font-medium.rounded-lg.transition-colors.flex.items-center.gap-2
       {:style {:background-color (if @saved? "#e6f4f2" "#f3f4f6")
                :color (if @saved? "#00857c" "#374151")
                :border "1px solid" 
                :border-color (if @saved? "#00857c" "#d1d5db")}
        :on-click (fn []
                    (reset! saving? true)
                    (state/save-draft!)
                    (js/setTimeout
                     (fn []
                       (reset! saving? false)
                       (reset! saved? true)
                       (js/setTimeout #(reset! saved? false) 2000))
                     500))}
       (cond
         @saving? [:span "Saving..."]
         @saved? [:span "✓ Draft Saved"]
         :else [:span "💾 Save Draft"])])))

(defn unified-app []
  (let [{:keys [current-step submitted form-data]} @state/app-state
        flow-type (:flow-type form-data)
        loan-decision (:loan-decision form-data)
        continue-with-account? (:continue-with-account-after-denial form-data)
        sso-authenticated? (:sso-authenticated form-data)
        steps (get-visible-steps flow-type loan-decision continue-with-account?)
        current-idx (get-step-index current-step steps)
        step-info (when current-idx (nth steps current-idx nil))]
    [:div.min-h-screen.flex.flex-col {:style {:background-color "#f0f5f4"}}
     ;; Header
     [:header {:style {:background-color "#00857c" :padding "0.75rem 0"}}
      [:div.container.mx-auto.px-4.flex.items-center.justify-between
       [:div.flex.items-center.gap-2.cursor-pointer
        {:on-click go-to-landing!}
        [:img {:src "/images/header.png" 
               :alt "Cambridge Savings Bank"
               :style {:max-height "680px" :width "1280px"}}]]
       [:div.text-white.text-sm.flex.items-center.gap-4
        (when sso-authenticated?
          [:span.px-3.py-1.rounded-full.text-xs.font-medium
           {:style {:background-color "rgba(255,255,255,0.2)"}}
           "✓ Signed In"])
        [:span "Need Help?"]
        [:a.font-medium.hover:underline {:href "tel:1-888-418-5626"} "1-888-418-5626"]]]]
     
     ;; SSO Welcome Banner
     (when (and sso-authenticated? (not submitted) (= current-step :intent))
       [:div {:style {:background-color "#e6f4f2" :border-bottom "1px solid #00857c"}}
        [:div.container.mx-auto.px-4.py-4
         [:div.flex.items-center.gap-4
          [:div.w-12.h-12.rounded-full.flex.items-center.justify-center
           {:style {:background-color "#00857c"}}
           [:span.text-white.text-xl "👤"]]
          [:div.flex-1
           [:h3.font-semibold.text-gray-900 
            (str "Welcome back, " (:owner-first-name form-data) "!")]
           [:p.text-sm.text-gray-600 
            "You're signed in via Online Banking. Your information has been pre-filled from your existing profile."]]
          [:div.text-right.text-sm
           [:div.font-medium {:style {:color "#00857c"}} (:business-legal-name form-data)]
           [:div.text-gray-500 (str "Session: " (subs (str (:sso-session-id form-data)) 0 12) "...")]]]]])
     
     ;; Action bar with Save Draft and View Applications
     (when (and (not submitted) (not= current-step :intent))
       [:div.bg-white.border-b.border-gray-200
        [:div.container.mx-auto.px-4.py-3.flex.items-center.justify-between
         [:div.flex.items-center.gap-4
          [save-draft-button]
          [:button.px-4.py-2.text-sm.font-medium.text-gray-600.hover:text-gray-900.flex.items-center.gap-2
           {:on-click main-state/go-to-dashboard!}
           [:span "📋 My Applications"]]]
         [:div.text-sm.text-gray-500
          (cond
            sso-authenticated?
            [:span.flex.items-center.gap-2
             [:span.text-green-600 "●"]
             "Online Banking Session Active"]
            (:app-id @state/app-state)
            (str "Application ID: " (subs (str (:app-id @state/app-state)) 0 8) "...")
            :else nil)]]])
     
     ;; Main content
     [:main#main-content.flex-1.container.mx-auto.px-4.py-8
      [:div {:style {:display "flex" :gap "2rem" :align-items "flex-start"}}
       ;; Sidebar
       (when-not submitted
         [progress-sidebar])
       
       ;; Form content
       [:div.flex-1 {:style {:max-width (if submitted "700px" "none")
                             :margin (when submitted "0 auto")}}
        ;; Step header
        (when (and (not submitted) step-info (not= current-step :intent))
          [:div.mb-6
           [:div.text-sm.font-medium.mb-1 {:style {:color "#00857c"}}
            (str "Step " (inc current-idx) " of " (count steps))]
           [:h1.text-2xl.font-bold.text-gray-900 (:label step-info)]])
        
        ;; Current step content
        [render-current-step]]]]
     
     ;; Footer
     [:footer.py-4.text-center.text-sm.text-gray-500
      {:style {:background-color "#f0f5f4"
               :border-top "1px solid #e5e7eb"}}
      [:div.container.mx-auto.px-4
       [:p "© 2024 Cambridge Savings Bank. Member FDIC. Equal Housing Lender."]
       [:p.mt-1 "Business Banking Application"]]]]))
