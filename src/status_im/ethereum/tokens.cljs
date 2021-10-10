(ns status-im.ethereum.tokens
  (:require [clojure.string :as string])
  (:require-macros
   [status-im.ethereum.macros :as ethereum.macros :refer [resolve-icons]]))

(def default-native-currency
  (memoize
   (fn []
     {:name     "Native"
      :symbol   :ETH
      :decimals 18
      :icon     {:source (js/require "../resources/images/tokens/default-native.png")}})))

(def snt-icon-source (js/require "../resources/images/tokens/mainnet/EROS.png"))

(def all-native-currencies
  (ethereum.macros/resolve-native-currency-icons
   {:mainnet {:name           "GOD"
              :symbol         :ETH
              :symbol-display :GOD
              :decimals       18}
    :bsc     {:name           "BNB"
              :symbol         :ETH
              :symbol-display :BNB
              :decimals       18}
    :heco    {:name           "HT"
              :symbol         :ETH
              :symbol-display :HT
              :decimals       18}
    :testnet {:name           "Ropsten Ether"
              :symbol         :ETH
              :symbol-display :ETHro
              :decimals       18}
    :rinkeby {:name           "Rinkeby Ether"
              :symbol         :ETH
              :symbol-display :ETHri
              :decimals       18}
    :poa     {:name           "POA"
              :symbol         :ETH
              :symbol-display :POA
              :decimals       18}
    :xdai    {:name            "xDAI"
              :symbol          :ETH
              :symbol-display  :xDAI                        　
              :symbol-exchange :DAI
              :decimals        18}}))

(def native-currency-symbols
  (set (map #(-> % val :symbol) all-native-currencies)))

(defn native-currency [chain]
  (-> (get all-native-currencies chain (default-native-currency))))

(defn ethereum? [symbol]
  (native-currency-symbols symbol))

;; NOTE(goranjovic) - fields description:
;;
;; - address - token contract address
;; - symbol - token identifier, must be unique within network
;; - name - token display name
;; - decimals - the maximum number of decimals (raw balance must be divided by 10^decimals to get the actual amount)
;; - nft? - set to true when token is an ERC-781 collectible
;; - hidden? - when true, token is not displayed in any asset selection screens, but will be displayed properly in
;;             transaction history (setting this field is a form of "soft" token removal).
;; - skip-decimals-check? - some tokens do not include the decimals field, which is compliant with ERC-20 since it is
;;;     and optional field. In that case we are explicitly skipping this step in order not to raise a false error.
;;;     We have this explicit flag for decimals and not for name and symbol because we can't tell apart unset decimals
;;;     from 0 decimals case.

(def all-default-tokens
  {:mainnet
   (resolve-icons :mainnet
                  [{:symbol   :EROS
                    :name     "EROS"
                    :address  "0x48b978a8250678f95663e8fbe3a1339fcd4fe943"
                    :decimals 18}
                   {:symbol   :GOD
                    :name     "GOD"
                    :address  "0x99dfa8964e635ebdbe347b02422083bfc3f20da0"
                    :decimals 18}
                   {:symbol   :USDT
                    :name     "USDT"
                    :address  "0x98A3d8D7F90DD73CbA99B3b1315f5bc9d96280c6"
                    :decimals 18}])
   :bsc
   (resolve-icons :bsc
                  [{:symbol   :USDT
                    :name     "USDT"
                    :address  "0x55d398326f99059ff775485246999027b3197955"
                    :decimals 18}])
   :heco
   (resolve-icons :heco
                  [{:symbol   :USDT
                    :name     "USDT"
                    :address  "0xa71edc38d189767582c38a3145b5873052c3e47a"
                    :decimals 18}])
   :testnet
   (resolve-icons :testnet
                  [{:name     "Status Test Token"
                    :symbol   :STT
                    :decimals 18
                    ;;NOTE(goranjovic): intentionally checksummed for purposes of testing
                    :address  "0xc55cf4b03948d7ebc8b9e8bad92643703811d162"}
                   {:name     "Handy Test Token"
                    :symbol   :HND
                    :decimals 0
                    :address  "0xdee43a267e8726efd60c2e7d5b81552dcd4fa35c"}
                   {:name     "Lucky Test Token"
                    :symbol   :LXS
                    :decimals 2
                    :address  "0x703d7dc0bc8e314d65436adf985dda51e09ad43b"}
                   {:name     "Adi Test Token"
                    :symbol   :ADI
                    :decimals 7
                    :address  "0xe639e24346d646e927f323558e6e0031bfc93581"}
                   {:name     "Wagner Test Token"
                    :symbol   :WGN
                    :decimals 10
                    :address  "0x2e7cd05f437eb256f363417fd8f920e2efa77540"}
                   {:name     "Modest Test Token"
                    :symbol   :MDS
                    :decimals 18
                    :address  "0x57cc9b83730e6d22b224e9dc3e370967b44a2de0"}])

   :rinkeby
   (resolve-icons :rinkeby
                  [{:name     "Moksha Coin"
                    :symbol   :MOKSHA
                    :decimals 18
                    :address  "0x6ba7dc8dd10880ab83041e60c4ede52bb607864b"}
                   {:symbol  :KDO
                    :nft?    true
                    :name    "KudosToken"
                    :address "0x93bb0afbd0627bbd3a6c72bc318341d3a22e254a"}
                   {:symbol   :WIBB
                    :name     "WIBB"
                    :address  "0x7d4ccf6af2f0fdad48ee7958bcc28bdef7b732c7"
                    :decimals 18}
                   {:name     "Status Test Token"
                    :symbol   :STT
                    :decimals 18
                    :address  "0xc55cf4b03948d7ebc8b9e8bad92643703811d162"}])

   :xdai
   (resolve-icons :xdai
                  [{:name     "buffiDai"
                    :symbol   :BUFF
                    :decimals 18
                    :address  "0x3e50bf6703fc132a94e4baff068db2055655f11b"}])

   :custom []})

(defn normalize-chain [tokens]
  (reduce (fn [acc {:keys [address] :as token}]
            (assoc acc address token))
          {}
          tokens))

(def all-tokens-normalized
  (reduce-kv (fn [m k v]
               (assoc m k (normalize-chain v)))
             {}
             all-default-tokens))

(defn nfts-for [all-tokens]
  (filter :nft? (vals all-tokens)))

(defn sorted-tokens-for [all-tokens]
  (->> (vals all-tokens)
       (filter #(not (:hidden? %)))
       (sort #(compare (string/lower-case (:name %1))
                       (string/lower-case (:name %2))))))

(defn symbol->token [all-tokens symbol]
  (some #(when (= symbol (:symbol %)) %) (vals all-tokens)))

(defn address->token [all-tokens address]
  (get all-tokens (string/lower-case address)))

(defn asset-for [all-tokens chain symbol]
  (let [native-coin (native-currency chain)]
    (if (= (:symbol native-coin) symbol)
      native-coin
      (symbol->token all-tokens symbol))))

(defn symbol->icon [sym]
  (:icon (first (filter #(= sym (:symbol %))
                        (:mainnet all-default-tokens)))))
