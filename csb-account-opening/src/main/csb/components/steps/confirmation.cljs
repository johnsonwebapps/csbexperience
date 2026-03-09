(ns csb.components.steps.confirmation
  (:require [csb.state :as state]
            [csb.utils :as utils]))

(def next-steps
  [{:step "1"
    :icon "📧"
    :title "Confirmation Email"
    :desc-fn #(str "A confirmation has been sent to " % ". Keep this for your records.")
    :time "Right now"}
   {:step "2"
    :icon "🔍"
    :title "Application Review"
    :desc-fn (constantly "Our team will review your application and verify the information provided.")
    :time "1–2 business days"}
   {:step "3"
    :icon "📞"
    :title "We'll Be in Touch"
    :desc-fn (constantly "A CSB business banking specialist may contact you if additional documentation is needed.")
    :time "1–3 business days"}
   {:step "4"
    :icon "🏦"
    :title "Account Open"
    :desc-fn (constantly "Once approved, your account will be opened and you'll receive your account details and debit card (if applicable).")
    :time "3–5 business days"}])

(def documents-needed
  ["Articles of Incorporation / Organization (or equivalent formation documents)"
   "Operating Agreement or Bylaws"
   "Government-issued photo ID for all beneficial owners"
   "Certificate of Good Standing (for established businesses)"
   "Business license (if applicable to your industry)"])

(defn confirmation-step [form-data]
  (let [confirmation-number (utils/generate-confirmation-number)
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
      [:h1.text-2xl.font-bold.text-gray-900.mb-2 "Application Submitted!"]
      [:p.text-gray-600
       "Thank you, "
       [:strong (:first-name form-data)]
       "! Your business account application has been received."]
      [:div.inline-block.mt-4.bg-csb-teal.text-white.rounded-xl.px-6.py-3
       [:div.text-xs.uppercase.tracking-widest.font-medium {:style {:color "rgba(255,255,255,0.7)"}} "Confirmation Number"]
       [:div.text-2xl.font-black.tracking-wider.mt-1 confirmation-number]]
      [:p.text-sm.text-gray-400.mt-3 today]]

     ;; Application summary
     [:div.card
      [:h2.font-bold.text-csb-teal.text-lg.mb-4.border-b.pb-3 "Application Summary"]
      [:div.space-y-3.text-sm
       [:div.flex.justify-between.py-1.5.border-b.border-gray-50
        [:span.text-gray-500 "Business"]
        [:span.font-semibold (:business-name form-data)]]
       [:div.flex.justify-between.py-1.5.border-b.border-gray-50
        [:span.text-gray-500 "Account Type"]
        [:span.font-semibold (get utils/products-map (:selected-product form-data))]]
       [:div.flex.justify-between.py-1.5.border-b.border-gray-50
        [:span.text-gray-500 "Primary Applicant"]
        [:span.font-semibold (str (:first-name form-data) " " (:last-name form-data))]]
       [:div.flex.justify-between.py-1.5
        [:span.text-gray-500 "Confirmation emailed to"]
        [:span.font-semibold (:email form-data)]]]]

     ;; What happens next
     [:div.card
      [:h2.font-bold.text-csb-teal.text-lg.mb-4.border-b.pb-3 "What Happens Next"]
      [:div.space-y-4
       (for [{:keys [step icon title desc-fn time]} next-steps]
         ^{:key step}
         [:div.flex.gap-4
          [:div.w-10.h-10.rounded-full.flex.items-center.justify-center.text-xl.flex-shrink-0
           {:style {:background-color "rgba(0, 133, 124, 0.1)"}}
           icon]
          [:div.flex-1
           [:div.font-semibold.text-gray-800 title]
           [:div.text-sm.text-gray-500.mt-0.5 (desc-fn (:email form-data))]
           [:div.text-xs.text-csb-teal.font-medium.mt-1 time]]])]]

     ;; Documents needed
     [:div.card
      [:h2.font-bold.text-csb-teal.text-lg.mb-3.border-b.pb-3 "Documents You May Need to Provide"]
      [:ul.space-y-2.text-sm.text-gray-600
       (for [doc documents-needed]
         ^{:key doc}
         [:li.flex.items-start.gap-2
          [:span.text-csb-teal.font-bold.flex-shrink-0 "•"]
          doc])]
      [:p.text-xs.text-gray-400.mt-3
       "We may request these by secure email or you can bring them to any CSB branch location."]]

     ;; Visit a branch CTA
     [:div.rounded-xl.overflow-hidden
      {:style {:background "linear-gradient(135deg, #00857c 0%, #006b64 100%)"}}
      [:div.p-6.text-white.text-center
       [:h3.font-bold.text-xl.mb-2 "Have Questions?"]
       [:p.text-sm.mb-4 {:style {:color "rgba(255,255,255,0.85)"}}
        "Our business banking team is here to help every step of the way."]
       [:div.flex.flex-wrap.justify-center.gap-3
        [:a.bg-white.font-bold.px-5.py-2.5.rounded-lg.text-sm.transition-colors
         {:href "tel:1-888-418-5626"
          :style {:color "#00857c"}}
         "📞 1-888-418-5626"]
        [:a.bg-transparent.border-2.border-white.text-white.font-bold.px-5.py-2.5.rounded-lg.text-sm.hover:bg-white.hover:bg-opacity-10.transition-colors
         {:href "#"}
         "Find a Branch"]]]]

     ;; Start over
     [:div.text-center
      [:button.text-csb-teal.font-semibold.text-sm.hover:underline
       {:on-click state/start-over!}
       "Start a new application"]]]))
