(ns alphabase.hex-instrumentation
  (:require
    [alphabase.bytes :as b]
    [alphabase.hex :as hex]
    #?(:clj  [clojure.test :refer :all]
       :cljs [cljs.test :refer-macros [deftest is testing]])
    #?(:cljs [clojure.test.check])
    #?(:cljs [clojure.test.check.properties :as prop :include-macros true])
    [clojure.spec.alpha :as spec]
    [clojure.spec.gen.alpha :as gen]
    [clojure.spec.test.alpha :as spec.test]
    #?(:clj  [clojure.pprint :as pprint]
       :cljs [cljs.pprint :as pprint])
    [clojure.string :as str]))


;; definition of generic "types"
(spec/def ::hex-string
  (spec/with-gen
    (spec/and string? #(hex/valid? %))
    #(gen/fmap
       (fn
         [i1]
         #?(:clj  (let [length (* 64 (+ 32 (Math/abs i1)))
                        r (java.util.Random.)
                        bi (BigInteger. length r)]
                    (format "%x" bi))
            :cljs (let [counter (+ 2 (Math/abs i1))]
                    (str/join (repeatedly counter (fn [] (.toString (rand-int 16rFFFFFF) 16)))))))
       (gen/int))))


;definition of function specs
(spec/fdef hex/decode
           :args (spec/cat :data ::hex-string)
           :ret bytes?)

(spec/fdef hex/encode
           :args (spec/cat :data bytes?)
           :ret any?
           :fn #(or (nil? (:ret %)) (hex/valid? (:ret %))))


;; Tests
(deftest hex-decode-instrumentation
  (testing "hex/decode"
    (spec.test/check `hex/decode)))

(deftest hex-encode-hex-decode-instrumentation
  (testing "hex/encode"
    ;(check' (spec.test/check `hex/encode))
    (spec.test/check `hex/encode)))


(comment

  (run-tests)

  (in-ns 'alphabase.hex-instrumentation)

  (spec/def :fluree/generic-string string?)

  (gen/sample (spec/gen ::hex-string))
  (gen/sample (spec/gen :fluree/generic-string))

  (spec.test/check `hex/decode)   ; fails

  (spec.test/instrument 'hex/decode)
  (spec.test/check `hex/decode) ; passes

  (gen/sample (spec/gen bytes?))
  (spec/fdef hex/encode
     :args (spec/cat :data bytes?)
     :ret any?
     :fn #(or (nil? (:ret %)) (hex/valid? (:ret %)))
     )
  (spec.test/instrument 'hex/encode)
  (spec.test/check `hex/encode) ; passes


  )
