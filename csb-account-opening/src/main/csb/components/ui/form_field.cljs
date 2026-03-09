(ns csb.components.ui.form-field)

(defn form-field [{:keys [label required error hint]} & children]
  [:div
   [:label.form-label
    label
    (when required
      [:span.required-star.ml-1 "*"])]
   (into [:<>] children)
   (when hint
     [:p.text-xs.text-gray-400.mt-1 hint])
   (when error
     [:p.error-text error])])
