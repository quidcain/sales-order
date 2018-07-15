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

(defn display-cust-table
  [customers]
  (doseq [customer customers]
    (println (format "%d: [\"%s\" \"%s\" \"%s\"]"
                 (:custID customer)
                 (:name customer)
                 (:address customer)
                 (:phoneNumber customer)))))

(defn display-prod-table
  [products]
  (doseq [product products]
    (println (format "%d: [\"%s\" \"%s\"]"
                 (:prodID product)
                 (:itemDescription product)
                 (:unitCost product)))))

(defn display-sales-table
  [sales customers products]
  (doseq [sale sales]
    (println (format "%d: [\"%s\" \"%s\" \"%d\"]"
                 (:salesID sale) 
                 (:name (first (filter #(= (:custID %)(:custID sale)) customers)))
                 (:itemDescription (first (filter #(= (:prodID %)(:prodID sale)) products)))
                 (:itemCount sale)))))

(defn- get-cust-by-name
  [name customers]
  (first (filter #(= (:name %) name) customers)))

(defn- get-sales-by-cust
  [customer sales]
  (filter #(= (:custID customer)
              (:custID %))
           sales))
(defn- get-prod-by-sale
  [sale prods]
  (first (filter #(= (:prodID sale)
                     (:prodID %))
                 prods)))

(defn total-sales-by-custName
  [customer-name customers products sales]
  (reduce + 0 (for [sale (-> customer-name
                             (get-cust-by-name customers)
                             (get-sales-by-cust sales))]
                (* (:unitCost (get-prod-by-sale sale products) 0)
                   (:itemCount sale 0)))))

(defn- get-prod-by-desc
  [prodDesc products]
  (first (filter #(= (:itemDescription %) prodDesc) products)))
(defn- get-sales-by-prod
  [product sales]
  (filter #(= (:prodID product)
              (:prodID %))
          sales))

(defn total-sales-by-prodDesc
  [prodDesc products sales]
  (->> (-> prodDesc
           (get-prod-by-desc products)
           (get-sales-by-prod sales))
       (map :itemCount)
       (reduce + 0)))

(defn- handle-4
  [customers products sales]
  (println "Enter customer's name")
  (let [name (read-line)]
    (println (str name ": $"(total-sales-by-custName name customers products sales)))))

(defn- handle-5
  [products sales]
  (println "Enter product's description")
  (let [prodDesc (read-line)]
    (println (str prodDesc ": "(total-sales-by-prodDesc prodDesc products sales)))))

(defn main
  []
  (println "
*** Sales Menu ***
------------------
1. Display Customer Table
2. Display Product Table
3. Display Sales Table
4. Total Sales for Customer
5. Total Count for Product
6. Exit
Enter an option?")
  (let [input (read-string (read-line))]
    (case input
      1 (display-cust-table customers)
      2 (display-prod-table products)
      3 (display-sales-table sales customers products)
      4 (handle-4 customers products sales)
      5 (handle-5 products sales)
      6 (println "Good Bye")
      "")
    (if (not= 6 input)
      (recur))))
