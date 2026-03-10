(ns csb.oao.app
  (:require [reagent.core :as r]
            [csb.state :as main-state]
            [csb.oao.state :as state]
            [csb.oao.steps.sso-auth :refer [sso-auth-step]]
            [csb.oao.steps.ob-auth :refer [ob-auth-step]]
            [csb.oao.steps.get-started :refer [get-started-step]]
            [csb.oao.steps.review-info :refer [review-info-step]]
            [csb.oao.steps.business-info :refer [business-info-step]]
            [csb.oao.steps.applicant-info :refer [applicant-info-step]]
            [csb.oao.steps.select-account :refer [select-account-step]]
            [csb.oao.steps.account-services :refer [account-services-step]]
            [csb.oao.steps.initial-deposit :refer [initial-deposit-step]]
            [csb.oao.steps.review-submit :refer [review-submit-step]]
            [csb.oao.steps.olb-enrollment :refer [olb-enrollment-step]]
            [csb.oao.steps.confirmation :refer [confirmation-step]]))

(defn progress-sidebar []
  (let [{:keys [current-step form-data]} @state/app-state
        entry-type (:entry-type form-data)
        steps (state/get-steps entry-type)
        current-idx (state/get-step-index current-step steps)]
    [:div.w-72.flex-shrink-0
     [:div {:style {:background-color "#00857c"}
            :class "rounded-xl p-6 sticky top-6"}
      [:h2.text-white.font-bold.text-lg.mb-6 "Application Progress"]
      [:ol.space-y-1
       (map-indexed
        (fn [idx step]
          (let [is-current (= current-step (:id step))
                is-completed (and current-idx (< idx current-idx))]
            ^{:key (:id step)}
            [:li.flex.items-center.gap-3.py-2.px-2.rounded-lg.transition-colors
             {:class (when is-current "bg-white bg-opacity-20")
              :style {:cursor (if is-completed "pointer" "default")}
              :on-click #(when is-completed (state/go-to-step! (:id step)))}
             [:div.w-7.h-7.rounded-full.flex.items-center.justify-center.text-sm.font-bold.flex-shrink-0
              {:class (cond
                        is-completed "bg-white"
                        is-current "bg-white"
                        :else "bg-white bg-opacity-30")
               :style {:color (if (or is-completed is-current) "#00857c" "#fff")}}
              (if is-completed "✓" (:number step))]
             [:span.text-sm
              {:style {:color (cond
                                is-current "white"
                                is-completed "rgba(255,255,255,0.9)"
                                :else "rgba(255,255,255,0.6)")
                       :font-weight (if (or is-current is-completed) "600" "400")}}
              (:label step)]]))
        steps)]
      
      ;; Flow type indicator
      [:div.mt-6.pt-4.text-xs
       {:style {:border-top "1px solid rgba(255,255,255,0.2)" :color "rgba(255,255,255,0.7)"}}
       [:p.font-medium.mb-1 "Flow Type:"]
       [:p (case entry-type
             :sso "SSO from Online Banking"
             :new-enroll "New Customer + OLB Enrollment"
             :ob-login "Existing Customer (OB Login)"
             "")]]]]))

(defn render-current-step []
  (let [{:keys [current-step submitted]} @state/app-state]
    (if submitted
      [confirmation-step]
      (case current-step
        :sso-auth [sso-auth-step]
        :ob-auth [ob-auth-step]
        :get-started [get-started-step]
        :review-info [review-info-step]
        :business-info [business-info-step]
        :applicant-info [applicant-info-step]
        :select-account [select-account-step]
        :account-services [account-services-step]
        :initial-deposit [initial-deposit-step]
        :review-submit [review-submit-step]
        :olb-enrollment [olb-enrollment-step]
        [:div.p-8.text-center "Loading..."]))))

(defn go-to-landing! []
  (state/start-over!)
  (main-state/go-to-landing!))

(defn oao-app []
  (let [{:keys [current-step submitted form-data]} @state/app-state
        entry-type (:entry-type form-data)
        steps (state/get-steps entry-type)
        current-idx (state/get-step-index current-step steps)
        step-info (when current-idx (nth steps current-idx nil))]
    [:div.min-h-screen.flex.flex-col {:style {:background-color "#f0f5f4"}}
     ;; Header
     [:header {:style {:background-color "#00857c" :padding "0.75rem 0"}}
      [:div.container.mx-auto.px-4.flex.items-center.justify-between
       [:div.flex.items-center.gap-2.cursor-pointer
        {:on-click go-to-landing!}
        [:img {:src "/images/header.png"
               :alt "Cambridge Savings Bank"
               :style {:height "1280px"}}]]
       [:div.text-white.text-sm.flex.items-center.gap-4
        [:span "Need Help?"]
        [:a.font-medium.hover:underline {:href "tel:1-888-418-5626"} "1-888-418-5626"]]]]
     
     ;; Main content
     [:main.flex-1.container.mx-auto.px-4.py-8
      [:div {:style {:display "flex" :gap "2rem" :align-items "flex-start"}}
       ;; Sidebar
       (when-not submitted
         [progress-sidebar])
       
       ;; Form content
       [:div.flex-1 {:style {:max-width (if submitted "700px" "none")
                             :margin (when submitted "0 auto")}}
        ;; Step header
        (when (and (not submitted) step-info
                   (not (#{:sso-auth :ob-auth :get-started} current-step)))
          [:div.mb-6
           [:div.text-sm.font-medium.mb-1 {:style {:color "#00857c"}}
            (str "Step " (:number step-info) " of " (count steps))]
           [:h1.text-2xl.font-bold.text-gray-900 (:label step-info)]])
        
        ;; Current step content
        [render-current-step]]]]
     
     ;; Footer
     [:footer.py-4.text-center.text-sm.text-gray-500
      {:style {:background-color "#f0f5f4"
               :border-top "1px solid #e5e7eb"}}
      [:div.container.mx-auto.px-4
       [:p "© 2026 Cambridge Savings Bank. Member FDIC. Equal Housing Lender."]
       [:p.mt-1 "Online Account Opening powered by Terafina."]]]]))
