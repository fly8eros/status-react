(ns status-im.ethereum.macros
  (:require [clojure.string :as string]
            [clojure.java.io :as io]))

(defn token-icon-path
  [network symbol]
  (let [s     (str "./resources/images/tokens/" (name network) "/" (name symbol) ".png")
        s-js  (str "." s)
        image (gensym)]
    (if (.exists (io/file s))
      `(let [~image (atom nil)]
         (fn []
           (or @~image
               (reset! ~image (js/require ~s-js)))))
      `(let [~image (atom nil)]
         (fn []
           (or
            @~image
            (reset! ~image
                    (js/require "../resources/images/tokens/default-token.png"))))))))

(defn- token->icon [network {:keys [icon symbol]}]
  ;; Tokens can define their own icons.
  ;; If not try to make one using a local image as resource, if it does not exist fallback to default.
  (or icon (token-icon-path network symbol)))

(defmacro resolve-icons
  "In react-native arguments to require must be static strings.
   Resolve all icons at compilation time so no variable is used."
  [network tokens]
  (mapv #(-> %
             (assoc-in [:icon :source] (token->icon network %))
             (update :address string/lower-case))
        tokens))

(defn network->icon [network]
  (let [s     (str "../resources/images/tokens/" (name network) "/0-native.png")
        image (gensym)]
    (if (.exists (io/file (subs s 1)))
      `(let [~image (atom nil)]
         (fn []
           (or @~image
               (reset! ~image (js/require ~s)))))
      `(let [~image (atom nil)]
         (fn []
           (or
            @~image
            (reset! ~image
                    (js/require "../resources/images/tokens/default-native.png"))))))))

(defmacro resolve-native-currency-icons
  "In react-native arguments to require must be static strings.
   Resolve all icons at compilation time so no variable is used."
  [all-native-currencies]
  (into {}
        (map (fn [[network native-currency]]
               [network (assoc-in native-currency
                                  [:icon :source]
                                  (network->icon network))]) all-native-currencies)))
