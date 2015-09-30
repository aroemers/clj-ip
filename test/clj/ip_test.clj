(ns clj.ip-test
  (:refer-clojure :exclude (compile))
  (:require [clj.ip :refer :all]
            [clojure.set :refer (difference)]
            [clojure.test :refer :all]))


;;; Helper definitions.

(def ^:dynamic *rand-tests* 100000)

(defn test-rand-ips [r]
  (-> (for [n (range *rand-tests*)
            :let [i (str (rand-int 256) "." (rand-int 256) "." (rand-int 256) "." (rand-int 256))]
            :when (has-ip? r i)]
        i)
      (set)))


;;; Tests

(deftest uncompiled-test
  (are [range ip in?] (= (has-ip? range ip) in?)
    "1.1.1.12/30" "1.1.1.11" false
    "1.1.1.12/30" "1.1.1.12" true
    "1.1.1.12/30" "1.1.1.13" true
    "1.1.1.12/30" "1.1.1.14" true
    "1.1.1.12/30" "1.1.1.15" true
    "1.1.1.12/30" "1.1.1.16" false)
  (is (empty? (difference (test-rand-ips "1.1.1.12/30")
                          #{"1.1.1.12" "1.1.1.13" "1.1.1.14" "1.1.1.15"}))))



(deftest compiled-test
  (let [compiled (compile "1.1.1.12/30")]
    (are [range ip in?] (= (has-ip? range ip) in?)
      compiled "1.1.1.11" false
      compiled "1.1.1.12" true
      compiled "1.1.1.13" true
      compiled "1.1.1.14" true
      compiled "1.1.1.15" true
      compiled "1.1.1.16" false)
    (is (empty? (difference (test-rand-ips compiled)
                            #{"1.1.1.12" "1.1.1.13" "1.1.1.14" "1.1.1.15"})))))
