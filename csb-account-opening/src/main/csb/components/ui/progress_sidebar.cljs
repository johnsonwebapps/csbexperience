(ns csb.components.ui.progress-sidebar)

(defn check-icon []
  [:svg.w-4.h-4 {:viewBox "0 0 20 20" :fill "currentColor"}
   [:path {:fill-rule "evenodd"
           :d "M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z"
           :clip-rule "evenodd"}]])

(defn progress-sidebar [steps current-step]
  [:div.card.sticky {:style {:top "1rem"}}
   [:div.text-xs.font-bold.uppercase.tracking-widest.text-gray-500.mb-4
    "Application Progress"]
   [:ol.space-y-1
    (for [[i step] (map-indexed vector steps)
          :let [status (cond
                         (< (:id step) current-step) "completed"
                         (= (:id step) current-step) "active"
                         :else "pending")]]
      ^{:key (:id step)}
      [:li.flex.items-start.gap-3
       [:div.flex.flex-col.items-center
        [:div {:class (str "step-dot " status)}
         (if (= status "completed")
           [check-icon]
           (:id step))]
        (when (< i (dec (count steps)))
          [:div.w-0.5.h-6.mt-1.rounded
           {:class (if (= status "completed")
                     "bg-csb-teal"
                     "bg-gray-200")}])]
       [:div.pt-1.5.pb-3
        [:div.text-sm.font-semibold.leading-tight
         {:class (case status
                   "active" "text-csb-teal"
                   "completed" "text-csb-teal"
                   "text-gray-400")}
         (:label step)]
        (when (= status "active")
          [:div.text-xs.text-gray-500.mt-0.5 "In progress"])
        (when (= status "completed")
          [:div.text-xs.text-csb-teal.mt-0.5 "Complete"])]])]
   
   [:div.mt-6.pt-4.border-t.border-gray-200
    [:div.info-box
     [:p.font-semibold.mb-1 "Need help?"]
     [:p "Call us at "
      [:a.font-bold.text-csb-teal {:href "tel:888-418-5626"} "888.418.5626"]]
     [:p.mt-1.text-sm.text-gray-600 "Mon–Fri 8am–6pm ET"]]]])
