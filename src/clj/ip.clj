(ns clj.ip
  (:refer-clojure :exclude (compile))
  (:require [clojure.string :refer (split trim)])
  (:import [java.math BigInteger]
           [java.net InetAddress]))


(defprotocol IP
  (to-bytes [this]
    "Returns the given IP instance as a BigInteger representation of
    the bytes."))

(extend-protocol IP
  String
  (to-bytes [this]
    (BigInteger. (.getAddress (InetAddress/getByName this))))

  InetAddress
  (to-bytes [this]
    (BigInteger. (.getAddress this))))


(defprotocol Range
  (compile [this]
    "Compiles the range to a faster version, to optimize when a range is
    used often. Currently a String (ipv4 and ipv6) can be compiled
    (but can also be used directly). A compiled range can also be used
    as a predicate, implicitly calling `has-ip?`.")

  (has-ip? [this ip]
    "Given this range, compiled or uncompiled, and an IP address in the
    form of a String (ipv4 or ipv6) or InetAddress, this function
    checks whether the given IP falls within the range."))

(deftype CompiledRange [^String original ^BigInteger prefix-num ^int shift]
  Range
  (compile [this] this)
  (has-ip? [this ip]
    (= 0 (.shiftRight (.xor prefix-num (to-bytes ip)) shift)))

  clojure.lang.IFn
  (invoke [this ip]
    (has-ip? this ip))

  Object
  (toString [this] original)
  (equals [this other] (and (= prefix-num (.prefix-num ^CompiledRange other))
                            (= shift (.shift ^CompiledRange other))))
  (hashCode [this] (.hashCode original)))

(extend-type String
  Range
  (compile [this]
    (let [[prefix bits] (split (trim this) #"/")
          bits (int (Integer/parseInt bits))
          prefix-bytes (.getAddress (InetAddress/getByName prefix))
          prefix-num (BigInteger. prefix-bytes)
          ipv6? (< 4 (count prefix-bytes))]
      (CompiledRange. (trim this) prefix-num (- (if ipv6? 128 32) bits))))

  (has-ip? [this ip]
    (has-ip? (compile this) ip)))
