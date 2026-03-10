(ns csb.oao.steps.review-info
  (:require [reagent.core :as r]
            [csb.oao.state :as state]))

;; Review pre-filled information step (used by SSO and OB-login flows)

(defn field-row [label value & [editable?]]
  [:div.flex.items-center.justify-between.py-2 {:style {:border-bottom "1px solid #f0f0f0"}}
   [:span.text-sm.text-gray-500 label]
   [:span.text-sm.font-medium {:style {:color "#333"}} value]])

(defn review-info-step []
  (let [editing (r/atom false)]
    (fn []
      (let [{:keys [form-data]} @state/app-state
            {:keys [first-name last-name email phone ssn dob
                    address city state zip
                    business-legal-name dba-name business-type ein
                    state-of-formation date-established business-phone
                    business-address business-city business-state business-zip
                    business-description]} form-data]
        [:div.space-y-6
         [:div.bg-white.rounded-xl.p-6 {:style {:box-shadow "0 2px 12px rgba(0,0,0,0.08)"}}
          [:div.flex.items-center.justify-between.mb-4
           [:h2.text-xl.font-bold {:style {:color "#333"}} "Personal Information"]
           [:button.text-sm.font-medium.px-3.py-1.rounded
            {:style {:color "#00857c" :border "1px solid #00857c"}
             :on-click #(swap! editing not)}
            (if @editing "Done Editing" "Edit")]]
          
          (if @editing
            ;; Edit mode
            [:div.space-y-4
             [:div.grid.grid-cols-2.gap-4
              [:div
               [:label.block.text-sm.font-medium.text-gray-700.mb-1 "First Name"]
               [:input.w-full.px-3.py-2.border.border-gray-300.rounded-lg
                {:value first-name
                 :on-change #(state/update-form-data! {:first-name (.. % -target -value)})}]]
              [:div
               [:label.block.text-sm.font-medium.text-gray-700.mb-1 "Last Name"]
               [:input.w-full.px-3.py-2.border.border-gray-300.rounded-lg
                {:value last-name
                 :on-change #(state/update-form-data! {:last-name (.. % -target -value)})}]]]
             [:div.grid.grid-cols-2.gap-4
              [:div
               [:label.block.text-sm.font-medium.text-gray-700.mb-1 "Email"]
               [:input.w-full.px-3.py-2.border.border-gray-300.rounded-lg
                {:value email
                 :on-change #(state/update-form-data! {:email (.. % -target -value)})}]]
              [:div
               [:label.block.text-sm.font-medium.text-gray-700.mb-1 "Phone"]
               [:input.w-full.px-3.py-2.border.border-gray-300.rounded-lg
                {:value phone
                 :on-change #(state/update-form-data! {:phone (.. % -target -value)})}]]]
             [:div
              [:label.block.text-sm.font-medium.text-gray-700.mb-1 "Address"]
              [:input.w-full.px-3.py-2.border.border-gray-300.rounded-lg
               {:value address
                :on-change #(state/update-form-data! {:address (.. % -target -value)})}]]
             [:div.grid.grid-cols-3.gap-4
              [:div
               [:label.block.text-sm.font-medium.text-gray-700.mb-1 "City"]
               [:input.w-full.px-3.py-2.border.border-gray-300.rounded-lg
                {:value city
                 :on-change #(state/update-form-data! {:city (.. % -target -value)})}]]
              [:div
               [:label.block.text-sm.font-medium.text-gray-700.mb-1 "State"]
               [:input.w-full.px-3.py-2.border.border-gray-300.rounded-lg
                {:value state
                 :on-change #(state/update-form-data! {:state (.. % -target -value)})}]]
              [:div
               [:label.block.text-sm.font-medium.text-gray-700.mb-1 "ZIP"]
               [:input.w-full.px-3.py-2.border.border-gray-300.rounded-lg
                {:value zip
                 :on-change #(state/update-form-data! {:zip (.. % -target -value)})}]]]]
            ;; View mode
            [:div
             [field-row "Name" (str first-name " " last-name)]
             [field-row "Email" email]
             [field-row "Phone" phone]
             [field-row "SSN" ssn]
             [field-row "Date of Birth" dob]
             [field-row "Address" (str address ", " city ", " state " " zip)]])]
         
         [:div.bg-white.rounded-xl.p-6 {:style {:box-shadow "0 2px 12px rgba(0,0,0,0.08)"}}
          [:h2.text-xl.font-bold.mb-4 {:style {:color "#333"}} "Business Information"]
          [:div
           [field-row "Legal Name" business-legal-name]
           [field-row "DBA" dba-name]
           [field-row "Business Type" business-type]
           [field-row "EIN" ein]
           [field-row "State of Formation" state-of-formation]
           [field-row "Date Established" date-established]
           [field-row "Phone" business-phone]
           [field-row "Address" (str business-address ", " business-city ", " business-state " " business-zip)]
           [field-row "Description" business-description]]]
         
         [:div.rounded-lg.p-4 {:style {:background-color "#FFF7ED" :border "1px solid #FED7AA"}}
          [:div.flex.items-start.gap-2
           [:span "ℹ️"]
           [:p.text-sm {:style {:color "#9A3412"}}
            "This information was pre-filled from your Online Banking profile. "
            "Please review for accuracy and click Edit to make any changes before proceeding."]]]
         
         ;; Navigation
         [:div.flex.justify-between
          [:button.py-3.px-6.rounded-lg.font-semibold
           {:style {:color "#00857c" :border "1px solid #00857c"}
            :on-click #(state/go-back!)}
           "← Back"]
          [:button.py-3.px-8.rounded-lg.font-semibold.text-white
           {:style {:background-color "#00857c"}
            :on-click #(state/go-next!)}
           "Continue →"]]]))))
