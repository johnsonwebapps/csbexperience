(ns csb.unified.dashboard
  (:require [reagent.core :as r]
            [csb.unified.state :as state]
            [csb.storage :as storage]
            [csb.state :as main-state]))

(defn format-date [iso-string]
  (when iso-string
    (.toLocaleDateString (js/Date. iso-string)
                         "en-US"
                         #js {:month "short"
                              :day "numeric"
                              :year "numeric"
                              :hour "2-digit"
                              :minute "2-digit"})))

(defn get-flow-label [flow-type]
  (case flow-type
    :account-only "Account Opening"
    :loan-only "Loan Application"
    :loan-and-account "Loan + Account"
    "Application"))

(defn get-status-badge [status loan-decision]
  (cond
    (= status :draft)
    [:span.px-2.py-1.rounded-full.text-xs.font-medium
     {:style {:background-color "#fef3c7" :color "#92400e"}}
     "Draft"]
    
    (= loan-decision :approved)
    [:span.px-2.py-1.rounded-full.text-xs.font-medium
     {:style {:background-color "#d1fae5" :color "#065f46"}}
     "Approved"]
    
    (= loan-decision :denied)
    [:span.px-2.py-1.rounded-full.text-xs.font-medium
     {:style {:background-color "#fee2e2" :color "#991b1b"}}
     "Denied"]
    
    (= loan-decision :pending-review)
    [:span.px-2.py-1.rounded-full.text-xs.font-medium
     {:style {:background-color "#dbeafe" :color "#1e40af"}}
     "Pending Review"]
    
    (= status :submitted)
    [:span.px-2.py-1.rounded-full.text-xs.font-medium
     {:style {:background-color "#d1fae5" :color "#065f46"}}
     "Submitted"]
    
    :else
    [:span.px-2.py-1.rounded-full.text-xs.font-medium
     {:style {:background-color "#f3f4f6" :color "#6b7280"}}
     "Unknown"]))

(defn application-card [app on-continue on-delete]
  (let [{:keys [id status form-data created-at updated-at]} app
        {:keys [flow-type business-legal-name owner-first-name owner-last-name 
                loan-amount loan-decision]} form-data
        is-draft (= status :draft)]
    [:div.bg-white.rounded-lg.border.p-4.hover:shadow-md.transition-shadow
     {:style {:border-color "#e5e7eb"}}
     [:div.flex.items-start.justify-between.mb-3
      [:div
       [:div.flex.items-center.gap-2.mb-1
        [:span.font-semibold.text-gray-900 
         (or business-legal-name "New Application")]
        [get-status-badge status loan-decision]]
       [:p.text-sm.text-gray-500 (get-flow-label flow-type)]]
      [:div.text-right.text-xs.text-gray-400
       [:div (str "ID: " id)]]]
     
     ;; Details
     [:div.grid.grid-cols-3.gap-4.text-sm.mb-4
      [:div
       [:span.text-gray-500 "Applicant: "]
       [:span.font-medium 
        (if (and owner-first-name owner-last-name)
          (str owner-first-name " " owner-last-name)
          "—")]]
      (when loan-amount
        [:div
         [:span.text-gray-500 "Loan Amount: "]
         [:span.font-medium (str "$" loan-amount)]])
      [:div
       [:span.text-gray-500 "Updated: "]
       [:span.font-medium (format-date (or updated-at created-at))]]]
     
     ;; Actions
     [:div.flex.items-center.gap-2.pt-3.border-t
      (if is-draft
        [:button.flex-1.py-2.px-4.rounded-lg.font-medium.text-white.transition-colors
         {:style {:background-color "#00857c"}
          :on-click #(on-continue id)}
         "Continue Application →"]
        [:button.flex-1.py-2.px-4.rounded-lg.font-medium.transition-colors
         {:style {:color "#00857c" :border "1px solid #00857c"}
          :on-click #(on-continue id)}
         "View Details"])
      [:button.py-2.px-4.rounded-lg.text-red-600.hover:bg-red-50.transition-colors
       {:on-click #(when (js/confirm "Are you sure you want to delete this application?")
                     (on-delete id))}
       "Delete"]]]))

(defn empty-state []
  [:div.text-center.py-12
   [:div.text-6xl.mb-4 "📋"]
   [:h3.text-xl.font-semibold.text-gray-900.mb-2 "No Applications Yet"]
   [:p.text-gray-600.mb-6 "Start a new application to see it here."]
   [:button.font-bold.py-3.px-8.rounded-lg.text-white.transition-all
    {:style {:background-color "#00857c"}
     :on-click #(do (state/start-over!)
                    (main-state/go-to-unified-application!))}
    "Start New Application"]])

(defn stats-cards [stats]
  [:div.grid.grid-cols-4.gap-4.mb-8
   [:div.bg-white.rounded-lg.p-4.text-center.border
    [:div.text-3xl.font-bold {:style {:color "#00857c"}} (:total stats)]
    [:div.text-sm.text-gray-600 "Total Applications"]]
   [:div.bg-white.rounded-lg.p-4.text-center.border
    [:div.text-3xl.font-bold.text-yellow-600 (:drafts stats)]
    [:div.text-sm.text-gray-600 "Drafts"]]
   [:div.bg-white.rounded-lg.p-4.text-center.border
    [:div.text-3xl.font-bold.text-green-600 (:approved stats)]
    [:div.text-sm.text-gray-600 "Approved"]]
   [:div.bg-white.rounded-lg.p-4.text-center.border
    [:div.text-3xl.font-bold.text-blue-600 (:submitted stats)]
    [:div.text-sm.text-gray-600 "Submitted"]]])

(defn file-sync-controls [_on-refresh]
  (let [importing? (r/atom false)
        import-status (r/atom nil)]
    (fn [on-refresh]
      [:div.bg-blue-50.rounded-lg.p-4.mb-6.border.border-blue-200
       [:div.flex.items-start.gap-3
        [:div.text-2xl "📁"]
        [:div.flex-1
         [:h3.font-semibold.text-gray-900.mb-1 "Sync Data via GitHub"]
         [:p.text-sm.text-gray-600.mb-3
          "Export your applications to save them to the project, then commit to GitHub. "
          "Import on other computers to load the shared data."]
         [:div.flex.flex-wrap.gap-2
          ;; Export button
          [:button.px-4.py-2.text-sm.font-medium.rounded-lg.bg-white.border.border-gray-300.hover:bg-gray-50.flex.items-center.gap-2
           {:on-click storage/export-to-file!}
           [:span "📤"] "Export Data"]
          
          ;; Import button (file picker)
          [:label.px-4.py-2.text-sm.font-medium.rounded-lg.bg-white.border.border-gray-300.hover:bg-gray-50.flex.items-center.gap-2.cursor-pointer
           [:span "📥"] "Import Data"
           [:input.hidden
            {:type "file"
             :accept ".edn"
             :on-change (fn [e]
                          (when-let [file (-> e .-target .-files (aget 0))]
                            (reset! importing? true)
                            (reset! import-status nil)
                            (let [reader (js/FileReader.)]
                              (set! (.-onload reader)
                                    (fn [_]
                                      (storage/import-from-content!
                                       (.-result reader)
                                       (fn [success?]
                                         (reset! importing? false)
                                         (reset! import-status (if success? :success :error))
                                         (when success? (on-refresh))
                                         (js/setTimeout #(reset! import-status nil) 3000)))))
                              (.readAsText reader file))))}]]
          
          (when @importing?
            [:span.text-sm.text-gray-500 "Importing..."])
          (when (= @import-status :success)
            [:span.text-sm.text-green-600 "✓ Imported successfully!"])
          (when (= @import-status :error)
            [:span.text-sm.text-red-600 "✗ Import failed"])]]
        [:div.text-xs.text-gray-500 {:style {:min-width "200px"}}
         [:div.font-medium.mb-1 "Move exported file to:"]
         [:div.font-mono.text-xs.break-all {:style {:color "#00857c"}}
          "csb-account-opening/resources/public/data/applications.edn"]
         [:div.mt-2.text-gray-400 "Then: git add, commit, push"]]]])))

(defn dashboard []
  (let [apps-atom (r/atom (storage/get-all-applications))
        refresh! #(reset! apps-atom (storage/get-all-applications))]
    (fn []
      (let [apps @apps-atom
            stats (storage/get-stats)
            on-continue (fn [app-id]
                          (state/load-application! app-id)
                          (main-state/go-to-unified-application!))
            on-delete (fn [app-id]
                        (state/delete-app! app-id)
                        (refresh!))]
        [:div.min-h-screen.flex.flex-col {:style {:background-color "#f0f5f4"}}
         ;; Header
         [:header {:style {:background-color "#00857c" :padding "0.75rem 0"}}
          [:div.container.mx-auto.px-4.flex.items-center.justify-between
           [:div.flex.items-center.gap-2.cursor-pointer
            {:on-click main-state/go-to-landing!}
            [:img {:src "/images/header.png" 
                   :alt "Cambridge Savings Bank"
                   :style {:height "40px"}}]]
           [:div.text-white.text-sm.flex.items-center.gap-4
            [:span "Need Help?"]
            [:a.font-medium.hover:underline {:href "tel:1-888-418-5626"} "1-888-418-5626"]]]]
         
         ;; Main content
         [:main.flex-1.container.mx-auto.px-4.py-8 {:style {:max-width "1000px"}}
          ;; Header with back button
          [:div.flex.items-center.justify-between.mb-6
           [:div
            [:button.text-sm.mb-2.flex.items-center.gap-1
             {:style {:color "#00857c"}
              :on-click main-state/go-to-landing!}
             "← Back to Home"]
            [:h1.text-2xl.font-bold.text-gray-900 "My Applications"]]
           [:button.font-bold.py-2.px-6.rounded-lg.text-white.transition-all
            {:style {:background-color "#00857c"}
             :on-click #(do (state/start-over!)
                            (main-state/go-to-unified-application!))}
            "+ New Application"]]
          
          ;; Stats
          [stats-cards stats]
          
          ;; File sync controls
          [file-sync-controls refresh!]
          
          ;; Applications list
          (if (empty? apps)
            [empty-state]
            [:div.space-y-4
             ;; Drafts section
             (let [drafts (filter #(= (:status %) :draft) apps)]
               (when (seq drafts)
                 [:div
                  [:h2.font-semibold.text-lg.text-gray-700.mb-3 
                   (str "Drafts (" (count drafts) ")")]
                  [:div.space-y-3
                   (for [app (sort-by :updated-at > drafts)]
                     ^{:key (:id app)}
                     [application-card app on-continue on-delete])]]))
             
             ;; Submitted section
             (let [submitted (filter #(= (:status %) :submitted) apps)]
               (when (seq submitted)
                 [:div.mt-8
                  [:h2.font-semibold.text-lg.text-gray-700.mb-3 
                   (str "Submitted (" (count submitted) ")")]
                  [:div.space-y-3
                   (for [app (sort-by :submitted-at > submitted)]
                     ^{:key (:id app)}
                     [application-card app on-continue on-delete])]]))])]
         
         ;; Footer
         [:footer.py-4.text-center.text-sm.text-gray-500
          {:style {:background-color "#f0f5f4"
                   :border-top "1px solid #e5e7eb"}}
          [:div.container.mx-auto.px-4
           [:p "© 2024 Cambridge Savings Bank. Member FDIC. Equal Housing Lender."]]]]))))
