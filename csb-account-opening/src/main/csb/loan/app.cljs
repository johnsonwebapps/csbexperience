(ns csb.loan.app
  (:require [reagent.core :as r]
            [csb.state :as main-state]
            [csb.loan.state :as state]
            [csb.loan.steps.loan-request :refer [loan-request-step]]
            [csb.loan.steps.business-info :refer [business-info-step]]
            [csb.loan.steps.financial-info :refer [financial-info-step]]
            [csb.loan.steps.guarantor-info :refer [guarantor-info-step]]
            [csb.loan.steps.collateral :refer [collateral-step]]
            [csb.loan.steps.documents :refer [documents-step]]
            [csb.loan.steps.review :refer [review-step]]
            [csb.loan.steps.confirmation :refer [confirmation-step]]))

(defn progress-sidebar []
  (let [current-step (:current-step @state/loan-state)
        steps state/steps]
    [:div.w-72.flex-shrink-0
     [:div {:style {:background-color "#00857c"}
            :class "rounded-xl p-6 sticky top-6"}
      [:h2.text-white.font-bold.text-lg.mb-6 "Application Progress"]
      [:ol.space-y-1
       (map-indexed
        (fn [idx step]
          (let [step-num (inc idx)
                is-current (= current-step step-num)
                is-completed (< step-num current-step)]
            ^{:key idx}
            [:li.flex.items-center.gap-3.py-2.px-2.rounded-lg.cursor-pointer.transition-colors
             {:class (when is-current "bg-white bg-opacity-20")
              :on-click #(when is-completed (state/go-to-step! step-num))}
             [:div.w-7.h-7.rounded-full.flex.items-center.justify-center.text-sm.font-bold.flex-shrink-0
              {:class (cond
                        is-completed "bg-white"
                        is-current "bg-white"
                        :else "bg-white bg-opacity-30")
               :style {:color (if (or is-completed is-current) "#00857c" "#fff")}}
              (if is-completed "✓" step-num)]
             [:span.text-sm
              {:style {:color (cond
                                is-current "white"
                                is-completed "rgba(255,255,255,0.9)"
                                :else "rgba(255,255,255,0.6)")
                       :font-weight (if (or is-current is-completed) "600" "400")}}
              (:label step)]]))
        steps)]
      [:div.mt-6.pt-4.text-xs
       {:style {:border-top "1px solid rgba(255,255,255,0.2)" :color "rgba(255,255,255,0.7)"}}
       [:p "Your progress is automatically saved."
        [:br]
        "Click any completed step to review."]]]]))

(defn render-current-step []
  (let [current-step (:current-step @state/loan-state)
        submitted (:submitted @state/loan-state)
        form-data (:form-data @state/loan-state)]
    (if submitted
      [confirmation-step form-data]
      (case current-step
        1 [loan-request-step form-data]
        2 [business-info-step form-data]
        3 [financial-info-step form-data]
        4 [guarantor-info-step form-data]
        5 [collateral-step form-data]
        6 [documents-step form-data]
        7 [review-step form-data]
        [loan-request-step form-data]))))

(defn go-to-landing! []
  (state/start-over!)
  (main-state/go-to-landing!))

(defn loan-app []
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
     (when (not (:submitted @state/loan-state))
       [progress-sidebar])
     
     ;; Form content
     [:div.flex-1 {:style {:max-width (if (:submitted @state/loan-state) "700px" "none")
                           :margin (when (:submitted @state/loan-state) "0 auto")}}
      ;; Step header
      (when (not (:submitted @state/loan-state))
        (let [current-step (:current-step @state/loan-state)
              step-info (nth state/steps (dec current-step))]
          [:div.mb-6
           [:div.text-sm.font-medium.mb-1 {:style {:color "#00857c"}}
            (str "Step " current-step " of " (count state/steps))]
           [:h1.text-2xl.font-bold.text-gray-900 (:label step-info)]
           [:p.text-gray-600.mt-1 (:short step-info)]]))
      
      ;; Current step content
      [render-current-step]]]]
   
   ;; Footer
   [:footer.py-4.text-center.text-sm.text-gray-500
    {:style {:background-color "#f0f5f4"
             :border-top "1px solid #e5e7eb"}}
    [:div.container.mx-auto.px-4
     [:p "© 2024 Cambridge Savings Bank. Member FDIC. Equal Housing Lender."]
     [:p.mt-1 "Small Business Loan Application powered by nCino."]]]])
