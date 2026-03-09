(ns csb.loan.steps.documents
  (:require [reagent.core :as r]
            [csb.loan.state :as state]
            [csb.components.ui.form-field :refer [form-field]]))

(defn document-upload-item [{:keys [id label required]} uploaded-docs on-upload]
  (let [is-uploaded (get uploaded-docs id)]
    [:div.flex.items-center.justify-between.p-4.rounded-lg.border.transition-all
     {:class (if is-uploaded "border-[#00857c] bg-[#00857c]/5" "border-gray-200 hover:border-gray-300")}
     [:div.flex.items-center.gap-3
      [:div.w-10.h-10.rounded-full.flex.items-center.justify-center.flex-shrink-0
       {:style {:background-color (if is-uploaded "#00857c" "#e5e5e5")}}
       (if is-uploaded
         [:span.text-white "✓"]
         [:span.text-gray-500 "📄"])]
      [:div
       [:div.font-medium.text-gray-800
        label
        (when required [:span.text-red-500.ml-1 "*"])]
       (when is-uploaded
         [:div.text-xs.text-gray-500 "Document uploaded"])]]
     [:label.cursor-pointer
      [:input.hidden {:type "file"
                      :accept ".pdf,.doc,.docx,.xls,.xlsx,.jpg,.jpeg,.png"
                      :on-change (fn [e]
                                   (when-let [file (-> e .-target .-files (aget 0))]
                                     (on-upload id (.-name file))))}]
      [:span.text-sm.font-semibold.px-4.py-2.rounded-lg.transition-all
       {:style {:background-color (if is-uploaded "#f0f0f0" "#00857c")
                :color (if is-uploaded "#333" "white")}}
       (if is-uploaded "Replace" "Upload")]]]))

(defn documents-step [form-data]
  (let [uploaded-docs (r/atom (or (:uploaded-documents form-data) {}))]
    (fn [form-data]
      (let [handle-upload (fn [doc-id filename]
                            (swap! uploaded-docs assoc doc-id filename)
                            (state/update-form-data! {:uploaded-documents @uploaded-docs}))
            handle-next (fn []
                          (state/update-form-data! {:uploaded-documents @uploaded-docs})
                          (state/go-next!))
            required-docs (filter :required state/required-documents)
            optional-docs (remove :required state/required-documents)
            required-complete (every? #(get @uploaded-docs (:id %)) required-docs)]
        [:div.space-y-6
         ;; Header
         [:div.card
          [:h2.text-xl.font-bold.mb-2 {:style {:color "#333"}} "Document Upload"]
          [:p.text-gray-500.text-sm
           "Upload the required documents to support your loan application. Documents marked with "
           [:span.text-red-500 "*"] " are required. You can upload PDF, Word, Excel, or image files."]]
         
         ;; Upload Progress
         [:div.card
          [:div.flex.items-center.justify-between.mb-3
           [:span.font-semibold.text-gray-700 "Upload Progress"]
           [:span.text-sm {:style {:color "#00857c"}}
            (str (count (filter #(get @uploaded-docs (:id %)) required-docs))
                 " of " (count required-docs) " required documents")]]
          [:div.h-2.bg-gray-200.rounded-full.overflow-hidden
           [:div.h-full.rounded-full.transition-all
            {:style {:background-color "#00857c"
                     :width (str (* 100 (/ (count (filter #(get @uploaded-docs (:id %)) required-docs))
                                           (max 1 (count required-docs)))) "%")}}]]]
         
         ;; Required Documents
         [:div.card.space-y-4
          [:h3.font-bold.text-gray-700.border-b.pb-2 "Required Documents"]
          [:p.text-sm.text-gray-500.mb-4
           "These documents are necessary to process your application."]
          
          (for [doc required-docs]
            ^{:key (:id doc)}
            [document-upload-item doc @uploaded-docs handle-upload])]
         
         ;; Optional Documents
         [:div.card.space-y-4
          [:h3.font-bold.text-gray-700.border-b.pb-2 "Additional Documents (Optional)"]
          [:p.text-sm.text-gray-500.mb-4
           "These documents may help expedite your application or improve your terms."]
          
          (for [doc optional-docs]
            ^{:key (:id doc)}
            [document-upload-item doc @uploaded-docs handle-upload])]
         
         ;; Notes
         [:div.card.space-y-4
          [:h3.font-bold.text-gray-700.border-b.pb-2 "Additional Notes"]
          [form-field {:label "Notes or Comments" :hint "Provide any additional context about your documents or application"}
           [:textarea.form-input {:rows 3
                                  :placeholder "e.g., My 2023 tax return is on extension, will provide by..."
                                  :value (:document-notes form-data)
                                  :on-change #(state/update-form-data!
                                               {:document-notes (.. % -target -value)})}]]]
         
         ;; Info Box
         [:div.rounded-lg.p-4 {:style {:background-color "#fff3cd" :border "1px solid #ffc107"}}
          [:div.flex.gap-3
           [:div.text-2xl "📋"]
           [:div
            [:h4.font-semibold.mb-1 {:style {:color "#856404"}} "Document Tips"]
            [:ul.text-sm.space-y-1 {:style {:color "#856404"}}
             [:li "• Ensure all pages are legible and complete"]
             [:li "• Tax returns should include all schedules"]
             [:li "• Financial statements should be recent (within 90 days)"]
             [:li "• Bank statements should show all pages"]]]]]
         
         ;; Warning if not all required
         (when (not required-complete)
           [:div.rounded-lg.p-4 {:style {:background-color "#f8d7da" :border "1px solid #f5c6cb"}}
            [:div.flex.gap-3
             [:div.text-2xl "⚠️"]
             [:div
              [:h4.font-semibold.mb-1 {:style {:color "#721c24"}} "Missing Required Documents"]
              [:p.text-sm {:style {:color "#721c24"}}
               "You can continue without uploading all documents, but your application may be delayed. "
               "A loan officer will contact you to request any missing documents."]]]])
         
         ;; Navigation
         [:div.flex.justify-between.gap-3
          [:button.font-semibold.py-3.px-6.rounded-lg.transition-all
           {:style {:border "2px solid #00857c" :color "#00857c"}
            :on-click state/go-back!}
           "← Back"]
          [:button.font-bold.py-3.px-8.rounded-lg.text-white.transition-all
           {:style {:background-color "#00857c"}}
           {:on-click handle-next}
           "Continue →"]]]))))
