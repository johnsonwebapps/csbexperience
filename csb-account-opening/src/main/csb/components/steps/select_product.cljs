(ns csb.components.steps.select-product
  (:require [reagent.core :as r]
            [csb.state :as state]))

(def products
  [{:id "small-business-checking"
    :category "Checking"
    :name "Small Business Checking"
    :tagline "Everyday banking made simple"
    :icon "🏦"
    :features ["No monthly maintenance fee with $1,500 avg. daily balance"
               "Unlimited electronic transactions"
               "Free online and mobile banking"
               "Free first order of business checks"
               "Debit Mastercard® included"]
    :min-deposit "$100"
    :apy nil
    :popular true}
   {:id "business-growth-checking"
    :category "Checking"
    :name "Business Growth Checking"
    :tagline "Built for growing businesses"
    :icon "💰"
    :features ["Earn interest on your balance"
               "Unlimited electronic transactions"
               "Free online and mobile banking"
               "Debit Mastercard® included"
               "Ideal for partnerships & LLCs"]
    :min-deposit "$500"
    :apy "Up to 0.10%"
    :popular false}
   {:id "small-business-money-market-special"
    :category "Money Market"
    :name "Small Business Money Market Special"
    :tagline "Higher yields for larger balances"
    :icon "📊"
    :features ["Tiered interest rates – earn more as balances grow"
               "Check writing and debit access"
               "Free online and mobile banking"
               "Ideal for operating reserves"
               "FDIC insured up to $250,000"]
    :min-deposit "$2,500"
    :apy "Up to 0.40%"
    :popular false}
   {:id "4-month-cd-special"
    :category "CD"
    :name "4 Month Certificate of Deposit Special"
    :tagline "Lock in a guaranteed rate"
    :icon "📅"
    :features ["4 month term with special promotional rate"
               "Guaranteed fixed rate for the full term"
               "FDIC insured up to $250,000"
               "Automatic renewal option"
               "Great for surplus funds you won't need immediately"]
    :min-deposit "$1,000"
    :apy "Up to 4.50%"
    :popular false}])

(def categories ["All" "Checking" "Money Market" "CD"])

(defn product-card [product selected]
  [:div {:class (str "product-card relative cursor-pointer "
                    (when (= selected (:id product)) "selected"))
         :role "radio"
         :aria-checked (= selected (:id product))
         :tab-index 0
         :on-click #(state/update-form-data! {:selected-product (:id product)})
         :on-key-down #(when (or (= (.-key %) "Enter") (= (.-key %) " "))
                        (state/update-form-data! {:selected-product (:id product)}))}
   (when (:popular product)
     [:div.absolute.top-3.right-3.text-xs.font-bold.px-3.py-1.rounded-full
      {:style {:background-color "#00857c" :color "white"}}
      "Most Popular"])
   [:div.flex.items-start.gap-4
    [:div.text-3xl.flex-shrink-0.mt-0.5 (:icon product)]
    [:div.flex-1.min-w-0
     [:div.flex.items-start.justify-between.gap-2
      [:div
       [:span.text-xs.text-csb-teal.font-semibold.uppercase.tracking-wide (:category product)]
       [:h3.font-bold.text-gray-900.text-lg.leading-tight (:name product)]
       [:p.text-sm.text-gray-500 (:tagline product)]]
      [:div {:class (str "mt-1 w-5 h-5 rounded-full border-2 flex items-center justify-center flex-shrink-0 transition-all "
                        (if (= selected (:id product))
                          "border-csb-teal"
                          "border-gray-300"))}
       (when (= selected (:id product))
         [:div.w-2.5.h-2.5.rounded-full.bg-csb-teal])]]
     [:div.flex.gap-6.mt-3.mb-3.text-sm
      [:div
       [:span.text-gray-400.text-xs.uppercase.tracking-wide "Min. Deposit"]
       [:div.font-bold {:style {:color "#333"}} (:min-deposit product)]]
      (when (:apy product)
        [:div
         [:span.text-gray-400.text-xs.uppercase.tracking-wide "APY"]
         [:div.font-bold.text-csb-teal (:apy product)]])]
     [:ul.space-y-1
      (for [f (:features product)]
        ^{:key f}
        [:li.flex.items-start.gap-2.text-sm.text-gray-600
         [:span.text-csb-teal.font-bold.flex-shrink-0.mt-0.5 "✓"]
         f])]]]])

(defn select-product-step [form-data]
  (let [filter-state (r/atom "All")
        error (r/atom "")]
    (fn [form-data]
      (let [selected (:selected-product form-data)
            filtered (filter #(or (= @filter-state "All")
                                  (= (:category %) @filter-state))
                            products)]
        [:div.space-y-6
         [:div.card
          [:h2.section-header "Choose Your Account"]
          [:p.text-gray-500.text-sm
           "Select the account that best fits your business needs. You can open additional accounts later."]]

         ;; Filter tabs
         [:div.flex.gap-2.flex-wrap
          (for [cat categories]
            ^{:key cat}
            [:button {:class (str "px-4 py-1.5 rounded-full text-sm font-semibold transition-all "
                                 (if (= @filter-state cat)
                                   "bg-csb-teal text-white"
                                   "bg-white text-gray-600 border border-gray-200 hover:border-csb-teal hover:text-csb-teal"))
                      :on-click #(reset! filter-state cat)}
             cat])]

         ;; Product cards
         [:div.grid.gap-4
          (for [product filtered]
            ^{:key (:id product)}
            [product-card product selected])]

         (when (seq @error)
           [:p.error-text @error])

         [:div.flex.justify-between.gap-3
          [:button.btn-secondary {:on-click state/go-back!} "← Back"]
          [:button.btn-primary
           {:on-click #(if (empty? selected)
                        (reset! error "Please select an account type to continue.")
                        (do (reset! error "")
                            (state/go-next!)))}
           "Continue →"]]]))))
