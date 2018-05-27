(use 'clojure.string)

(defn parse-line
  [line]
  (split line #"[\\|]"))

(defn slurp-and-parse
  [file-name]
  (map parse-line
       (split-lines
        (slurp file-name))))

(defn parse-cust
  [[custID name address phoneNumber]]
  {:custID (read-string custID)
   :name name
   :address address
   :phoneNumber phoneNumber})
(defn parse-prod
  [[prodID itemDescription unitCost]]
  {:prodID (read-string prodID)
   :itemDescription itemDescription
   :unitCost (read-string unitCost)})
(defn parse-sales
  [[salesID custID prodID itemCount]]
  {:salesID (read-string salesID)
   :custID (read-string custID)
   :prodID (read-string prodID)
   :itemCount (read-string itemCount)})


(def customers (sort-by :custID (map parse-cust (slurp-and-parse "cust.txt"))))
(def products (sort-by :prodID (map parse-prod (slurp-and-parse "prod.txt"))))
(def sales (sort-by :salesID (map parse-sales (slurp-and-parse "sales.txt"))))
