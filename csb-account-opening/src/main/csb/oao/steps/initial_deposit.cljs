(ns csb.oao.steps.initial-deposit
  (:require [reagent.core :as r]
            [csb.oao.state :as state]))

;; Initial deposit / fund transfer step

(defn text-field [label field-key form-data & [{:keys [placeholder type]}]]
  [:div
   [:label.block.text-sm.font-medium.text-gray-700.mb-1 label]
   [:input.w-full.px-3.py-2.border.border-gray-300.rounded-lg.focus:outline-none
    {:type (or type "text")
     :placeholder (or placeholder "")
     :value (get form-data field-key "")
     :on-change #(state/update-form-data! {field-key (.. % -target -value)})}]])

(defn initial-deposit-step []
  (let [{:keys [form-data]} @state/app-state
        fund-source (:fund-source form-data)
        entry-type (:entry-type form-data)
        has-existing-account (or (= entry-type :sso) (= entry-type :ob-login))]
    [:div.space-y-6
     [:div.bg-white.rounded-xl.p-6 {:style {:box-shadow "0 2px 12px rgba(0,0,0,0.08)"}}
      [:h2.text-xl.font-bold.mb-2 {:style {:color "#333"}} "Initial Deposit"]
      [:p.text-sm.text-gray-500.mb-6 "Fund your new account with an initial deposit."]
      
      ;; Fund source selection
      [:div.space-y-3.mb-6
       (when has-existing-account
         [:div.rounded-lg.p-4.cursor-pointer.transition-all
          {:style {:border (if (= fund-source "existing-account")
                            "2px solid #00857c"
                            "1px solid #e5e7eb")}
           :on-click #(state/update-form-data! {:fund-source "existing-account"})}
          [:div.flex.items-center.gap-3
           [:div.w-5.h-5.rounded-full.border-2.flex.items-center.justify-center
            {:style {:border-color (if (= fund-source "existing-account") "#00857c" "#d1d5db")}}
            (when (= fund-source "existing-account")
              [:div.w-3.h-3.rounded-full {:style {:background-color "#00857c"}}])]
           [:div
            [:span.font-medium.text-sm "Transfer from Existing CSB Account"]
            [:p.text-xs.text-gray-500 "Move funds from one of your linked accounts"]]]])
       
       [:div.rounded-lg.p-4.cursor-pointer.transition-all
        {:style {:border (if (= fund-source "external")
                          "2px solid #00857c"
                          "1px solid #e5e7eb")}
         :on-click #(state/update-form-data! {:fund-source "external"})}
        [:div.flex.items-center.gap-3
         [:div.w-5.h-5.rounded-full.border-2.flex.items-center.justify-center
          {:style {:border-color (if (= fund-source "external") "#00857c" "#d1d5db")}}
          (when (= fund-source "external")
            [:div.w-3.h-3.rounded-full {:style {:background-color "#00857c"}}])]
         [:div
          [:span.font-medium.text-sm "Transfer from External Account"]
          [:p.text-xs.text-gray-500 "Provide routing and account number for ACH transfer"]]]]]
      
      ;; Details based on selection
      (when (= fund-source "existing-account")
        [:div.space-y-4.mt-4.pt-4 {:style {:border-top "1px solid #e5e7eb"}}
         [:div
          [:label.block.text-sm.font-medium.text-gray-700.mb-1 "Select Source Account"]
          [:select.w-full.px-3.py-2.border.border-gray-300.rounded-lg
           {:value (:source-account form-data "")
            :on-change #(state/update-form-data! {:source-account (.. % -target -value)})}
           [:option {:value ""} "Select an account..."]
           [:option {:value "checking-1234"} "Business Checking ****1234"]
           [:option {:value "savings-5678"} "Business Savings ****5678"]
           [:option {:value "mm-9012"} "Money Market ****9012"]]]
         [text-field "Deposit Amount *" :deposit-amount form-data {:placeholder "$0.00"}]])
      
      (when (= fund-source "external")
        [:div.space-y-4.mt-4.pt-4 {:style {:border-top "1px solid #e5e7eb"}}
         [:div.grid.grid-cols-2.gap-4
          [text-field "Routing Number *" :routing-number form-data {:placeholder "9 digits"}]
          [text-field "Account Number *" :account-number form-data]]
         [text-field "Deposit Amount *" :deposit-amount form-data {:placeholder "$0.00"}]])]
     
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
