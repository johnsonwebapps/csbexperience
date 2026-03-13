(ns csb.core
  (:require [reagent.dom.client :as rdom]
            [csb.components.app :refer [app]]
            [csb.storage :as storage]))

(defonce root (atom nil))

(defn render []
  (when @root
    (rdom/render @root [app])))

(defn ^:export init []
  ;; Load data from file at startup (merges with localStorage)
  (storage/load-from-file! nil)
  (reset! root (rdom/create-root (.getElementById js/document "root")))
  (render))
