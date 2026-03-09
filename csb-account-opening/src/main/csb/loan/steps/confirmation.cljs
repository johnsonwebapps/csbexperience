(ns csb.loan.steps.confirmation
  (:require [csb.loan.state :as state]))

(defn generate-confirmation-number []
  (str "LOAN-" (.toUpperCase (.toString (random-uuid)))))

(def next-steps
  [{:step "1"
    :icon "📧"
    :title "Application Received"
    :desc "You'll receive a confirmation email with your application details."
    :time "Immediate"}
   {:step "2"
    :icon "🔍"
    :title "Credit & Background Review"
    :desc "Our team will review your application, pull credit reports, and verify business information."
    :time "1-2 business days"}
   {:step "3"
    :icon "📞"
    :title "Underwriting"
    :desc "A loan officer will analyze your financials and may contact you for additional documentation."
    :time "3-5 business days"}
   {:step "4"
    :icon "📋"
    :title "Loan Decision"
    :desc "You'll receive a decision with terms, or a request for additional information."
    :time "5-7 business days"}
   {:step "5"
    :icon "✍️"
    :title "Closing & Funding"
    :desc "Upon approval, you'll review and sign loan documents, then funds will be disbursed."
    :time "7-14 business days"}])

(defn confirmation-step [form-data]
  (let [confirmation-number (generate-confirmation-number)
        today (.toLocaleDateString (js/Date.)
                                   "en-US"
                                   #js {:weekday "long"
                                        :year "numeric"
                                        :month "long"
                                        :day "numeric"})]
    [:div.space-y-6
     ;; Success header
     [:div.card.text-center.py-8
      [:div.w-20.h-20.rounded-full.bg-green-100.flex.items-center.justify-center.mx-auto.mb-4
       [:svg.w-10.h-10.text-green-600 {:fill "none" :viewBox "0 0 24 24" :stroke "currentColor"}
        [:path {:stroke-linecap "round" :stroke-linejoin "round" :stroke-width 2.5
               :d "M5 13l4 4L19 7"}]]]
      [:h1.text-2xl.font-bold.text-gray-900.mb-2 "Loan Application Submitted!"]
      [:p.text-gray-600
       "Thank you, "
       [:strong (:primary-first-name form-data)]
       "! Your small business loan application has been received."]
      [:div.inline-block.mt-4.rounded-xl.px-6.py-3
       {:style {:background-color "#00857c" :color "white"}}
       [:div.text-xs.uppercase.tracking-widest.font-medium {:style {:color "rgba(255,255,255,0.7)"}} 
        "Confirmation Number"]
       [:div.text-xl.font-black.tracking-wider.mt-1 confirmation-number]]
      [:p.text-sm.text-gray-400.mt-3 today]]
     
     ;; Application summary
     [:div.card
      [:h2.font-bold.text-lg.mb-4.border-b.pb-3 {:style {:color "#00857c"}} "Application Summary"]
      [:div.space-y-3.text-sm
       [:div.flex.justify-between.py-1.5.border-b.border-gray-50
        [:span.text-gray-500 "Loan Type"]
        [:span.font-semibold (:loan-type form-data)]]
       [:div.flex.justify-between.py-1.5.border-b.border-gray-50
        [:span.text-gray-500 "Requested Amount"]
        [:span.font-semibold (str "$" (:loan-amount form-data))]]
       [:div.flex.justify-between.py-1.5.border-b.border-gray-50
        [:span.text-gray-500 "Business"]
        [:span.font-semibold (:business-legal-name form-data)]]
       [:div.flex.justify-between.py-1.5.border-b.border-gray-50
        [:span.text-gray-500 "Primary Applicant"]
        [:span.font-semibold (str (:primary-first-name form-data) " " (:primary-last-name form-data))]]
       [:div.flex.justify-between.py-1.5
        [:span.text-gray-500 "Confirmation emailed to"]
        [:span.font-semibold (:primary-email form-data)]]]]
     
     ;; What happens next
     [:div.card
      [:h2.font-bold.text-lg.mb-4.border-b.pb-3 {:style {:color "#00857c"}} "What Happens Next"]
      [:div.space-y-4
       (for [{:keys [step icon title desc time]} next-steps]
         ^{:key step}
         [:div.flex.gap-4
          [:div.w-10.h-10.rounded-full.flex.items-center.justify-center.text-xl.flex-shrink-0
           {:style {:background-color "rgba(0, 133, 124, 0.1)"}}
           icon]
          [:div.flex-1
           [:div.font-semibold.text-gray-800 title]
           [:div.text-sm.text-gray-500.mt-0.5 desc]
           [:div.text-xs.font-medium.mt-1 {:style {:color "#00857c"}} time]]])]]
     
     ;; nCino Integration Note
     [:div.rounded-lg.p-4 {:style {:background-color "rgba(0, 133, 124, 0.08)"
                                   :border "1px solid rgba(0, 133, 124, 0.2)"}}
      [:div.flex.gap-3
       [:div.text-2xl "🔒"]
       [:div
        [:h4.font-semibold.mb-1 {:style {:color "#00857c"}} "Secure Processing"]
        [:p.text-sm.text-gray-600
         "Your application is being processed through our secure nCino lending platform. "
         "All data is encrypted and handled in compliance with banking regulations. "
         "You may be asked to log into our secure portal for updates and document uploads."]]]]
     
     ;; Documents reminder
     [:div.card
      [:h2.font-bold.text-lg.mb-3.border-b.pb-3 {:style {:color "#00857c"}} "Documents to Have Ready"]
      [:ul.space-y-2.text-sm.text-gray-600
       [:li.flex.items-start.gap-2
        [:span.font-bold.flex-shrink-0 {:style {:color "#00857c"}} "•"]
        "Business and personal tax returns (last 3 years)"]
       [:li.flex.items-start.gap-2
        [:span.font-bold.flex-shrink-0 {:style {:color "#00857c"}} "•"]
        "Year-to-date financial statements"]
       [:li.flex.items-start.gap-2
        [:span.font-bold.flex-shrink-0 {:style {:color "#00857c"}} "•"]
        "Bank statements (last 6 months)"]
       [:li.flex.items-start.gap-2
        [:span.font-bold.flex-shrink-0 {:style {:color "#00857c"}} "•"]
        "Business formation documents"]
       [:li.flex.items-start.gap-2
        [:span.font-bold.flex-shrink-0 {:style {:color "#00857c"}} "•"]
        "Collateral documentation (if applicable)"]]
      [:p.text-xs.text-gray-400.mt-3
       "Our team will reach out if additional documents are needed."]]
     
     ;; Contact CTA
     [:div.rounded-xl.overflow-hidden
      {:style {:background "linear-gradient(135deg, #00857c 0%, #006b64 100%)"}}
      [:div.p-6.text-white.text-center
       [:h3.font-bold.text-xl.mb-2 "Questions About Your Application?"]
       [:p.text-sm.mb-4 {:style {:color "rgba(255,255,255,0.85)"}}
        "Our business banking team is here to help every step of the way."]
       [:div.flex.flex-wrap.justify-center.gap-3
        [:a.bg-white.font-bold.px-5.py-2.5.rounded-lg.text-sm.transition-colors
         {:href "tel:1-888-418-5626"
          :style {:color "#00857c"}}
         "📞 1-888-418-5626"]
        [:a.bg-transparent.border-2.border-white.text-white.font-bold.px-5.py-2.5.rounded-lg.text-sm.transition-colors
         {:href "mailto:businessbanking@cambridgesavings.com"}
         "✉️ Email Us"]]]]
     
     ;; Start over
     [:div.text-center
      [:button.font-semibold.text-sm.hover:underline
       {:style {:color "#00857c"}
        :on-click state/start-over!}
       "Start a new application"]]]))
