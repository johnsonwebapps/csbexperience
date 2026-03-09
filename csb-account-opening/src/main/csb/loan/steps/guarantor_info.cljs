(ns csb.loan.steps.guarantor-info
  (:require [reagent.core :as r]
            [clojure.string :as str]
            [csb.loan.state :as state]
            [csb.utils :as utils]
            [csb.components.ui.form-field :refer [form-field]]))

(def empty-guarantor
  {:first-name "" :last-name "" :title "" :ownership-pct ""
   :email "" :phone "" :ssn "" :dob ""
   :address "" :city "" :state "" :zip ""
   :years-at-address "" :housing-status "" :monthly-housing-payment ""
   :annual-income "" :other-income "" :other-income-source ""
   :credit-score-estimate ""})

(defn guarantor-form [guarantor index errors on-update on-remove]
  (let [show-ssn (r/atom false)]
    (fn [guarantor index errors on-update on-remove]
      (let [set-field (fn [field]
                        (fn [e]
                          (on-update index (assoc guarantor field (.. e -target -value)))))]
        [:div.border.border-gray-200.rounded-xl.p-5.bg-gray-50.space-y-4
         [:div.flex.items-center.justify-between.mb-1
          [:h4.font-bold {:style {:color "#00857c"}} (str "Additional Guarantor #" (inc index))]
          [:button.text-red-500.hover:text-red-700.text-sm.font-medium.flex.items-center.gap-1
           {:type "button" :on-click #(on-remove index)}
           "Remove"]]
         
         [:div.grid.sm:grid-cols-2.gap-4
          [form-field {:label "First Name" :required true}
           [:input.form-input {:type "text"
                               :value (:first-name guarantor)
                               :on-change (set-field :first-name)}]]
          [form-field {:label "Last Name" :required true}
           [:input.form-input {:type "text"
                               :value (:last-name guarantor)
                               :on-change (set-field :last-name)}]]]
         
         [:div.grid.sm:grid-cols-2.gap-4
          [form-field {:label "Title / Role"}
           [:input.form-input {:type "text"
                               :placeholder "e.g., Co-Owner, Partner"
                               :value (:title guarantor)
                               :on-change (set-field :title)}]]
          [form-field {:label "Ownership %" :required true}
           [:input.form-input {:type "number" :min 0 :max 100
                               :value (:ownership-pct guarantor)
                               :on-change (set-field :ownership-pct)}]]]
         
         [:div.grid.sm:grid-cols-2.gap-4
          [form-field {:label "SSN" :required true}
           [:div.relative
            [:input.form-input {:type (if @show-ssn "text" "password")
                                :placeholder "XXX-XX-XXXX"
                                :value (:ssn guarantor)
                                :on-change #(on-update index 
                                              (assoc guarantor :ssn 
                                                     (utils/format-ssn (.. % -target -value))))}]
            [:button.absolute.right-3.text-gray-400.text-xs.font-medium
             {:type "button"
              :style {:top "50%" :transform "translateY(-50%)"}
              :on-click #(swap! show-ssn not)}
             (if @show-ssn "Hide" "Show")]]]
          [form-field {:label "Date of Birth" :required true}
           [:input.form-input {:type "date"
                               :value (:dob guarantor)
                               :on-change (set-field :dob)}]]]
         
         [:div.grid.sm:grid-cols-2.gap-4
          [form-field {:label "Email"}
           [:input.form-input {:type "email"
                               :value (:email guarantor)
                               :on-change (set-field :email)}]]
          [form-field {:label "Phone"}
           [:input.form-input {:type "tel"
                               :value (:phone guarantor)
                               :on-change (set-field :phone)}]]]
         
         [form-field {:label "Home Address" :required true}
          [:input.form-input {:type "text"
                              :value (:address guarantor)
                              :on-change (set-field :address)}]]
         
         [:div.grid.sm:grid-cols-3.gap-4
          [form-field {:label "City"}
           [:input.form-input {:type "text"
                               :value (:city guarantor)
                               :on-change (set-field :city)}]]
          [form-field {:label "State"}
           [:select.form-input {:value (:state guarantor)
                                :on-change (set-field :state)}
            [:option {:value ""} "..."]
            (for [s utils/us-states]
              ^{:key s}
              [:option {:value s} s])]]
          [form-field {:label "ZIP"}
           [:input.form-input {:type "text"
                               :value (:zip guarantor)
                               :on-change (set-field :zip)}]]]
         
         [form-field {:label "Annual Income"}
          [:div.relative
           [:span.absolute.left-3.text-gray-500 {:style {:top "50%" :transform "translateY(-50%)"}} "$"]
           [:input.form-input.pl-7 {:type "text"
                                     :value (:annual-income guarantor)
                                     :on-change (set-field :annual-income)}]]]]))))

(defn guarantor-info-step [form-data]
  (let [errors (r/atom {})
        guarantors (r/atom (or (:guarantors form-data) []))
        show-ssn (r/atom false)]
    (fn [form-data]
      (let [validate (fn []
                       (cond-> {}
                         (str/blank? (:primary-first-name form-data))
                         (assoc :primary-first-name "First name is required.")
                         
                         (str/blank? (:primary-last-name form-data))
                         (assoc :primary-last-name "Last name is required.")
                         
                         (str/blank? (:primary-ssn form-data))
                         (assoc :primary-ssn "SSN is required for credit check.")
                         
                         (str/blank? (:primary-dob form-data))
                         (assoc :primary-dob "Date of birth is required.")
                         
                         (str/blank? (:primary-address form-data))
                         (assoc :primary-address "Address is required.")
                         
                         (str/blank? (:primary-ownership-pct form-data))
                         (assoc :primary-ownership-pct "Ownership percentage is required.")))
            handle-next (fn []
                          (let [e (validate)]
                            (reset! errors e)
                            (state/update-form-data! {:guarantors @guarantors})
                            (when (empty? e)
                              (state/go-next!))))
            set-field (fn [field]
                        (fn [e]
                          (state/update-form-data! {field (.. e -target -value)})))
            add-guarantor (fn [] (swap! guarantors conj empty-guarantor))
            remove-guarantor (fn [idx] (swap! guarantors #(vec (concat (subvec % 0 idx) (subvec % (inc idx))))))
            update-guarantor (fn [idx g] (swap! guarantors assoc idx g))]
        [:div.space-y-6
         ;; Header
         [:div.card
          [:h2.text-xl.font-bold.mb-2 {:style {:color "#333"}} "Owners & Personal Guarantors"]
          [:p.text-gray-500.text-sm
           "We need information about business owners and anyone who will personally guarantee the loan. "
           "This information is used for credit checks and identity verification. "
           "Fields marked with " [:span.text-red-500 "*"] " are required."]]
         
         ;; Credit Check Notice
         [:div.rounded-lg.p-4 {:style {:background-color "#fff3cd" :border "1px solid #ffc107"}}
          [:div.flex.gap-3
           [:div.text-2xl "⚠️"]
           [:div
            [:h4.font-semibold.mb-1 {:style {:color "#856404"}} "Credit Check Authorization"]
            [:p.text-sm {:style {:color "#856404"}}
             "By providing your Social Security Number, you authorize Cambridge Savings Bank to obtain your credit report from one or more credit bureaus. "
             "This will result in a hard inquiry on your credit report."]]]]
         
         ;; Primary Guarantor
         [:div.card.space-y-5
          [:h3.font-bold.text-gray-700.border-b.pb-2 "Primary Applicant / Guarantor"]
          
          [:div.grid.sm:grid-cols-2.gap-5
           [form-field {:label "First Name" :required true :error (:primary-first-name @errors)}
            [:input.form-input {:type "text"
                                :value (:primary-first-name form-data)
                                :on-change (set-field :primary-first-name)}]]
           [form-field {:label "Last Name" :required true :error (:primary-last-name @errors)}
            [:input.form-input {:type "text"
                                :value (:primary-last-name form-data)
                                :on-change (set-field :primary-last-name)}]]]
          
          [:div.grid.sm:grid-cols-2.gap-5
           [form-field {:label "Title / Role in Business" :required true}
            [:input.form-input {:type "text"
                                :placeholder "e.g., Owner, CEO, Managing Member"
                                :value (:primary-title form-data)
                                :on-change (set-field :primary-title)}]]
           [form-field {:label "Ownership Percentage" :required true :error (:primary-ownership-pct @errors)}
            [:div.relative
             [:input.form-input.pr-8 {:type "number" :min 0 :max 100
                                       :value (:primary-ownership-pct form-data)
                                       :on-change (set-field :primary-ownership-pct)}]
             [:span.absolute.right-3.text-gray-500 {:style {:top "50%" :transform "translateY(-50%)"}} "%"]]]]
          
          [:div.grid.sm:grid-cols-2.gap-5
           [form-field {:label "Social Security Number" :required true :error (:primary-ssn @errors)}
            [:div.relative
             [:input.form-input {:type (if @show-ssn "text" "password")
                                 :placeholder "XXX-XX-XXXX"
                                 :value (:primary-ssn form-data)
                                 :on-change #(state/update-form-data!
                                              {:primary-ssn (utils/format-ssn (.. % -target -value))})}]
             [:button.absolute.right-3.text-gray-400.text-xs.font-medium
              {:type "button"
               :style {:top "50%" :transform "translateY(-50%)"}
               :on-click #(swap! show-ssn not)}
              (if @show-ssn "Hide" "Show")]]]
           [form-field {:label "Date of Birth" :required true :error (:primary-dob @errors)}
            [:input.form-input {:type "date"
                                :value (:primary-dob form-data)
                                :max (utils/max-dob)
                                :on-change (set-field :primary-dob)}]]]
          
          [:div.grid.sm:grid-cols-2.gap-5
           [form-field {:label "Email Address" :required true}
            [:input.form-input {:type "email"
                                :value (:primary-email form-data)
                                :on-change (set-field :primary-email)}]]
           [form-field {:label "Phone Number" :required true}
            [:input.form-input {:type "tel"
                                :value (:primary-phone form-data)
                                :on-change (set-field :primary-phone)}]]]]
         
         ;; Primary Address
         [:div.card.space-y-5
          [:h3.font-bold.text-gray-700.border-b.pb-2 "Home Address"]
          
          [form-field {:label "Street Address" :required true :error (:primary-address @errors)}
           [:input.form-input {:type "text"
                               :value (:primary-address form-data)
                               :on-change (set-field :primary-address)}]]
          
          [:div.grid.sm:grid-cols-3.gap-5
           [form-field {:label "City" :required true}
            [:input.form-input {:type "text"
                                :value (:primary-city form-data)
                                :on-change (set-field :primary-city)}]]
           [form-field {:label "State" :required true}
            [:select.form-input {:value (:primary-state form-data)
                                 :on-change (set-field :primary-state)}
             [:option {:value ""} "State..."]
             (for [s utils/us-states]
               ^{:key s}
               [:option {:value s} s])]]
           [form-field {:label "ZIP Code" :required true}
            [:input.form-input {:type "text"
                                :value (:primary-zip form-data)
                                :on-change (set-field :primary-zip)}]]]
          
          [:div.grid.sm:grid-cols-2.gap-5
           [form-field {:label "Years at Current Address"}
            [:input.form-input {:type "number" :min 0
                                :value (:primary-years-at-address form-data)
                                :on-change (set-field :primary-years-at-address)}]]
           [form-field {:label "Housing Status"}
            [:select.form-input {:value (:primary-housing-status form-data)
                                 :on-change (set-field :primary-housing-status)}
             [:option {:value ""} "Select..."]
             [:option {:value "own"} "Own"]
             [:option {:value "rent"} "Rent"]
             [:option {:value "other"} "Other"]]]]]
         
         ;; Primary Income
         [:div.card.space-y-5
          [:h3.font-bold.text-gray-700.border-b.pb-2 "Personal Financial Information"]
          
          [:div.grid.sm:grid-cols-2.gap-5
           [form-field {:label "Annual Personal Income"}
            [:div.relative
             [:span.absolute.left-3.text-gray-500 {:style {:top "50%" :transform "translateY(-50%)"}} "$"]
             [:input.form-input.pl-7 {:type "text"
                                       :placeholder "e.g., 150,000"
                                       :value (:primary-annual-income form-data)
                                       :on-change (set-field :primary-annual-income)}]]]
           [form-field {:label "Monthly Housing Payment"}
            [:div.relative
             [:span.absolute.left-3.text-gray-500 {:style {:top "50%" :transform "translateY(-50%)"}} "$"]
             [:input.form-input.pl-7 {:type "text"
                                       :value (:primary-monthly-housing-payment form-data)
                                       :on-change (set-field :primary-monthly-housing-payment)}]]]]
          
          [form-field {:label "Estimated Credit Score"}
           [:select.form-input {:value (:primary-credit-score-estimate form-data)
                                :on-change (set-field :primary-credit-score-estimate)}
            [:option {:value ""} "Select range..."]
            [:option {:value "excellent"} "Excellent (750+)"]
            [:option {:value "good"} "Good (700-749)"]
            [:option {:value "fair"} "Fair (650-699)"]
            [:option {:value "poor"} "Below 650"]]]]
         
         ;; Additional Guarantors
         (when (seq @guarantors)
           [:div.space-y-4
            (for [[i g] (map-indexed vector @guarantors)]
              ^{:key i}
              [guarantor-form g i {} update-guarantor remove-guarantor])])
         
         [:button.w-full.border-2.border-dashed.border-gray-300.rounded-xl.py-4.font-semibold.transition-all.flex.items-center.justify-center.gap-2
          {:type "button"
           :style {:color "#00857c"}
           :on-click add-guarantor}
          [:span {:style {:font-size "20px"}} "+"]
          "Add Additional Guarantor"]
         
         ;; Navigation
         [:div.flex.justify-between.gap-3
          [:button.font-semibold.py-3.px-6.rounded-lg.transition-all
           {:style {:border "2px solid #00857c" :color "#00857c"}
            :on-click state/go-back!}
           "← Back"]
          [:button.font-bold.py-3.px-8.rounded-lg.text-white.transition-all
           {:style {:background-color "#00857c"}
            :on-click handle-next}
           "Continue →"]]]))))
