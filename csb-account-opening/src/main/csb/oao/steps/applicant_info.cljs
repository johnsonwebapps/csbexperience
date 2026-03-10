(ns csb.oao.steps.applicant-info
  (:require [reagent.core :as r]
            [csb.oao.state :as state]))

;; Applicant info step for Q2-BUS-035A (new customer flow)

(defn text-field [label field-key form-data & [{:keys [placeholder type]}]]
  [:div
   [:label.block.text-sm.font-medium.text-gray-700.mb-1 label]
   [:input.w-full.px-3.py-2.border.border-gray-300.rounded-lg.focus:outline-none
    {:type (or type "text")
     :placeholder (or placeholder "")
     :value (get form-data field-key "")
     :on-change #(state/update-form-data! {field-key (.. % -target -value)})}]])

(defn applicant-info-step []
  (let [{:keys [form-data]} @state/app-state]
    [:div.space-y-6
     [:div.bg-white.rounded-xl.p-6 {:style {:box-shadow "0 2px 12px rgba(0,0,0,0.08)"}}
      [:h2.text-xl.font-bold.mb-6 {:style {:color "#333"}} "Applicant Information"]
      [:div.space-y-4
       [:div.grid.grid-cols-2.gap-4
        [text-field "First Name *" :first-name form-data]
        [text-field "Last Name *" :last-name form-data]]
       
       [:div.grid.grid-cols-2.gap-4
        [text-field "Email Address *" :email form-data {:type "email"}]
        [text-field "Phone Number *" :phone form-data {:type "tel"}]]
       
       [:div.grid.grid-cols-2.gap-4
        [text-field "Date of Birth *" :dob form-data {:type "date"}]
        [text-field "SSN *" :ssn form-data {:placeholder "XXX-XX-XXXX"}]]
       
       [:div
        [:label.block.text-sm.font-medium.text-gray-700.mb-1 "Home Address *"]
        [text-field "" :address form-data {:placeholder "Street Address"}]]
       [:div.grid.grid-cols-3.gap-4
        [text-field "City *" :city form-data]
        [text-field "State *" :state form-data]
        [text-field "ZIP Code *" :zip form-data]]]]
     
     ;; Navigation
     [:div.flex.justify-between
      [:button.py-3.px-6.rounded-lg.font-semibold
       {:style {:color "#00857c" :border "1px solid #00857c"}
        :on-click #(state/go-back!)}
       "← Back"]
      [:button.py-3.px-8.rounded-lg.font-semibold.text-white
       {:style {:background-color "#00857c"}
        :on-click #(state/go-next!)}
       "Continue →"]]]))
