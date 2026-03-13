(ns csb.storage
  "File-based persistence for application data - syncs via GitHub"
  (:require [cljs.reader :as reader]
            [cljs.pprint :as pprint]
            [clojure.string :as str]))

;; Storage keys for localStorage (used as cache + working storage)
(def APPLICATIONS_KEY "csb-applications")
(def CURRENT_APP_KEY "csb-current-application")
(def DATA_FILE_PATH "/data/applications.edn")

;; Atom to track if we've loaded from file
(defonce file-loaded? (atom false))
(defonce loading? (atom false))

;; Helper functions for localStorage
(defn- get-item [key]
  (when-let [value (.getItem js/localStorage key)]
    (try
      (reader/read-string value)
      (catch :default _
        nil))))

(defn- set-item [key value]
  (.setItem js/localStorage key (pr-str value)))

(defn- remove-item [key]
  (.removeItem js/localStorage key))

;; Generate unique application ID
(defn generate-app-id []
  (str "APP-" (.substring (.toUpperCase (.toString (random-uuid))) 0 8)))

;; Load applications from the EDN file (called at app startup)
(defn load-from-file! [on-complete]
  (when-not @loading?
    (reset! loading? true)
    (-> (js/fetch DATA_FILE_PATH)
        (.then (fn [response]
                 (if (.-ok response)
                   (.text response)
                   (throw (js/Error. "Failed to load data file")))))
        (.then (fn [text]
                 ;; Parse EDN, handling comments
                 (let [clean-text (->> (str/split-lines text)
                                       (remove #(str/starts-with? (str/trim %) ";;"))
                                       (str/join "\n"))
                       apps (try
                              (reader/read-string clean-text)
                              (catch :default _ []))]
                   (when (seq apps)
                     ;; Merge with localStorage (file takes precedence for same IDs)
                     (let [local-apps (or (get-item APPLICATIONS_KEY) [])
                           file-ids (set (map :id apps))
                           unique-local (filter #(not (file-ids (:id %))) local-apps)
                           merged (vec (concat apps unique-local))]
                       (set-item APPLICATIONS_KEY merged)))
                   (reset! file-loaded? true)
                   (reset! loading? false)
                   (when on-complete (on-complete)))))
        (.catch (fn [_err]
                  (reset! file-loaded? true)
                  (reset! loading? false)
                  (when on-complete (on-complete)))))))

;; Get all applications
(defn get-all-applications []
  (or (get-item APPLICATIONS_KEY) []))

;; Get a specific application by ID
(defn get-application [app-id]
  (->> (get-all-applications)
       (filter #(= (:id %) app-id))
       first))

;; Save/update an application
(defn save-application! [app-data]
  (let [apps (get-all-applications)
        app-id (or (:id app-data) (generate-app-id))
        now (.toISOString (js/Date.))
        updated-app (-> app-data
                        (assoc :id app-id)
                        (assoc :updated-at now)
                        (update :created-at #(or % now)))
        ;; Remove existing app with same ID, then add updated
        other-apps (filter #(not= (:id %) app-id) apps)
        new-apps (conj (vec other-apps) updated-app)]
    (set-item APPLICATIONS_KEY new-apps)
    updated-app))

;; Delete an application
(defn delete-application! [app-id]
  (let [apps (get-all-applications)
        new-apps (filter #(not= (:id %) app-id) apps)]
    (set-item APPLICATIONS_KEY (vec new-apps))))

;; Export all applications as EDN file (downloads to user's computer)
(defn export-to-file! []
  (let [apps (get-all-applications)
        header ";; CSB Business Banking Applications Database\n;; Generated: "
        timestamp (.toISOString (js/Date.))
        content (str header timestamp 
                     "\n;; Move this file to: csb-account-opening/resources/public/data/applications.edn"
                     "\n;; Then: git add resources/public/data/applications.edn && git commit -m 'Update app data' && git push\n"
                     (with-out-str (pprint/pprint apps)))
        blob (js/Blob. #js [content] #js {:type "text/plain"})
        url (.createObjectURL js/URL blob)
        link (.createElement js/document "a")]
    (set! (.-href link) url)
    (set! (.-download link) "applications.edn")
    (.appendChild (.-body js/document) link)
    (.click link)
    (.removeChild (.-body js/document) link)
    (.revokeObjectURL js/URL url)))

;; Import applications from file content (called after file picker)
(defn import-from-content! [content on-complete]
  (try
    (let [clean-text (->> (str/split-lines content)
                          (remove #(str/starts-with? (str/trim %) ";;"))
                          (str/join "\n"))
          apps (reader/read-string clean-text)]
      (when (vector? apps)
        (set-item APPLICATIONS_KEY apps))
      (when on-complete (on-complete true)))
    (catch :default e
      (js/console.error "Failed to import:" e)
      (when on-complete (on-complete false)))))

;; Save current application state (auto-save)
(defn save-current-state! [state]
  (set-item CURRENT_APP_KEY state))

;; Get current application state
(defn get-current-state []
  (get-item CURRENT_APP_KEY))

;; Clear current state
(defn clear-current-state! []
  (remove-item CURRENT_APP_KEY))

;; Get applications by status
(defn get-applications-by-status [status]
  (->> (get-all-applications)
       (filter #(= (:status %) status))))

(defn get-drafts []
  (get-applications-by-status :draft))

(defn get-submitted []
  (get-applications-by-status :submitted))

;; Get statistics
(defn get-stats []
  (let [apps (get-all-applications)]
    {:total (count apps)
     :drafts (count (filter #(= (:status %) :draft) apps))
     :submitted (count (filter #(= (:status %) :submitted) apps))
     :approved (count (filter #(= (get-in % [:form-data :loan-decision]) :approved) apps))
     :denied (count (filter #(= (get-in % [:form-data :loan-decision]) :denied) apps))}))
