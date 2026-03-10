(ns csb.oao.steps.business-info
  (:require [reagent.core :as r]
            [csb.oao.state :as state]))

;; Business info step for Q2-BUS-035A (new customer flow)

(def business-types
  [{:value "" :label "Select business type..."}
   {:value "sole-proprietorship" :label "Sole Proprietorship"}
   {:value "llc" :label "Limited Liability Company (LLC)"}
   {:value "corporation" :label "Corporation (C-Corp or S-Corp)"}
   {:value "partnership" :label "Partnership"}
   {:value "non-profit" :label "Non-Profit Organization"}])

(defn text-field [label field-key form-data & [{:keys [placeholder type]}]]
  [:div
   [:label.block.text-sm.font-medium.text-gray-700.mb-1 label]
   [:input.w-full.px-3.py-2.border.border-gray-300.rounded-lg.focus:outline-none
    {:type (or type "text")
     :placeholder (or placeholder "")
     :value (get form-data field-key "")
     :on-change #(state/update-form-data! {field-key (.. % -target -value)})}]])

(defn business-info-step []
  (let [{:keys [form-data]} @state/app-state]
    [:div.space-y-6
     [:div.bg-white.rounded-xl.p-6 {:style {:box-shadow "0 2px 12px rgba(0,0,0,0.08)"}}
      [:h2.text-xl.font-bold.mb-6 {:style {:color "#333"}} "Business Information"]
      [:div.space-y-4
       [:div.grid.grid-cols-2.gap-4
        [text-field "Legal Business Name *" :business-legal-name form-data]
        [text-field "DBA (Doing Business As)" :dba-name form-data]]
       
       [:div.grid.grid-cols-2.gap-4
        [:div
         [:label.block.text-sm.font-medium.text-gray-700.mb-1 "Business Type *"]
         [:select.w-full.px-3.py-2.border.border-gray-300.rounded-lg
          {:value (:business-type form-data "")
           :on-change #(state/update-form-data! {:business-type (.. % -target -value)})}
          (for [{:keys [value label]} business-types]
            ^{:key value}
            [:option {:value value} label])]]
        [text-field "EIN / Tax ID *" :ein form-data {:placeholder "XX-XXXXXXX"}]]
       
       [:div.grid.grid-cols-2.gap-4
        [text-field "State of Formation *" :state-of-formation form-data]
        [text-field "Date Established *" :date-established form-data {:type "date"}]]
       
       [:div.grid.grid-cols-2.gap-4
        [text-field "Business Phone *" :business-phone form-data {:type "tel"}]
        [text-field "NAICS Code" :naics-code form-data]]
       
       [text-field "Business Description" :business-description form-data]

       [:div
        [:label.block.text-sm.font-medium.text-gray-700.mb-1 "Business Address *"]
        [text-field "" :business-address form-data {:placeholder "Street Address"}]]
       [:div.grid.grid-cols-3.gap-4
        [text-field "City *" :business-city form-data]
        [text-field "State *" :business-state form-data]
        [text-field "ZIP Code *" :business-zip form-data]]]]
     
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
