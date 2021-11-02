(ns status-im.ui.screens.browser.networks
  (:require [status-im.ui.components.list.views :as list]
            [status-im.i18n.i18n :as i18n]
            [status-im.ui.components.react :as react]
            [quo.core :as quo]
            [status-im.ui.components.chat-icon.screen :as chat-icon]
            [re-frame.core :as re-frame]))

(defn render-network [network _ _ dapps-network]
  [quo/list-item
   (merge {:accessory :radio
           :active    (= (:networkId dapps-network) (:networkId network))
           :icon      [chat-icon/custom-icon-view-list (:name network)]
           :title     (:name network)
           :subtitle  (str (:networkId network))}
          (when (not= (:networkId dapps-network) (:networkId network))
            {:on-press  #(re-frame/dispatch [:dapps-network-selected network])}))])

(defn networks-list [networks network]
  (fn []
    [react/view {:flex 1}
     [react/text {:style {:margin 16 :text-align :center}}
      (i18n/label :t/select-network-dapp)]
     [list/flat-list {:data        networks
                      :key-fn      :networkId
                      :render-data network
                      :render-fn   render-network}]]))
