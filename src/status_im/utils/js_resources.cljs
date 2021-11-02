(ns status-im.utils.js-resources
  (:require-macros [status-im.utils.slurp :refer [slurp]])
  (:require [status-im.utils.config :as config]
            [status-im.utils.types :as types]))

(def provider-file (slurp "resources/js/provider.js"))
(defn ethereum-provider [network-id networks]
  (str "window.statusAppNetworkId = " network-id ";"
       "window.statusAppNetworks = " (types/clj->json networks) ";"
       (when config/debug-webview? "window.statusAppDebug = true;")
       provider-file))
