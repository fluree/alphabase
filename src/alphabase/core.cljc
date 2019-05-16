(ns alphabase.core
  (:require [alphabase.base64 :as base64]
            [alphabase.base58 :as base58]
            [alphabase.codec :as codec]
            [alphabase.hex :as ahex]))


(defn biginteger->bytes
  [bint]
  #?(:cljs (throw (js/Error. "Biginteger is not supported in cljs."))
     :clj  (->> bint
                biginteger
                .toByteArray
                (drop-while zero?)
                byte-array)))


(defn bytes->biginteger
  [ba]
  #?(:cljs (throw (js/Error. "Biginteger is not supported in cljs."))
     :clj  (BigInteger. 1 ba)))


(defn encode
  [alphabet ^bytes data]
  (codec/encode alphabet data))


(defn decode
  [alphabet tokens]
  (codec/decode alphabet tokens))


(defn bytes->base58
  "Converts bytes to base-58"
  [b]
  (base58/encode b))


(defn base58->bytes
  "Converts bytes to base-58"
  [b58]
  (base58/decode b58))


(defn bytes->base64
  "Converts bytes to base-64"
  [b]
  (base64/encode b))


(defn base64->bytes
  "Converts bytes to base-64"
  [b64]
  (base64/decode b64))


(defn hex->bytes
  [hex]
  (ahex/decode hex))


(defn bytes->hex
  [b]
  (ahex/encode b))


(defn base58?
  "Test if a string is base58"
  [x]
  (base58/base58? x))


(defn base64?
  "Test if a string is base58"
  [x]
  (base64/base64? x))


(defn hex?
  "Test if a string is base58"
  [x]
  (ahex/hex? x))


(defn base58-to-hex
  "Encodes a base58-string as a hex-string"
  [data]
  (-> data
      base58->bytes
      bytes->hex))


(defn hex-to-base58
  "Encodes a hex-string as a base58-string"
  [data]
  (assert (hex? data) "Input must be hexadecimal")
  (-> data
      hex->bytes
      bytes->base58))


(defn byte-array-to-base
  [data output-format]
  (let [data #?(:clj (byte-array data)
                :cljs data)]
    (case output-format
      :biginteger (bytes->biginteger data)
      :hex (bytes->hex data)
      :base64 (bytes->base64 data)
      :base58 (bytes->base58 data)
      :bytes data
      (throw (ex-info "Unsupported output-format"
                      {:data          data
                       :output-format output-format})))))


(defn base-to-byte-array
  "Convert a string of specified base to a byte-array"
  [data format]
  (case format
    :biginteger (biginteger->bytes data)
    :hex (hex->bytes data)
    :base64 (base64->bytes data)
    :base58 (base58->bytes data)
    :bytes #?(:clj  (byte-array data)
              :cljs data)
    (throw (ex-info "Unsupported format"
                    {:data   data
                     :format format}))))


(defn base-to-base
  "Convert one base into another"
  [data input-format output-format]
  (cond
    (nil? data)
    data

    (= input-format output-format)
    data

    (= [:base58 :hex]
       [input-format output-format])
    (base58-to-hex data)

    (= [:hex :base58]
       [input-format output-format])
    (hex-to-base58 data)

    :else
    (-> data
        (base-to-byte-array input-format)
        (byte-array-to-base output-format))))

