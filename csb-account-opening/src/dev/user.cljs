(ns user
  "Development namespace for REPL utilities."
  (:require [csb.state :as state]))

(defn reset-app!
  "Reset the application to initial state."
  []
  (state/start-over!))

(defn go-to-step!
  "Navigate directly to a specific step (1-6)."
  [step]
  (state/go-to-step! step))

(defn show-state
  "Print the current application state."
  []
  (cljs.pprint/pprint @state/app-state))

(defn fill-sample-data!
  "Fill the form with sample data for testing."
  []
  (state/update-form-data!
   {:account-purpose "new-business"
    :selected-product "business-checking"
    :business-name "Acme Widget Company LLC"
    :dba "Acme Widgets"
    :business-type "llc-multi"
    :ein "12-3456789"
    :state-of-formation "MA"
    :date-established "2020-01-15"
    :business-phone "(617) 555-1234"
    :business-address "123 Main Street"
    :business-city "Cambridge"
    :business-state "MA"
    :business-zip "02138"
    :naics "541511"
    :business-description "Software development and consulting services"
    :first-name "Jane"
    :last-name "Smith"
    :title "ceo"
    :email "jane@acmewidgets.com"
    :phone "(617) 555-5678"
    :dob "1985-06-15"
    :ssn "123-45-6789"
    :address "456 Oak Avenue"
    :city "Cambridge"
    :state "MA"
    :zip "02139"
    :id-type "drivers-license"
    :id-number "S12345678"
    :id-expiry "2028-12-31"
    :id-state "MA"
    :ownership-pct "60"
    :certify-beneficial-owners true
    :agree-terms true
    :agree-esign true
    :agree-privacy true}))
