(ns csb.unified.steps.documents
  (:require [csb.unified.state :as state]
            [reagent.core :as r]))

(defn document-item [doc uploaded?]
  (let [{:keys [id label required]} doc]
    [:div.flex.items-center.justify-between.p-4.rounded-lg.border
     {:class (if uploaded? "border-green-200 bg-green-50" "border-gray-200")}
     [:div.flex.items-center.gap-3
      [:div.w-8.h-8.rounded-full.flex.items-center.justify-center
       {:class (if uploaded? "bg-green-500 text-white" "bg-gray-200 text-gray-500")}
       (if uploaded? "✓" "📄")]
      [:div
       [:p.font-medium.text-gray-900 
        label
        (when required [:span.text-red-500.ml-1 "*"])]
       [:p.text-xs.text-gray-500 (if uploaded? "Uploaded" "Not uploaded")]]]
     [:button.px-4.py-2.rounded-lg.text-sm.font-medium.transition-all
      {:style (if uploaded?
                {:color "#00857c" :border "1px solid #00857c"}
                {:background-color "#00857c" :color "white"})
       :on-click #(state/update-form-data! 
                   {:uploaded-documents 
                    (if uploaded?
                      (dissoc (:uploaded-documents (:form-data @state/app-state)) id)
                      (assoc (:uploaded-documents (:form-data @state/app-state)) id true))})}
      (if uploaded? "Remove" "Upload")]]))

(defn documents-step []
  (let [form-data (:form-data @state/app-state)
        uploaded (:uploaded-documents form-data)
        required-docs (filter :required state/required-documents)
        optional-docs (filter #(not (:required %)) state/required-documents)
        required-uploaded (count (filter #(get uploaded (:id %)) required-docs))
        total-required (count required-docs)]
    [:div.space-y-6
     [:div.card
      [:div.flex.items-center.gap-2.mb-4
       [:div.w-8.h-8.rounded-full.flex.items-center.justify-center.text-white.text-sm.font-bold
        {:style {:background-color "#00857c"}} "5"]
       [:h2.text-xl.font-bold.text-gray-900 "Supporting Documents"]]
      [:p.text-gray-600.mb-6
       "Please upload the following documents to support your loan application. "
       "Required documents are marked with an asterisk (*)."]]
     
     ;; Progress indicator
     [:div.card
      [:div.flex.items-center.justify-between.mb-2
       [:span.font-medium.text-gray-700 "Document Upload Progress"]
       [:span.text-sm {:style {:color "#00857c"}} 
        (str required-uploaded " of " total-required " required documents")]]
      [:div.h-2.bg-gray-200.rounded-full.overflow-hidden
       [:div.h-full.transition-all.duration-300
        {:style {:width (str (* (/ required-uploaded total-required) 100) "%")
                 :background-color "#00857c"}}]]]
     
     ;; Required Documents
     [:div.card
      [:h3.font-semibold.text-lg.mb-4.pb-2.border-b {:style {:color "#00857c"}}
       "Required Documents"]
      [:div.space-y-3
       (for [doc required-docs]
         ^{:key (:id doc)}
         [document-item doc (get uploaded (:id doc))])]]
     
     ;; Optional Documents
     [:div.card
      [:h3.font-semibold.text-lg.mb-4.pb-2.border-b.text-gray-600
       "Optional Documents"]
      [:p.text-sm.text-gray-500.mb-4 
       "These documents may help strengthen your application."]
      [:div.space-y-3
       (for [doc optional-docs]
         ^{:key (:id doc)}
         [document-item doc (get uploaded (:id doc))])]]
     
     ;; Note about documents
     [:div.rounded-lg.p-4 {:style {:background-color "rgba(0, 133, 124, 0.08)"
                                   :border "1px solid rgba(0, 133, 124, 0.2)"}}
      [:div.flex.gap-3
       [:div.text-xl "📋"]
       [:div
        [:h4.font-semibold.mb-1 {:style {:color "#00857c"}} "Document Tips"]
        [:ul.text-sm.text-gray-600.space-y-1
         [:li "• Tax returns should include all schedules"]
         [:li "• Financial statements should be current (within 90 days)"]
         [:li "• Bank statements should show all pages"]
         [:li "• You can upload additional documents later if needed"]]]]]
     
     ;; Navigation
     [:div.flex.justify-between.pt-4
      [:button.font-medium.py-3.px-6.rounded-lg.transition-all
       {:style {:color "#00857c" :border "2px solid #00857c"}
        :on-click state/go-back!}
       "← Back"]
      [:button.font-bold.py-3.px-8.rounded-lg.text-white.transition-all
       {:style {:background-color "#00857c"}
        :on-click state/go-next!}
       "Continue →"]]]))
