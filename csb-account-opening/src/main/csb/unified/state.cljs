(ns csb.unified.state
  (:require [reagent.core :as r]
            [csb.storage :as storage]))

;; Flow types
(def flow-types
  [{:id :account-only :label "Open Business Account" :desc "Checking, Savings, CDs"}
   {:id :loan-only :label "Apply for Business Loan" :desc "Term loans, lines of credit, SBA loans"}
   {:id :loan-and-account :label "Loan + New Account" :desc "Apply for financing with a new business account"}])

;; Dynamic steps based on flow selection
;; Base KYC/KYB steps (always required)
(def base-steps
  [{:id :intent :label "Get Started" :number 1}
   {:id :business-info :label "Business Information" :number 2}
   {:id :owner-info :label "Owner Information" :number 3}])

;; Loan-specific steps (for loan flows)
(def loan-steps
  [{:id :loan-request :label "Loan Request" :number 4}
   {:id :financials :label "Financial Details" :number 5}
   {:id :documents :label "Documents" :number 6}
   {:id :loan-decision :label "Loan Decision" :number 7}])

;; Account selection step
(def account-step
  {:id :account-selection :label "Select Accounts" :number 8})

;; Final steps
(def final-steps
  [{:id :review :label "Review & Submit" :number 9}])

(defn get-steps-for-flow [flow-type loan-approved?]
  (case flow-type
    :account-only
    (concat base-steps
            [(assoc account-step :number 4)]
            [(assoc (first final-steps) :number 5)])
    
    :loan-only
    (if loan-approved?
      ;; If approved, need to open checking for payments
      (concat base-steps
              loan-steps
              [(assoc account-step :number 8)]
              [(assoc (first final-steps) :number 9)])
      ;; If not yet decided or denied, no account step
      (concat base-steps
              loan-steps
              [(assoc (first final-steps) :number 8)]))
    
    :loan-and-account
    (concat base-steps
            loan-steps
            [(assoc account-step :number 8)]
            [(assoc (first final-steps) :number 9)])
    
    ;; Default - just base steps
    base-steps))

;; Business types
(def business-types
  [{:value "" :label "Select business type..."}
   {:value "sole-proprietorship" :label "Sole Proprietorship"}
   {:value "llc" :label "Limited Liability Company (LLC)"}
   {:value "corporation" :label "Corporation (C-Corp or S-Corp)"}
   {:value "partnership" :label "Partnership"}
   {:value "non-profit" :label "Non-Profit Organization"}])

;; Loan types
(def loan-types
  [{:value "" :label "Select loan type..."}
   {:value "term-loan" :label "Term Loan"}
   {:value "line-of-credit" :label "Business Line of Credit"}
   {:value "sba-7a" :label "SBA 7(a) Loan"}
   {:value "sba-504" :label "SBA 504 Loan"}
   {:value "equipment" :label "Equipment Financing"}
   {:value "commercial-real-estate" :label "Commercial Real Estate"}
   {:value "working-capital" :label "Working Capital Loan"}])

;; Loan purposes
(def loan-purposes
  [{:value "" :label "Select purpose..."}
   {:value "working-capital" :label "Working Capital / Cash Flow"}
   {:value "equipment" :label "Equipment Purchase"}
   {:value "real-estate" :label "Real Estate Purchase"}
   {:value "expansion" :label "Business Expansion"}
   {:value "inventory" :label "Inventory Purchase"}
   {:value "refinance" :label "Debt Refinancing"}
   {:value "startup" :label "Startup Costs"}
   {:value "other" :label "Other"}])

;; Account products
(def account-products
  [{:id "small-business-checking"
    :name "Small Business Checking"
    :description "Essential checking for daily operations"
    :features ["No minimum balance" "Free online banking" "Debit card included"]
    :required-for-loan true}
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

;; Required documents for loan
(def required-documents
  [{:id "business-tax-returns" :label "Business Tax Returns (3 years)" :required true}
   {:id "personal-tax-returns" :label "Personal Tax Returns (3 years)" :required true}
   {:id "financial-statements" :label "Business Financial Statements" :required true}
   {:id "bank-statements" :label "Bank Statements (6 months)" :required true}
   {:id "articles-of-incorporation" :label "Articles of Incorporation" :required true}
   {:id "business-licenses" :label "Business Licenses" :required false}
   {:id "business-plan" :label "Business Plan" :required false}])

;; Consolidated form data - no duplicates!
(def initial-data
  {;; Flow selection
   :flow-type nil ; :account-only, :loan-only, :loan-and-account
   
   ;; === KYB - Business Information (Step 2) ===
   :business-legal-name ""
   :dba-name ""
   :business-type ""
   :ein ""
   :state-of-incorporation ""
   :date-established ""
   :years-in-business ""
   :business-phone ""
   :business-email ""
   :business-website ""
   :business-address ""
   :business-city ""
   :business-state ""
   :business-zip ""
   :naics-code ""
   :industry ""
   :business-description ""
   :number-of-employees ""
   
   ;; === KYC - Owner/Applicant Information (Step 3) ===
   :owner-first-name ""
   :owner-last-name ""
   :owner-title ""
   :owner-email ""
   :owner-phone ""
   :owner-ssn ""
   :owner-dob ""
   :owner-address ""
   :owner-city ""
   :owner-state ""
   :owner-zip ""
   :owner-ownership-pct ""
   :owner-id-type ""
   :owner-id-number ""
   :owner-id-state ""
   :owner-id-expiry ""
   ;; Additional owners
   :additional-owners []
   :certify-beneficial-owners false
   
   ;; === Loan Request (Step 4 - loan flows only) ===
   :loan-type ""
   :loan-amount ""
   :loan-purpose ""
   :loan-purpose-detail ""
   :loan-term ""
   :urgency ""
   
   ;; === Financial Information (Step 5 - loan flows only) ===
   :annual-revenue ""
   :annual-revenue-prior ""
   :net-income ""
   :total-assets ""
   :total-liabilities ""
   :existing-debt-payments ""
   :cash-on-hand ""
   :bankruptcy-history false
   :bankruptcy-details ""
   :tax-liens false
   :pending-lawsuits false
   
   ;; Collateral (simplified)
   :collateral-offered false
   :collateral-type ""
   :collateral-description ""
   :collateral-value ""
   
   ;; === Documents (Step 6 - loan flows only) ===
   :uploaded-documents {}
   
   ;; === Loan Decision (Step 7 - loan flows only) ===
   ;; This would come from backend/nCino in real implementation
   :loan-decision nil ; :approved, :denied, :pending-review
   :loan-decision-notes ""
   :approved-amount ""
   :approved-rate ""
   :approved-term ""
   :continue-with-account-after-denial false ; User can opt to open account even if loan denied
   
   ;; === Account Selection (Step 8) ===
   :selected-accounts #{"small-business-checking"} ; Default to checking
   :checking-required-for-loan true
   
   ;; === Agreements ===
   :agree-credit-check false
   :agree-terms false
   :agree-esign false
   :agree-privacy false
   :certify-accurate false})

;; Application state
(defonce app-state 
  (r/atom {:current-step :intent
           :form-data initial-data
           :submitted false
           :validation-errors {}
           :app-id nil}))  ;; Track current application ID

;; Get current step index for progress display
(defn get-step-index [step-id flow-type loan-approved?]
  (let [steps (get-steps-for-flow flow-type loan-approved?)]
    (->> steps
         (map-indexed (fn [idx s] [idx s]))
         (filter #(= step-id (:id (second %))))
         first
         first)))

;; Navigation helpers
(defn get-next-step [current-step flow-type loan-decision continue-with-account?]
  (case current-step
    :intent :business-info
    :business-info :owner-info
    :owner-info (if (= flow-type :account-only)
                  :account-selection
                  :loan-request)
    :loan-request :financials
    :financials :documents
    :documents :loan-decision
    :loan-decision (cond
                     ;; Denied but wants to continue with account
                     (and (= loan-decision :denied) continue-with-account?)
                     :account-selection
                     ;; Denied and does not want account
                     (= loan-decision :denied)
                     :review
                     ;; Approved or pending - go to account selection
                     :else
                     :account-selection)
    :account-selection :review
    :review :submitted
    nil))

(defn get-prev-step [current-step flow-type loan-decision continue-with-account?]
  (case current-step
    :business-info :intent
    :owner-info :business-info
    :loan-request :owner-info
    :financials :loan-request
    :documents :financials
    :loan-decision :documents
    :account-selection (if (= flow-type :account-only)
                         :owner-info
                         :loan-decision)
    :review (cond
              (= flow-type :account-only)
              :account-selection
              
              (and (= loan-decision :denied) continue-with-account?)
              :account-selection
              
              (= loan-decision :denied)
              :loan-decision
              
              :else
              :account-selection)
    nil))

;; State update functions
(defn update-form-data! [updates]
  (swap! app-state update :form-data merge updates))

(defn set-flow-type! [flow-type]
  (swap! app-state assoc-in [:form-data :flow-type] flow-type))

;; Forward declaration for submit-application!
(declare submit-application!)

(defn go-next! []
  (let [{:keys [form-data current-step]} @app-state
        flow-type (:flow-type form-data)
        loan-decision (:loan-decision form-data)
        continue-with-account? (:continue-with-account-after-denial form-data)
        next-step (get-next-step current-step flow-type loan-decision continue-with-account?)]
    (when next-step
      (if (= next-step :submitted)
        (submit-application!)  ;; Save the application with pending-review status
        (swap! app-state assoc :current-step next-step))
      (js/setTimeout
       #(when-let [el (.getElementById js/document "main-content")]
          (.scrollIntoView el #js {:behavior "smooth" :block "start"}))
       50))))

(defn go-back! []
  (let [{:keys [form-data current-step]} @app-state
        flow-type (:flow-type form-data)
        loan-decision (:loan-decision form-data)
        continue-with-account? (:continue-with-account-after-denial form-data)
        prev-step (get-prev-step current-step flow-type loan-decision continue-with-account?)]
    (when prev-step
      (swap! app-state assoc :current-step prev-step)
      (js/setTimeout
       #(when-let [el (.getElementById js/document "main-content")]
          (.scrollIntoView el #js {:behavior "smooth" :block "start"}))
       50))))

(defn go-to-step! [step-id]
  (swap! app-state assoc :current-step step-id)
  (js/setTimeout
   #(when-let [el (.getElementById js/document "main-content")]
      (.scrollIntoView el #js {:behavior "smooth" :block "start"}))
   50))

(defn simulate-loan-decision! []
  ;; In production, this would call nCino API
  ;; For demo, we'll approve loans over $50k with good financials
  (let [form-data (:form-data @app-state)
        loan-amount (js/parseInt (:loan-amount form-data) 10)
        revenue (js/parseInt (:annual-revenue form-data) 10)]
    (cond
      ;; Simulate approval for reasonable loan-to-revenue ratio
      (and (> revenue 0) (< (/ loan-amount revenue) 0.5))
      (swap! app-state update :form-data merge
             {:loan-decision :approved
              :approved-amount (:loan-amount form-data)
              :approved-rate "7.25%"
              :approved-term (:loan-term form-data)
              :loan-decision-notes "Congratulations! Your loan has been pre-approved."})
      
      ;; Pending review for borderline cases
      (and (> revenue 0) (< (/ loan-amount revenue) 0.75))
      (swap! app-state update :form-data merge
             {:loan-decision :pending-review
              :loan-decision-notes "Your application requires additional review by our underwriting team."})
      
      ;; Deny if ratio too high or missing data
      :else
      (swap! app-state update :form-data merge
             {:loan-decision :denied
              :loan-decision-notes "We're unable to approve your loan at this time based on the information provided."}))))

;; Save current application as draft
(defn save-draft! []
  (let [state @app-state
        app-data {:id (:app-id state)
                  :status :draft
                  :current-step (:current-step state)
                  :form-data (:form-data state)
                  :submitted false}
        saved (storage/save-application! app-data)]
    (swap! app-state assoc :app-id (:id saved))
    saved))

;; Submit application (mark as pending-review for human review)
(defn submit-application! []
  (let [state @app-state
        now (.toISOString (js/Date.))
        app-data {:id (:app-id state)
                  :status :pending-review  ;; All applications require human review
                  :current-step (:current-step state)
                  :form-data (:form-data state)
                  :submitted true
                  :submitted-at now
                  :review-status :awaiting-review
                  :review-notes nil}]
    (storage/save-application! app-data)
    (swap! app-state assoc :submitted true :app-id (:id (storage/save-application! app-data)))))

;; Load an existing application
(defn load-application! [app-id]
  (when-let [app (storage/get-application app-id)]
    (reset! app-state {:current-step (:current-step app)
                       :form-data (:form-data app)
                       :submitted (:submitted app)
                       :validation-errors {}
                       :app-id (:id app)})))

;; Delete an application
(defn delete-app! [app-id]
  (storage/delete-application! app-id))

;; Get all applications (for dashboard)
(defn get-all-applications []
  (storage/get-all-applications))

(defn start-over! []
  (reset! app-state {:current-step :intent
                     :form-data initial-data
                     :submitted false
                     :validation-errors {}
                     :app-id nil}))

;; Sample SSO customer data (simulates data from Online Banking SSO token)
(def sso-customer-data
  {;; Business Information (from existing OLB business profile)
   :business-legal-name "Acme Consulting LLC"
   :dba-name "Acme Business Solutions"
   :business-type "llc"
   :ein "12-3456789"
   :state-of-incorporation "MA"
   :date-established "2018-03-15"
   :years-in-business "8"
   :business-phone "(617) 555-1234"
   :business-email "john.smith@acmeconsulting.com"
   :business-website "www.acmeconsulting.com"
   :business-address "100 Cambridge Street"
   :business-city "Cambridge"
   :business-state "MA"
   :business-zip "02139"
   :naics-code "541611"
   :industry "Management Consulting Services"
   :business-description "Business strategy and operations consulting for small to mid-size companies"
   :number-of-employees "12"
   
   ;; Owner/Applicant Information (from existing OLB user profile)
   :owner-first-name "John"
   :owner-last-name "Smith"
   :owner-title "Managing Partner"
   :owner-email "john.smith@acmeconsulting.com"
   :owner-phone "(617) 555-5678"
   :owner-ssn "123-45-6789"
   :owner-dob "1975-06-20"
   :owner-address "456 Elm Street"
   :owner-city "Somerville"
   :owner-state "MA"
   :owner-zip "02144"
   :owner-ownership-pct "60"
   :owner-id-type "drivers-license"
   :owner-id-number "S12345678"
   :owner-id-state "MA"
   :owner-id-expiry "2028-06-20"
   
   ;; Financial Information (from existing account history)
   :annual-revenue "850000"
   :annual-revenue-prior "720000"
   :cash-on-hand "125000"})

;; Initialize unified flow with SSO pre-filled data
(defn init-from-sso! []
  (reset! app-state {:current-step :intent
                     :form-data (merge initial-data 
                                       sso-customer-data
                                       {:sso-authenticated true
                                        :sso-session-id (str "SSO-" (random-uuid))})
                     :submitted false
                     :validation-errors {}
                     :app-id nil}))

;; Check if current session is SSO authenticated
(defn sso-authenticated? []
  (get-in @app-state [:form-data :sso-authenticated]))

