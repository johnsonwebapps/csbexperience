(ns csb.components.steps.welcome
  (:require [reagent.core :as r]
            [csb.state :as state]))

(def purpose-options
  [{:id "new-business"
    :icon "🏢"
    :title "New Business"
    :desc "Opening accounts for a newly formed business entity"}
   {:id "existing-business"
    :icon "📈"
    :title "Existing Business"
    :desc "Adding or replacing accounts for an established business"}
   {:id "sole-proprietor"
    :icon "👤"
    :title "Sole Proprietor"
    :desc "Self-employed individual or single-member LLC without employees"}])

(def before-you-begin-items
  [["📄" "Business formation documents (Articles of Incorporation, LLC Agreement, etc.)"]
   ["🔢" "Employer Identification Number (EIN) or Tax ID"]
   ["🪪" "Government-issued photo ID for all owners"]
   ["📊" "SSN/ITIN for owners with 25% or more ownership"]
   ["🏠" "Business and personal addresses"]
   ["💳" "Initial deposit information (if funding at opening)"]])

(defn welcome-step [form-data]
  (let [error (r/atom "")]
    (fn [form-data]
      (let [selected (:account-purpose form-data)]
        [:div.space-y-6
         ;; Hero banner
         [:div.rounded-lg.overflow-hidden
          {:style {:background "linear-gradient(135deg, #00857c 0%, #006b64 100%)"}}
          [:div.p-8.text-white
           [:h1.text-3xl.font-bold.mb-3.leading-tight.uppercase.tracking-wide
            "Get Started"]
           [:p.text-lg.max-w-xl {:style {:color "rgba(255,255,255,0.9)"}}
            "Get started in minutes. Our secure online application makes it easy to open the right account for your business."]
           [:div.flex.flex-wrap.gap-6.mt-6.text-sm
            (for [item ["FDIC Insured" "No hidden fees" "Local, community bank" "15–20 minutes to complete"]]
              ^{:key item}
              [:div.flex.items-center.gap-2
               [:span {:style {:color "#fff"}} "✓"]
               [:span item]])]]]

         ;; What you'll need
         [:div.card
          [:h2.text-lg.font-bold.mb-3 {:style {:color "#333"}} "Before You Begin"]
          [:p.text-gray-600.text-sm.mb-4
           "Please have the following information ready to complete your application:"]
          [:div.grid.sm:grid-cols-2.gap-3.text-sm
           (for [[icon text] before-you-begin-items]
             ^{:key text}
             [:div.flex.items-start.gap-2.bg-gray-50.rounded-lg.p-3
              [:span.text-lg.flex-shrink-0 icon]
              [:span.text-gray-700 text]])]]

         ;; Purpose selection
         [:div.card
          [:h2.section-header "What brings you here today?"]
          [:p.text-gray-500.text-sm.mb-5
           "Tell us a little about your situation so we can guide you through the right process."]
          [:div.space-y-3
           (for [opt purpose-options]
             ^{:key (:id opt)}
             [:div {:class (str "product-card flex items-start gap-4 "
                               (when (= selected (:id opt)) "selected"))
                    :role "radio"
                    :aria-checked (= selected (:id opt))
                    :tab-index 0
                    :on-click #(do
                                (state/update-form-data! {:account-purpose (:id opt)})
                                (reset! error ""))
                    :on-key-down #(when (or (= (.-key %) "Enter") (= (.-key %) " "))
                                   (state/update-form-data! {:account-purpose (:id opt)})
                                   (reset! error ""))}
              [:div.text-3xl.mt-0.5 (:icon opt)]
              [:div.flex-1
               [:div.font-semibold.text-gray-800 (:title opt)]
               [:div.text-sm.text-gray-500.mt-0.5 (:desc opt)]]
              [:div {:class (str "mt-1 w-5 h-5 rounded-full border-2 flex items-center justify-center flex-shrink-0 transition-all "
                                (if (= selected (:id opt))
                                  "border-csb-teal"
                                  "border-gray-300"))}
               (when (= selected (:id opt))
                 [:div.w-2.5.h-2.5.rounded-full.bg-csb-teal])]])]
          (when (seq @error)
            [:p.error-text.mt-2 @error])]

         ;; Disclosures
         [:div.text-xs.text-gray-400.leading-relaxed
          "Cambridge Savings Bank is a Member FDIC. By proceeding, you agree to our "
          [:a.underline.hover:text-gray-600 {:href "#"} "Privacy Policy"]
          " and "
          [:a.underline.hover:text-gray-600 {:href "#"} "Terms of Use"]
          ". This application is for business accounts only. For personal accounts, please visit your nearest branch or call us."]

         [:div.flex.justify-end
          [:button.btn-primary.px-8
           {:on-click #(if (empty? selected)
                        (reset! error "Please select an option to continue.")
                        (do (reset! error "")
                            (state/go-next!)))}
           "Continue →"]]]))))
