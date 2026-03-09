(ns csb.loan.state
  (:require [reagent.core :as r]))

;; Loan application steps aligned with nCino workflow
(def steps
  [{:id 1 :label "Loan Request" :short "Request"}
   {:id 2 :label "Business Information" :short "Business"}
   {:id 3 :label "Financial Details" :short "Financials"}
   {:id 4 :label "Owners & Guarantors" :short "Guarantors"}
   {:id 5 :label "Collateral" :short "Collateral"}
   {:id 6 :label "Documents" :short "Documents"}
   {:id 7 :label "Review & Submit" :short "Review"}])

;; Loan types offered
(def loan-types
  [{:value "" :label "Select loan type..."}
   {:value "term-loan" :label "Term Loan"}
   {:value "line-of-credit" :label "Business Line of Credit"}
   {:value "sba-7a" :label "SBA 7(a) Loan"}
   {:value "sba-504" :label "SBA 504 Loan"}
   {:value "equipment" :label "Equipment Financing"}
   {:value "commercial-real-estate" :label "Commercial Real Estate Loan"}
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
   {:value "acquisition" :label "Business Acquisition"}
   {:value "startup" :label "Startup Costs"}
   {:value "renovation" :label "Renovation / Construction"}
   {:value "other" :label "Other"}])

;; Collateral types
(def collateral-types
  [{:value "" :label "Select collateral type..."}
   {:value "real-estate" :label "Real Estate"}
   {:value "equipment" :label "Equipment / Machinery"}
   {:value "inventory" :label "Inventory"}
   {:value "accounts-receivable" :label "Accounts Receivable"}
   {:value "vehicles" :label "Vehicles"}
   {:value "securities" :label "Securities / Investments"}
   {:value "cash" :label "Cash / CD"}
   {:value "other" :label "Other Assets"}
   {:value "unsecured" :label "No Collateral (Unsecured)"}])

;; Document types required for loan
(def required-documents
  [{:id "business-tax-returns" :label "Business Tax Returns (Last 3 Years)" :required true}
   {:id "personal-tax-returns" :label "Personal Tax Returns (Last 3 Years)" :required true}
   {:id "financial-statements" :label "Business Financial Statements" :required true}
   {:id "bank-statements" :label "Business Bank Statements (Last 6 Months)" :required true}
   {:id "business-plan" :label "Business Plan" :required false}
   {:id "articles-of-incorporation" :label "Articles of Incorporation / Organization" :required true}
   {:id "business-licenses" :label "Business Licenses & Permits" :required false}
   {:id "accounts-receivable-aging" :label "Accounts Receivable Aging Report" :required false}
   {:id "accounts-payable-aging" :label "Accounts Payable Aging Report" :required false}
   {:id "collateral-docs" :label "Collateral Documentation" :required false}
   {:id "lease-agreements" :label "Lease Agreements (if applicable)" :required false}
   {:id "franchise-agreement" :label "Franchise Agreement (if applicable)" :required false}])

;; Initial form data structure for nCino integration
(def initial-data
  {;; Step 1: Loan Request
   :loan-type ""
   :loan-amount ""
   :loan-purpose ""
   :loan-purpose-detail ""
   :loan-term "" ; in months
   :requested-rate-type "" ; fixed, variable
   :urgency "" ; immediate, 30-days, 60-days, exploring
   :existing-customer false
   :existing-account-number ""
   
   ;; Step 2: Business Information (KYB - Know Your Business)
   :business-legal-name ""
   :dba-name ""
   :business-type "" ; LLC, Corp, Partnership, Sole Prop
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
   :mailing-same-as-business true
   :mailing-address ""
   :mailing-city ""
   :mailing-state ""
   :mailing-zip ""
   :naics-code ""
   :industry ""
   :business-description ""
   :number-of-employees ""
   :annual-payroll ""
   
   ;; Step 3: Financial Information
   :annual-revenue ""
   :annual-revenue-prior-year ""
   :gross-profit-margin ""
   :net-income ""
   :net-income-prior-year ""
   :total-assets ""
   :total-liabilities ""
   :current-debt-payments ""
   :accounts-receivable ""
   :accounts-payable ""
   :inventory-value ""
   :cash-on-hand ""
   :existing-loans []
   :bankruptcy-history false
   :bankruptcy-details ""
   :tax-liens false
   :tax-lien-details ""
   :pending-lawsuits false
   :lawsuit-details ""
   
   ;; Step 4: Owners & Guarantors (for credit checks)
   :guarantors []
   :primary-guarantor-is-applicant true
   
   ;; Primary applicant/guarantor info
   :primary-first-name ""
   :primary-last-name ""
   :primary-title ""
   :primary-ownership-pct ""
   :primary-email ""
   :primary-phone ""
   :primary-ssn ""
   :primary-dob ""
   :primary-address ""
   :primary-city ""
   :primary-state ""
   :primary-zip ""
   :primary-years-at-address ""
   :primary-housing-status "" ; own, rent, other
   :primary-monthly-housing-payment ""
   :primary-annual-income ""
   :primary-other-income ""
   :primary-other-income-source ""
   :primary-credit-score-estimate "" ; excellent, good, fair, poor
   
   ;; Step 5: Collateral
   :collateral-offered false
   :collateral-type ""
   :collateral-description ""
   :collateral-estimated-value ""
   :collateral-address ""
   :collateral-city ""
   :collateral-state ""
   :collateral-zip ""
   :collateral-lien-holder ""
   :collateral-lien-amount ""
   :additional-collateral []
   
   ;; Step 6: Documents
   :uploaded-documents {}
   :document-notes ""
   
   ;; Step 7: Agreements
   :agree-credit-check false
   :agree-terms false
   :agree-esign false
   :agree-privacy false
   :certify-accurate false})

;; Loan application state
(defonce loan-state (r/atom {:current-step 1
                              :form-data initial-data
                              :submitted false
                              :validation-errors {}}))

(defn update-form-data! [updates]
  (swap! loan-state update :form-data merge updates))

(defn go-next! []
  (when (< (:current-step @loan-state) (count steps))
    (swap! loan-state update :current-step inc)
    (.scrollTo js/window #js {:top 0 :behavior "smooth"})))

(defn go-back! []
  (when (> (:current-step @loan-state) 1)
    (swap! loan-state update :current-step dec)
    (.scrollTo js/window #js {:top 0 :behavior "smooth"})))

(defn go-to-step! [step]
  (swap! loan-state assoc :current-step step)
  (.scrollTo js/window #js {:top 0 :behavior "smooth"}))

(defn submit! []
  (swap! loan-state assoc :submitted true)
  (.scrollTo js/window #js {:top 0 :behavior "smooth"}))

(defn start-over! []
  (reset! loan-state {:current-step 1
                      :form-data initial-data
                      :submitted false
                      :validation-errors {}}))

;; Validation helpers
(defn validate-step [step form-data]
  (case step
    1 (cond-> {}
        (empty? (:loan-type form-data))
        (assoc :loan-type "Please select a loan type.")
        
        (empty? (:loan-amount form-data))
        (assoc :loan-amount "Please enter the loan amount.")
        
        (empty? (:loan-purpose form-data))
        (assoc :loan-purpose "Please select the loan purpose."))
    
    2 (cond-> {}
        (empty? (:business-legal-name form-data))
        (assoc :business-legal-name "Business legal name is required.")
        
        (empty? (:ein form-data))
        (assoc :ein "EIN / Tax ID is required.")
        
        (empty? (:business-type form-data))
        (assoc :business-type "Business type is required."))
    
    {}))
