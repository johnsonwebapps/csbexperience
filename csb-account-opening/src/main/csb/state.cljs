(ns csb.state
  (:require [reagent.core :as r]))

(def steps
  [{:id 1 :label "Get Started" :short "Get Started"}
   {:id 2 :label "Choose Account" :short "Account Type"}
   {:id 3 :label "Business Details" :short "Business"}
   {:id 4 :label "Your Information" :short "Applicant"}
   {:id 5 :label "Ownership" :short "Ownership"}
   {:id 6 :label "Review & Submit" :short "Review"}])

(def initial-data
  {;; Step 1
   :account-purpose ""
   ;; Step 2
   :selected-product ""
   ;; Step 3
   :business-name ""
   :dba ""
   :business-type ""
   :ein ""
   :state-of-formation ""
   :date-established ""
   :business-phone ""
   :business-address ""
   :business-city ""
   :business-state ""
   :business-zip ""
   :naics ""
   :business-description ""
   ;; Step 4
   :first-name ""
   :last-name ""
   :title ""
   :email ""
   :phone ""
   :dob ""
   :ssn ""
   :address ""
   :city ""
   :state ""
   :zip ""
   :id-type ""
   :id-number ""
   :id-expiry ""
   :id-state ""
   :ownership-pct ""
   ;; Step 5
   :beneficial-owners []
   :certify-beneficial-owners false
   ;; Step 6
   :agree-terms false
   :agree-esign false
   :agree-privacy false})

;; Application state
(defonce app-state (r/atom {:page :landing
                            :current-step 1
                            :form-data initial-data
                            :submitted false}))

(defn go-to-application! []
  (swap! app-state assoc :page :application)
  (.scrollTo js/window #js {:top 0 :behavior "smooth"}))

(defn go-to-loan-application! []
  (swap! app-state assoc :page :loan-application)
  (.scrollTo js/window #js {:top 0 :behavior "smooth"}))

(defn go-to-unified-application! []
  (swap! app-state assoc :page :unified-application)
  (.scrollTo js/window #js {:top 0 :behavior "smooth"}))

(defn go-to-oao-sso! []
  (swap! app-state assoc :page :oao-sso)
  (.scrollTo js/window #js {:top 0 :behavior "smooth"}))

(defn go-to-oao-new-enroll! []
  (swap! app-state assoc :page :oao-new-enroll)
  (.scrollTo js/window #js {:top 0 :behavior "smooth"}))

(defn go-to-oao-ob-login! []
  (swap! app-state assoc :page :oao-ob-login)
  (.scrollTo js/window #js {:top 0 :behavior "smooth"}))

(defn go-to-landing! []
  (swap! app-state assoc :page :landing)
  (.scrollTo js/window #js {:top 0 :behavior "smooth"}))

(defn update-form-data! [updates]
  (swap! app-state update :form-data merge updates))

(defn go-next! []
  (when (< (:current-step @app-state) (count steps))
    (swap! app-state update :current-step inc)
    (.scrollTo js/window #js {:top 0 :behavior "smooth"})))

(defn go-back! []
  (when (> (:current-step @app-state) 1)
    (swap! app-state update :current-step dec)
    (.scrollTo js/window #js {:top 0 :behavior "smooth"})))

(defn go-to-step! [step]
  (swap! app-state assoc :current-step step)
  (.scrollTo js/window #js {:top 0 :behavior "smooth"}))

(defn submit! []
  (swap! app-state assoc :submitted true)
  (.scrollTo js/window #js {:top 0 :behavior "smooth"}))

(defn start-over! []
  (reset! app-state {:page :landing
                     :current-step 1
                     :form-data initial-data
                     :submitted false}))
