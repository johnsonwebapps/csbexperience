(ns csb.components.app
  (:require [csb.state :as state]
            [csb.components.ui.header :refer [header]]
            [csb.components.ui.progress-sidebar :refer [progress-sidebar]]
            [csb.components.landing :refer [landing-page]]
            [csb.components.steps.welcome :refer [welcome-step]]
            [csb.components.steps.select-product :refer [select-product-step]]
            [csb.components.steps.business-info :refer [business-info-step]]
            [csb.components.steps.applicant-info :refer [applicant-info-step]]
            [csb.components.steps.beneficial-owners :refer [beneficial-owners-step]]
            [csb.components.steps.review :refer [review-step]]
            [csb.components.steps.confirmation :refer [confirmation-step]]
            [csb.loan.app :as loan-app]
            [csb.unified.app :as unified-app]
            [csb.unified.dashboard :as unified-dashboard]
            [csb.oao.app :as oao-app]
            [csb.oao.state :as oao-state]))

(defn mobile-step-indicator [current-step steps]
  [:div.lg:hidden.mb-4
   [:div.card.py-3.px-4
    [:div.flex.items-center.justify-between.text-sm
     [:span.font-semibold.text-csb-teal
      (str "Step " current-step " of " (count steps))]
     [:span.text-gray-500
      (:label (nth steps (dec current-step)))]]
    [:div.mt-2.h-1.5.bg-gray-200.rounded-full.overflow-hidden
     [:div.h-full.bg-csb-teal.rounded-full.transition-all.duration-300
      {:style {:width (str (* (/ current-step (count steps)) 100) "%")}}]]]])

(defn footer []
  [:footer.mt-12.py-8 {:style {:background-color "#333333"}}
   [:div.max-w-6xl.mx-auto.px-4.text-center
    [:div.text-sm.space-y-2 {:style {:color "#ccc"}}
     [:p "Cambridge Savings Bank · 1374 Massachusetts Avenue · Cambridge, MA 02138"]
     [:p 
      [:a.hover:text-white {:href "tel:888-418-5626"} "888.418.5626"]
      " · "
      [:a.hover:text-white {:href "https://www.cambridgesavings.com"} "cambridgesavings.com"]]
     [:p.text-xs.mt-4 {:style {:color "#999"}}
      "Member FDIC · NMLS ID# 543370 · Equal Housing Lender"
      [:br]
      "©2026 Cambridge Savings Bank. All Rights Reserved."]]]])

(defn application-page []
  (let [{:keys [current-step form-data submitted]} @state/app-state
        steps state/steps]
    (if submitted
      [:div.min-h-screen.flex.flex-col {:style {:background-color "#f5f5f5"}}
       [header]
       [:div.flex-1
        [:div.mx-auto.px-4.py-8 {:style {:max-width "1200px"}}
         [:div {:style {:display "flex" :gap "2rem"}}
          ;; Sidebar - always visible on left
          [:aside {:style {:width "280px" :flex-shrink "0"}}
           [progress-sidebar steps current-step]]
          ;; Main Content on right
          [:main {:style {:flex "1" :min-width "0"}}
           [confirmation-step form-data]]]]]
       [footer]]
      
      [:div.min-h-screen.flex.flex-col {:style {:background-color "#f5f5f5"}}
       [header]
       [:div.flex-1
        [:div.mx-auto.px-4.py-8 {:style {:max-width "1200px"}}
         [:div {:style {:display "flex" :gap "2rem"}}
          ;; Sidebar - always visible on left
          [:aside {:style {:width "280px" :flex-shrink "0"}}
           [progress-sidebar steps current-step]]
          
          ;; Main Content on right
          [:main {:style {:flex "1" :min-width "0"}}
           (case current-step
             1 [welcome-step form-data]
             2 [select-product-step form-data]
             3 [business-info-step form-data]
             4 [applicant-info-step form-data]
             5 [beneficial-owners-step form-data]
             6 [review-step form-data]
             nil)]]]]
       [footer]])))

(defn app []
  (let [{:keys [page]} @state/app-state]
    (case page
      :landing [landing-page]
      :application [application-page]
      :loan-application [loan-app/loan-app]
      :unified-application [unified-app/unified-app]
      :dashboard [unified-dashboard/dashboard]
      :oao-sso (do (when-not (:entry-type (:form-data @oao-state/app-state))
                     (oao-state/init-sso-flow!))
                   [oao-app/oao-app])
      :oao-new-enroll (do (when-not (:entry-type (:form-data @oao-state/app-state))
                            (oao-state/init-new-enroll-flow!))
                          [oao-app/oao-app])
      :oao-ob-login (do (when-not (:entry-type (:form-data @oao-state/app-state))
                          (oao-state/init-ob-login-flow!))
                        [oao-app/oao-app])
      [landing-page])))
