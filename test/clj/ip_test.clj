(ns cljip.core-test
  (:require [clojure.test :refer :all]
            [clj.ip :refer :all]))

(def allowed-ranges
  (mapv compile
        [;; Test server network
         "91.220.189.12/30"
         "91.220.189.16/32"
         ;; Live network
         "94.236.54.30/32"
         "194.247.166.0/23"
         "195.28.166.0/24" ]))

(def in-ranges?
  (apply some-fn allowed-ranges))
