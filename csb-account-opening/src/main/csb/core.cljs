(ns csb.core
  (:require [reagent.core :as r]
            [reagent.dom.client :as rdom]
            [csb.state :as state]
            [csb.components.app :refer [app]]))

(defonce root (atom nil))

(defn render []
  (when @root
    (rdom/render @root [app])))

(defn ^:export init []
  (reset! root (rdom/create-root (.getElementById js/document "root")))
  (render))
