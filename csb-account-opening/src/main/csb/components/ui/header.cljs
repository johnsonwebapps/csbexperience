(ns csb.components.ui.header)

(defn fdic-banner []
  [:div.flex.items-center.justify-center.gap-2.py-1
   {:style {:background-color "#003366" :color "white" :font-size "12px"}}
   [:span.font-bold "Member FDIC"]
   [:span " - FDIC-Insured - Backed by the full faith and credit of the U.S. Government"]])

(defn security-alert []
  [:div.py-2.px-4.text-center
   {:style {:background-color "#f8f8f8" :border-bottom "1px solid #e5e5e5" :font-size "11px" :color "#666"}}
   [:span.font-semibold {:style {:color "#c00"}} "SECURITY ALERT: "]
   "We will NEVER call, email, or text to ask for your Online Banking username, password, one-time code, PIN, or account details."])

(defn header []
  [:<>
   ;; FDIC Banner
   [fdic-banner]
   
   ;; Security Alert Banner
   [security-alert]
   
   ;; Main Header
   [:header.bg-white.border-b.border-gray-200
    [:div.max-w-6xl.mx-auto.px-4
     [:div.flex.items-center.justify-between.h-16
      ;; Logo
      [:a {:href "https://www.cambridgesavings.com"}
       [:img {:src "/images/header.png"
              :alt "Cambridge Savings Bank"
              :style {:max-height "1280px" :width "auto" :max-width "2080px"}}]]
      
      ;; Right side - Log In link
      [:div.flex.items-center.gap-4
       [:a.text-sm.font-semibold.px-4.py-2.rounded.transition-colors
        {:href "#"
         :style {:color "#00857c"}}
        "Log In"]
       ;; Hamburger menu icon (visual only)
       [:button.p-2.text-gray-600.hover:text-gray-800
        {:type "button"}
        [:svg.w-6.h-6 {:fill "none" :viewBox "0 0 24 24" :stroke "currentColor"}
         [:path {:stroke-linecap "round" :stroke-linejoin "round" :stroke-width 2 :d "M4 6h16M4 12h16M4 18h16"}]]]]]]]
   
   ;; Page title bar
   [:div.py-6
    {:style {:background-color "#00857c"}}
    [:div.max-w-6xl.mx-auto.px-4
     [:h1.text-white.text-2xl.font-bold.uppercase.tracking-wide
      "Open a Business Account"]]]])

