(ns csb.components.steps.beneficial-owners
  (:require [reagent.core :as r]
            [clojure.string :as str]
            [csb.state :as state]
            [csb.utils :as utils]
            [csb.components.ui.form-field :refer [form-field]]))

(def empty-owner
  {:first-name "" :last-name "" :title "" :ownership-pct ""
   :dob "" :ssn "" :email ""
   :address "" :city "" :state "" :zip ""})

(defn owner-form [owner index errors on-update on-remove]
  (let [show-ssn (r/atom false)]
    (fn [owner index errors on-update on-remove]
      (let [set-field (fn [field]
                       (fn [e]
                         (on-update index (assoc owner field (.. e -target -value)))))]
        [:div.border.border-gray-200.rounded-xl.p-5.bg-gray-50.space-y-4
         [:div.flex.items-center.justify-between.mb-1
          [:h4.font-bold.text-csb-teal (str "Beneficial Owner #" (inc index))]
          [:button.text-red-500.hover:text-red-700.text-sm.font-medium.flex.items-center.gap-1
           {:type "button"
            :on-click #(on-remove index)}
           [:svg.w-4.h-4 {:fill "none" :viewBox "0 0 24 24" :stroke "currentColor"}
            [:path {:stroke-linecap "round" :stroke-linejoin "round" :stroke-width 2
                   :d "M6 18L18 6M6 6l12 12"}]]
           "Remove"]]

         [:div.grid.sm:grid-cols-2.gap-4
          [form-field {:label "First Name" :required true :error (:first-name errors)}
           [:input.form-input {:type "text"
                               :placeholder "First name"
                               :value (:first-name owner)
                               :on-change (set-field :first-name)}]]
          [form-field {:label "Last Name" :required true :error (:last-name errors)}
           [:input.form-input {:type "text"
                               :placeholder "Last name"
                               :value (:last-name owner)
                               :on-change (set-field :last-name)}]]]

         [:div.grid.sm:grid-cols-2.gap-4
          [form-field {:label "Title / Role" :required true :error (:title errors)}
           [:input.form-input {:type "text"
                               :placeholder "e.g., Co-Owner, Partner, Director"
                               :value (:title owner)
                               :on-change (set-field :title)}]]
          [form-field {:label "Ownership %" :required true :error (:ownership-pct errors)
                      :hint "Must be 25% or more"}
           [:div.relative
            [:input.form-input.pr-8 {:type "number"
                                      :placeholder "e.g., 30"
                                      :min 25
                                      :max 100
                                      :value (:ownership-pct owner)
                                      :on-change (set-field :ownership-pct)}]
            [:span.absolute.right-3.text-gray-400.font-medium
             {:style {:top "50%" :transform "translateY(-50%)"}}
             "%"]]]]

         [:div.grid.sm:grid-cols-2.gap-4
          [form-field {:label "Date of Birth" :required true :error (:dob errors)}
           [:input.form-input {:type "date"
                               :value (:dob owner)
                               :max (utils/max-dob)
                               :on-change (set-field :dob)}]]
          [form-field {:label "Social Security Number" :required true :error (:ssn errors)}
           [:div.relative
            [:input.form-input.pr-12 {:type (if @show-ssn "text" "password")
                                       :placeholder "XXX-XX-XXXX"
                                       :value (:ssn owner)
                                       :on-change #(on-update index
                                                             (assoc owner :ssn
                                                                   (utils/format-ssn (.. % -target -value))))}]
            [:button.absolute.right-3.text-gray-400.hover:text-gray-600.text-xs.font-medium
             {:type "button"
              :style {:top "50%" :transform "translateY(-50%)"}
              :on-click #(swap! show-ssn not)}
             (if @show-ssn "Hide" "Show")]]]]

         [form-field {:label "Email Address" :required true :error (:email errors)}
          [:input.form-input {:type "email"
                              :placeholder "owner@example.com"
                              :value (:email owner)
                              :on-change (set-field :email)}]]

         [form-field {:label "Home Address" :required true :error (:address errors)}
          [:input.form-input {:type "text"
                              :placeholder "Street address"
                              :value (:address owner)
                              :on-change (set-field :address)}]]

         [:div.grid.sm:grid-cols-3.gap-4
          [form-field {:label "City" :required true :error (:city errors)}
           [:input.form-input {:type "text"
                               :placeholder "City"
                               :value (:city owner)
                               :on-change (set-field :city)}]]
          [form-field {:label "State" :required true :error (:state errors)}
           [:select.form-input {:value (:state owner)
                                :on-change (set-field :state)}
            [:option {:value ""} "State"]
            (for [s utils/us-states]
              ^{:key s}
              [:option {:value s} s])]]
          [form-field {:label "ZIP" :required true :error (:zip errors)}
           [:input.form-input {:type "text"
                               :placeholder "02138"
                               :value (:zip owner)
                               :max-length 10
                               :on-change (set-field :zip)}]]]]))))

(defn validate-owner [owner]
  (cond-> {}
    (str/blank? (:first-name owner)) (assoc :first-name "Required.")
    (str/blank? (:last-name owner)) (assoc :last-name "Required.")
    (str/blank? (:title owner)) (assoc :title "Required.")
    (str/blank? (:ownership-pct owner)) (assoc :ownership-pct "Required.")
    (and (not (str/blank? (:ownership-pct owner)))
         (< (js/parseFloat (:ownership-pct owner)) 25))
    (assoc :ownership-pct "Must be 25% or more.")
    (str/blank? (:dob owner)) (assoc :dob "Required.")
    (str/blank? (:ssn owner)) (assoc :ssn "Required.")
    (and (not (str/blank? (:ssn owner)))
         (not (utils/valid-ssn? (:ssn owner))))
    (assoc :ssn "Invalid SSN format.")
    (str/blank? (:email owner)) (assoc :email "Required.")
    (and (not (str/blank? (:email owner)))
         (not (utils/valid-email? (:email owner))))
    (assoc :email "Invalid email.")
    (str/blank? (:address owner)) (assoc :address "Required.")
    (str/blank? (:city owner)) (assoc :city "Required.")
    (str/blank? (:state owner)) (assoc :state "Required.")
    (str/blank? (:zip owner)) (assoc :zip "Required.")))

(defn beneficial-owners-step [form-data]
  (let [errors (r/atom {})
        owner-errors (r/atom [])]
    (fn [form-data]
      (let [owners (:beneficial-owners form-data)
            
            add-owner (fn []
                       (state/update-form-data!
                        {:beneficial-owners (conj owners empty-owner)}))
            
            update-owner (fn [index updated]
                          (state/update-form-data!
                           {:beneficial-owners (assoc owners index updated)}))
            
            remove-owner (fn [index]
                          (state/update-form-data!
                           {:beneficial-owners (vec (concat (subvec owners 0 index)
                                                           (subvec owners (inc index))))}))
            
            handle-next (fn []
                         (let [new-errors (cond-> {}
                                           (not (:certify-beneficial-owners form-data))
                                           (assoc :certify "You must certify the beneficial ownership information."))
                               o-errors (mapv validate-owner owners)
                               has-owner-errors (some #(seq %) o-errors)]
                           (reset! errors new-errors)
                           (reset! owner-errors o-errors)
                           (when (and (empty? new-errors) (not has-owner-errors))
                             (state/go-next!))))
            
            ;; Calculate total ownership
            primary-pct (or (js/parseFloat (:ownership-pct form-data)) 0)
            owner-pcts (map #(or (js/parseFloat (:ownership-pct %)) 0) owners)
            total-pct (+ primary-pct (reduce + 0 owner-pcts))]
        
        [:div.space-y-6
         [:div.card
          [:h2.section-header "Beneficial Ownership"]
          [:p.text-gray-500.text-sm
           "Federal law requires us to identify and verify the identity of all beneficial owners — individuals who own 25% or more of the business."]]

         ;; Regulation explanation
         [:div.card
          [:div.flex.gap-3
           [:div.text-2xl.flex-shrink-0 "📋"]
           [:div
            [:h3.font-bold.text-gray-800.mb-1 "FinCEN Beneficial Ownership Rule"]
            [:p.text-sm.text-gray-600.mb-3
             "Under the Customer Due Diligence (CDD) Rule, financial institutions must collect and verify information about the beneficial owners of legal entity customers. A beneficial owner is any individual who:"]
            [:ul.text-sm.text-gray-600.space-y-1.5
             [:li.flex.items-start.gap-2
              [:span.text-csb-teal.font-bold.flex-shrink-0 "•"]
              "Owns 25% or more equity interest in the legal entity, "
              [:strong "OR"]]
             [:li.flex.items-start.gap-2
              [:span.text-csb-teal.font-bold.flex-shrink-0 "•"]
              "Controls, manages, or directs the legal entity (Control Person – at least one required)"]]]]]

         ;; Primary owner summary
         [:div.card
          [:h3.font-bold.text-gray-700.mb-3.border-b.pb-2
           "Primary Applicant (Already Captured)"]
          [:div.flex.items-center.gap-3.rounded-lg.p-3
           {:style {:background-color "rgba(0, 133, 124, 0.05)"}}
           [:div.w-10.h-10.rounded-full.bg-csb-teal.flex.items-center.justify-center.text-white.font-bold.flex-shrink-0
            (if (seq (:first-name form-data))
              (str/upper-case (subs (:first-name form-data) 0 1))
              "?")]
           [:div.flex-1
            [:div.font-semibold (str (:first-name form-data) " " (:last-name form-data))]
            [:div.text-sm.text-gray-500
             (:title form-data)
             " · "
             (if (seq (:ownership-pct form-data))
               (str (:ownership-pct form-data) "% ownership")
               "Ownership not set")]]
           [:div.text-csb-teal.font-bold.text-sm "✓ Added"]]]

         ;; Ownership total
         [:div.card
          [:div.flex.items-center.justify-between
           [:span.font-semibold.text-gray-700 "Total Captured Ownership:"]
           [:span.text-lg.font-bold
            {:class (cond
                     (> total-pct 100) "text-red-600"
                     (= total-pct 100) "text-csb-teal"
                     :else "text-csb-teal")}
            (str (.toFixed total-pct 0) "%")]]
          (when (> total-pct 100)
            [:p.text-red-600.text-sm.mt-1 "Total exceeds 100%. Please check ownership percentages."])
          [:div.mt-2.h-2.bg-gray-200.rounded-full.overflow-hidden
           [:div.h-full.rounded-full.transition-all
            {:class (if (> total-pct 100) "bg-red-500" "bg-csb-teal")
             :style {:width (str (min total-pct 100) "%")}}]]]

         ;; Additional beneficial owners
         (when (seq owners)
           [:div.space-y-4
            [:h3.font-bold.text-gray-700 "Additional Beneficial Owners"]
            (for [[i owner] (map-indexed vector owners)]
              ^{:key i}
              [owner-form owner i (get @owner-errors i {}) update-owner remove-owner])])

         [:button.w-full.border-2.border-dashed.border-gray-300.rounded-xl.py-4.text-csb-teal.font-semibold.hover:border-csb-teal.transition-all.flex.items-center.justify-center.gap-2
          {:type "button"
           :style {:hover-bg "rgba(0, 133, 124, 0.05)"}
           :on-click add-owner}
          [:svg.w-5.h-5 {:fill "none" :viewBox "0 0 24 24" :stroke "currentColor"}
           [:path {:stroke-linecap "round" :stroke-linejoin "round" :stroke-width 2
                  :d "M12 4v16m8-8H4"}]]
          "Add Beneficial Owner (25%+ ownership)"]

         ;; Certification
         [:div.card
          [:label.flex.items-start.gap-3.cursor-pointer
           {:class (if (:certify-beneficial-owners form-data)
                    "bg-green-50 p-3 rounded-lg"
                    "hover:bg-gray-50 p-3 rounded-lg")}
           [:input.mt-0.5.w-5.h-5.flex-shrink-0.accent-csb-navy
            {:type "checkbox"
             :checked (:certify-beneficial-owners form-data)
             :on-change #(state/update-form-data!
                         {:certify-beneficial-owners (.. % -target -checked)})}]
           [:div
            [:div.font-semibold.text-sm.text-gray-800 "Beneficial Ownership Certification"]
            [:div.text-sm.text-gray-600.mt-0.5
             "I certify that the information provided regarding beneficial owners is complete and accurate. I understand that providing false information may subject me to legal penalties."]]]
          (when (:certify @errors)
            [:p.error-text.mt-2 (:certify @errors)])]

         [:div.flex.justify-between.gap-3
          [:button.btn-secondary {:on-click state/go-back!} "← Back"]
          [:button.btn-primary {:on-click handle-next} "Continue →"]]]))))
