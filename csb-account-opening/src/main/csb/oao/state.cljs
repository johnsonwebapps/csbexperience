(ns csb.oao.state
  (:require [reagent.core :as r]))

;; Entry types for the three use cases
;; :sso         - Q2-BUS-035:  SSO from Online Banking to OAO
;; :new-enroll  - Q2-BUS-035A: Open account via website with OLB enrollment
;; :ob-login    - Q2-BUS-035B: Open account via website using OB credentials

;; Steps per entry type
(def sso-steps
  [{:id :sso-auth      :label "SSO Authentication"        :number 1}
   {:id :review-info   :label "Review Pre-filled Info"     :number 2}
   {:id :select-account :label "Select Account"            :number 3}
   {:id :account-services :label "Account Services"        :number 4}
   {:id :initial-deposit :label "Initial Deposit"          :number 5}
   {:id :review-submit :label "Review & Submit"            :number 6}])

(def new-enroll-steps
  [{:id :get-started   :label "Get Started"                :number 1}
   {:id :business-info :label "Business Information"       :number 2}
   {:id :applicant-info :label "Applicant Information"     :number 3}
   {:id :select-account :label "Select Account"            :number 4}
   {:id :account-services :label "Account Services"        :number 5}
   {:id :initial-deposit :label "Initial Deposit"          :number 6}
   {:id :review-submit :label "Review & Submit"            :number 7}
   {:id :olb-enrollment :label "Online Banking Enrollment" :number 8}])

(def ob-login-steps
  [{:id :ob-auth       :label "Online Banking Login"       :number 1}
   {:id :review-info   :label "Review Pre-filled Info"     :number 2}
   {:id :select-account :label "Select Account"            :number 3}
   {:id :account-services :label "Account Services"        :number 4}
   {:id :initial-deposit :label "Initial Deposit"          :number 5}
   {:id :review-submit :label "Review & Submit"            :number 6}])

(defn get-steps [entry-type]
  (case entry-type
    :sso sso-steps
    :new-enroll new-enroll-steps
    :ob-login ob-login-steps
    []))

;; Account products
(def account-products
  [{:id "small-business-checking"
    :name "Small Business Checking"
    :description "Essential checking for daily operations"
    :features ["No minimum balance" "Free online banking" "Debit card included"]}
   {:id "business-growth-checking"
    :name "Business Growth Checking"
    :description "Built for growing businesses"
    :features ["Earn interest on balance" "Unlimited transactions" "Free online banking"]}
   {:id "small-business-money-market-special"
    :name "Small Business Money Market Special"
    :description "Higher yields for larger balances"
    :features ["Tiered interest rates" "Check writing" "FDIC insured"]}
   {:id "4-month-cd-special"
    :name "4 Month Certificate of Deposit Special"
    :description "Lock in a guaranteed promotional rate"
    :features ["4 month term" "Competitive promotional rate" "FDIC insured"]}])

;; Account services
(def account-services-options
  [{:id "debit-card"     :label "Business Debit Card"     :default true}
   {:id "estatements"    :label "eStatements"              :default true}
   {:id "mobile-deposit" :label "Mobile Deposit"           :default false}
   {:id "wire-transfers" :label "Wire Transfers"           :default false}
   {:id "ach-origination" :label "ACH Origination"         :default false}
   {:id "positive-pay"   :label "Positive Pay"             :default false}])

;; Initial form data
(def initial-data
  {;; Entry type
   :entry-type nil

   ;; SSO / OB Auth fields (simulated)
   :ob-session-valid false
   :ob-login-id ""
   :ob-password ""
   :id-token nil
   :mfa-code ""
   :mfa-verified false

   ;; Pre-filled customer info (from SSO token or OB login)
   :first-name ""
   :last-name ""
   :email ""
   :phone ""
   :ssn ""
   :dob ""
   :address ""
   :city ""
   :state ""
   :zip ""
   
   ;; Business info (for new-enroll flow)
   :business-legal-name ""
   :dba-name ""
   :business-type ""
   :ein ""
   :state-of-formation ""
   :date-established ""
   :business-phone ""
   :business-address ""
   :business-city ""
   :business-state ""
   :business-zip ""
   :business-description ""
   :naics-code ""

   ;; Account selection
   :selected-account ""
   
   ;; Account services
   :selected-services #{"debit-card" "estatements"}
   
   ;; Initial deposit
   :fund-source "" ;; "existing-account" or "external"
   :source-account ""
   :deposit-amount ""
   :routing-number ""
   :account-number ""
   
   ;; OLB Enrollment (Q2-BUS-035A)
   :enroll-olb true
   :new-login-id ""
   :new-password ""
   :confirm-password ""
   :mfa-phone ""
   :mfa-email ""

   ;; Agreements
   :agree-terms false
   :agree-esign false
   :agree-privacy false
   :consent-data-sharing false
   :consent-account-opening false})

;; Application state
(defonce app-state
  (r/atom {:current-step nil
           :form-data initial-data
           :submitted false
           :confirmation-data nil
           :validation-errors {}}))

;; Navigation
(defn get-step-index [step-id steps]
  (->> steps
       (map-indexed (fn [idx s] [idx s]))
       (filter #(= step-id (:id (second %))))
       first
       first))

(defn go-to-step! [step-id]
  (swap! app-state assoc :current-step step-id)
  (.scrollTo js/window #js {:top 0 :behavior "smooth"}))

(defn go-next! []
  (let [{:keys [current-step form-data]} @app-state
        entry-type (:entry-type form-data)
        steps (get-steps entry-type)
        current-idx (get-step-index current-step steps)]
    (when (and current-idx (< current-idx (dec (count steps))))
      (go-to-step! (:id (nth steps (inc current-idx)))))))

(defn go-back! []
  (let [{:keys [current-step form-data]} @app-state
        entry-type (:entry-type form-data)
        steps (get-steps entry-type)
        current-idx (get-step-index current-step steps)]
    (when (and current-idx (> current-idx 0))
      (go-to-step! (:id (nth steps (dec current-idx)))))))

(defn update-form-data! [updates]
  (swap! app-state update :form-data merge updates))

(defn submit! []
  (swap! app-state assoc 
         :submitted true
         :confirmation-data {:account-number (str "CSB-" (rand-int 900000000))
                             :timestamp (.toLocaleString (js/Date.))})
  (.scrollTo js/window #js {:top 0 :behavior "smooth"}))

(defn start-over! []
  (reset! app-state {:current-step nil
                     :form-data initial-data
                     :submitted false
                     :confirmation-data nil
                     :validation-errors {}}))

;; Initialize a flow
(defn init-sso-flow! []
  (reset! app-state
          {:current-step :sso-auth
           :form-data (merge initial-data
                             {:entry-type :sso})
           :submitted false
           :confirmation-data nil
           :validation-errors {}}))

(defn init-new-enroll-flow! []
  (reset! app-state
          {:current-step :get-started
           :form-data (merge initial-data
                             {:entry-type :new-enroll})
           :submitted false
           :confirmation-data nil
           :validation-errors {}}))

(defn init-ob-login-flow! []
  (reset! app-state
          {:current-step :ob-auth
           :form-data (merge initial-data
                             {:entry-type :ob-login})
           :submitted false
           :confirmation-data nil
           :validation-errors {}}))
