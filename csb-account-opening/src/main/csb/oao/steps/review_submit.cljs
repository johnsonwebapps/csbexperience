(ns csb.oao.steps.review-submit
  (:require [reagent.core :as r]
            [csb.oao.state :as state]))

;; Review & Submit step — shared across all three flows

(defn section [title items]
  [:div.bg-white.rounded-xl.p-6 {:style {:box-shadow "0 2px 12px rgba(0,0,0,0.08)"}}
   [:h3.font-semibold.mb-4 {:style {:color "#333"}} title]
   [:div.space-y-2
    (for [[label value] items]
      ^{:key label}
      [:div.flex.justify-between.py-1 {:style {:border-bottom "1px solid #f5f5f5"}}
       [:span.text-sm.text-gray-500 label]
       [:span.text-sm.font-medium {:style {:color "#333"}} value]])]])

(defn review-submit-step []
  (let [{:keys [form-data]} @state/app-state]
    [:div.space-y-6
     [:div.bg-white.rounded-xl.p-6 {:style {:box-shadow "0 2px 12px rgba(0,0,0,0.08)"}}
      [:h2.text-xl.font-bold.mb-2 {:style {:color "#333"}} "Review & Submit"]
      [:p.text-sm.text-gray-500 "Please review your application details before submitting."]]
     
     ;; Personal Information
     [section "Personal Information"
      [["Name" (str (:first-name form-data) " " (:last-name form-data))]
       ["Email" (:email form-data)]
       ["Phone" (:phone form-data)]
       ["Address" (str (:address form-data) ", " (:city form-data) ", " (:state form-data) " " (:zip form-data))]]]
     
     ;; Business Information
     [section "Business Information"
      [["Legal Name" (:business-legal-name form-data)]
       ["DBA" (:dba-name form-data)]
       ["Business Type" (:business-type form-data)]
       ["EIN" (:ein form-data)]
       ["Address" (str (:business-address form-data) ", " (:business-city form-data) ", " (:business-state form-data) " " (:business-zip form-data))]]]
     
     ;; Account
     (when (seq (:selected-account form-data))
       (let [product (->> state/account-products
                          (filter #(= (:id %) (:selected-account form-data)))
                          first)]
         [section "Selected Account"
          [["Account Type" (or (:name product) (:selected-account form-data))]
           ["Services" (clojure.string/join ", "
                        (map (fn [svc]
                               (let [opt (->> state/account-services-options
                                              (filter #(= (:id %) svc))
                                              first)]
                                 (:label opt svc)))
                             (:selected-services form-data)))]]]))
     
     ;; Deposit
     (when (seq (:fund-source form-data))
       [section "Initial Deposit"
        [["Source" (case (:fund-source form-data)
                    "existing-account" (str "CSB Account " (:source-account form-data))
                    "external" "External Bank Account"
                    "")]
         ["Amount" (str "$" (:deposit-amount form-data))]]])
     
     ;; Agreements
     [:div.bg-white.rounded-xl.p-6 {:style {:box-shadow "0 2px 12px rgba(0,0,0,0.08)"}}
      [:h3.font-semibold.mb-4 {:style {:color "#333"}} "Agreements & Consent"]
      [:div.space-y-3
       [:label.flex.items-start.gap-3.cursor-pointer
        [:input.mt-1 {:type "checkbox"
                      :checked (:consent-data-sharing form-data)
                      :on-change #(state/update-form-data! {:consent-data-sharing (.. % -target -checked)})}]
        [:span.text-sm.text-gray-600 "I consent to sharing my personal data for the purpose of opening this account."]]
       [:label.flex.items-start.gap-3.cursor-pointer
        [:input.mt-1 {:type "checkbox"
                      :checked (:consent-account-opening form-data)
                      :on-change #(state/update-form-data! {:consent-account-opening (.. % -target -checked)})}]
        [:span.text-sm.text-gray-600 "I consent to initiating the account opening process."]]
       [:label.flex.items-start.gap-3.cursor-pointer
        [:input.mt-1 {:type "checkbox"
                      :checked (:agree-terms form-data)
                      :on-change #(state/update-form-data! {:agree-terms (.. % -target -checked)})}]
        [:span.text-sm.text-gray-600 "I agree to the Terms and Conditions, E-SIGN Disclosure, and Privacy Policy."]]
       [:label.flex.items-start.gap-3.cursor-pointer
        [:input.mt-1 {:type "checkbox"
                      :checked (:agree-esign form-data)
                      :on-change #(state/update-form-data! {:agree-esign (.. % -target -checked)})}]
        [:span.text-sm.text-gray-600 "I agree to receive electronic documents and disclosures."]]]]
     
     ;; Navigation
     [:div.flex.justify-between
      [:button.py-3.px-6.rounded-lg.font-semibold
       {:style {:color "#00857c" :border "1px solid #00857c"}
        :on-click #(state/go-back!)}
       "← Back"]
      (let [all-agreed (and (:consent-data-sharing form-data)
                            (:consent-account-opening form-data)
                            (:agree-terms form-data)
                            (:agree-esign form-data))]
        [:button.py-3.px-8.rounded-lg.font-semibold.text-white
         {:style {:background-color "#00857c"
                  :opacity (if all-agreed 1 0.5)}
          :disabled (not all-agreed)
          :on-click #(when all-agreed
                       (if (= (:entry-type form-data) :new-enroll)
                         (state/go-next!)  ; Go to OLB enrollment step
                         (state/submit!)))}
         "Submit Application →"])]]))
