(ns clj.ip
  (:refer-clojure :exclude [compile])
  (:import [java.math BigInteger]
           [java.net InetAddress]))


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

(deftype CompiledRange [^String original ^BigInteger prefix-num ^int bits ^boolean ipv6?]
  Range
  (compile [this] this)
  (has-ip? [this ip]
    (let [;;---TODO Make IP a protocol, with a to-bytes function, as well?
          ip (if (instance? InetAddress ip) ip (InetAddress/getByName ip))
          ip-num (BigInteger. (.getAddress ip))]
      (= 0 (.shiftRight (.xor prefix-num ip-num) (- (if ipv6? 128 32) bits)))))

  clojure.lang.IFn
  (invoke [this ip]
    (has-ip? this ip))

  Object
  (toString [this] original)
  (equals [this other] (and (= prefix-num (.prefix-num other)) (= bits (.bits other))))
  (hashCode [this] (.hashCode original)))

(extend-type String
  Range
  (compile [this]
    (let [[prefix bits] (split (trim this) #"/")
          bits (int (Integer/parseInt bits))
          prefix-bytes (.getAddress (InetAddress/getByName prefix))
          prefix-num (BigInteger. prefix-bytes)]
      (CompiledRange. (trim this) prefix-num bits (< 4 (count prefix-bytes)))))

  (has-ip? [this ip]
    (has-ip? (compile this) ip)))
