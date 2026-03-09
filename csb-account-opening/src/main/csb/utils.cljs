(ns csb.utils)

(def us-states
  ["AL" "AK" "AZ" "AR" "CA" "CO" "CT" "DE" "FL" "GA" "HI" "ID" "IL" "IN" "IA"
   "KS" "KY" "LA" "ME" "MD" "MA" "MI" "MN" "MS" "MO" "MT" "NE" "NV" "NH" "NJ"
   "NM" "NY" "NC" "ND" "OH" "OK" "OR" "PA" "RI" "SC" "SD" "TN" "TX" "UT" "VT"
   "VA" "WA" "WV" "WI" "WY" "DC"])

(def business-types
  [{:value "" :label "Select business type..."}
   {:value "sole-prop" :label "Sole Proprietorship"}
   {:value "partnership" :label "General Partnership"}
   {:value "llp" :label "Limited Liability Partnership (LLP)"}
   {:value "llc-single" :label "LLC – Single Member"}
   {:value "llc-multi" :label "LLC – Multi Member"}
   {:value "corp-s" :label "Corporation (S-Corp)"}
   {:value "corp-c" :label "Corporation (C-Corp)"}
   {:value "nonprofit" :label "Non-Profit Organization"}
   {:value "trust" :label "Trust"}
   {:value "gov" :label "Government Entity"}
   {:value "other" :label "Other"}])

(def id-types
  [{:value "" :label "Select ID type..."}
   {:value "drivers-license" :label "Driver's License"}
   {:value "state-id" :label "State-Issued Photo ID"}
   {:value "passport" :label "U.S. Passport"}
   {:value "passport-card" :label "U.S. Passport Card"}
   {:value "military-id" :label "Military ID"}])

(def titles
  [{:value "" :label "Select title..."}
   {:value "owner" :label "Owner"}
   {:value "ceo" :label "CEO / President"}
   {:value "cfo" :label "CFO / Treasurer"}
   {:value "coo" :label "COO"}
   {:value "managing-member" :label "Managing Member"}
   {:value "partner" :label "Partner"}
   {:value "authorized-signer" :label "Authorized Signer"}])

(def products-map
  {"business-checking" "Business Checking"
   "business-interest-checking" "Business Interest Checking"
   "business-savings" "Business Savings"
   "business-money-market" "Business Money Market"
   "business-cd" "Business Certificate of Deposit"})

(def purpose-map
  {"new-business" "New Business"
   "existing-business" "Existing Business"
   "sole-proprietor" "Sole Proprietor"})

(def business-type-map
  {"sole-prop" "Sole Proprietorship"
   "partnership" "General Partnership"
   "llp" "Limited Liability Partnership (LLP)"
   "llc-single" "LLC – Single Member"
   "llc-multi" "LLC – Multi Member"
   "corp-s" "Corporation (S-Corp)"
   "corp-c" "Corporation (C-Corp)"
   "nonprofit" "Non-Profit Organization"
   "trust" "Trust"
   "gov" "Government Entity"
   "other" "Other"})

(defn format-ein [val]
  (let [digits (-> val
                   (clojure.string/replace #"\D" "")
                   (subs 0 (min 9 (count val))))]
    (if (>= (count digits) 3)
      (str (subs digits 0 2) "-" (subs digits 2))
      digits)))

(defn format-phone [val]
  (let [digits (-> val
                   (clojure.string/replace #"\D" "")
                   (subs 0 (min 10 (count val))))]
    (cond
      (>= (count digits) 7)
      (str "(" (subs digits 0 3) ") " (subs digits 3 6) "-" (subs digits 6))
      
      (>= (count digits) 4)
      (str "(" (subs digits 0 3) ") " (subs digits 3))
      
      (>= (count digits) 1)
      (str "(" digits)
      
      :else digits)))

(defn format-ssn [val]
  (let [digits (-> val
                   (clojure.string/replace #"\D" "")
                   (subs 0 (min 9 (count val))))]
    (cond
      (>= (count digits) 6)
      (str (subs digits 0 3) "-" (subs digits 3 5) "-" (subs digits 5))
      
      (>= (count digits) 4)
      (str (subs digits 0 3) "-" (subs digits 3))
      
      :else digits)))

(defn mask-ssn [ssn]
  (if (and ssn (>= (count ssn) 4))
    (str "•••-••-" (subs ssn (- (count ssn) 4)))
    ""))

(defn valid-email? [email]
  (re-matches #"^[^\s@]+@[^\s@]+\.[^\s@]+$" email))

(defn valid-zip? [zip]
  (re-matches #"^\d{5}(-\d{4})?$" zip))

(defn valid-ein? [ein]
  (or (re-matches #"^\d{2}-\d{7}$" ein)
      (= 9 (count (clojure.string/replace ein #"-" "")))))

(defn valid-ssn? [ssn]
  (re-matches #"^\d{3}-\d{2}-\d{4}$" ssn))

(defn generate-confirmation-number []
  (let [chars "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"]
    (str "CSB-"
         (apply str (repeatedly 8 #(nth chars (rand-int (count chars))))))))

(defn today-date []
  (.toISOString (js/Date.) ))

(defn max-dob []
  ;; 18 years ago
  (let [date (js/Date.)
        year (- (.getFullYear date) 18)]
    (.setFullYear date year)
    (subs (.toISOString date) 0 10)))
