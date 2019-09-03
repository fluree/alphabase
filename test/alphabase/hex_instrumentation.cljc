(ns alphabase.hex-instrumentation
  (:require
    #?(:clj  [clojure.test :refer :all]
       :cljs [cljs.test :refer-macros [deftest is testing]])
    #?(:clj  [clojure.pprint :as pprint]
       :cljs [cljs.pprint :as pprint])
    #?(:cljs [goog.string.format :as format])
    [alphabase.bytes :as b]
    [alphabase.hex :as hex]
    [clojure.spec.alpha :as spec]
    [clojure.spec.gen.alpha :as gen]
    [clojure.spec.test.alpha :as spec.test]
    [clojure.string :as str]))


;; definition of generic "types"
(spec/def ::hex-string
  (spec/with-gen
    (spec/and string? #(hex/valid? %))
    #(gen/fmap
       (fn
         [i1]
         (let [length (* 64 (+ 32 (Math/abs i1)))
               r (java.util.Random.)
               bi (BigInteger. length r)]
           (format "%x" bi)
           ))
       (gen/int)
       )
    )
  )


;definition of function specs
(spec/fdef hex/decode
           :args (spec/cat :data ::hex-string)
           :ret bytes?
           )

(spec/fdef hex/encode
           :args (spec/cat :data bytes?)
           :ret any?
           :fn #(or (nil? (:ret %)) (hex/valid? (:ret %)))
           )


;; Utility functions to integrate clojure.spec.test/check with clojure.test
(defn summarize-results' [spec-check]
  (map (comp #(pprint/write % :stream nil) spec.test/abbrev-result) spec-check))

(defn check' [spec-check]
  (is (nil? (-> spec-check first :failure)) (summarize-results' spec-check)))


;; Tests
(deftest hex-decode-instrumentation
  (testing "hex/decode"
    (check' (spec.test/check `hex/decode))
    )
  )

(deftest hex-encode-hex-decode-instrumentation
  (testing "hex/encode"
    (check' (spec.test/check `hex/encode))
    )
  )


(comment

  (run-tests)

  (in-ns 'alphabase.hex-instrumentation)

  (spec/def :fluree/generic-string string?)

  (spec/def ::hex-string
    (spec/with-gen
      (spec/and string? #(hex/valid? %))
      #(gen/fmap
         (fn
           [i1]
           (let [counter (+ 2 (Math/abs i1)) ]
             (str/join (repeatedly counter (fn [] (format "%06x" (rand-int 16rFFFFFF)))))
             ))
         (gen/int)
         )
      )
    )

  (gen/sample (spec/gen ::hex-string))
  (gen/sample (spec/gen :fluree/generic-string))

  (spec/fdef hex/decode
          :args (spec/cat :data string?)
          :ret bytes?
          )
  (spec.test/check `hex/decode)   ; fails


  (spec/fdef hex/decode
          :args (spec/cat :data ::hex-string)
          :ret bytes?
          )
  (spec.test/instrument 'hex/decode)
  (spec.test/check `hex/decode) ; passes

  (spec.test/summarize-results (spec.test/check `hex/decode))
  (spec.test/abbrev-result (first (spec.test/check `hex/decode)))  ;; use if errors


  (spec/def ::hex-string2
    (spec/with-gen
      (spec/and string? #(hex/valid? %))
      #(gen/fmap
         (fn
           [i1]
           (let [length (* 64 (+ 32 (Math/abs i1)))
                 r (java.util.Random.)
                 bi (BigInteger. length r)]
             (format "%x" bi)
             ))
         (gen/int)
         )
      )
    )
  (gen/sample (spec/gen ::hex-string2))

  (gen/sample (spec/gen bytes?))
  (spec/fdef hex/encode
     :args (spec/cat :data bytes?)
     :ret any?
     :fn #(or (nil? (:ret %)) (hex/valid? (:ret %)))
     )
  (spec.test/instrument 'hex/encode)
  (spec.test/check `hex/encode) ; passes


  )
