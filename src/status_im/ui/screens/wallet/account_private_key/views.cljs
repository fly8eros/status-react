(ns status-im.ui.screens.wallet.account-private-key.views
  (:require [status-im.ui.components.react :as react]
            [quo.core :as quo]
            [re-frame.core :as re-frame]
            [status-im.i18n.i18n :as i18n]
            [reagent.core :as reagent]
            [status-im.utils.security :as security]
            [status-im.ui.components.copyable-text :as copyable-text]
            [status-im.utils.handlers :refer [>evt <sub]]
            [status-im.ui.screens.wallet.account-private-key.events :as events]))

(defn valid-password? [password]
  (>= (count password) 6))

(defn hide-sheet-and-dispatch [event]
      (>evt [:bottom-sheet/hide])
      (>evt event))

(defn on-export-private-key [address password]
  #(do
     (re-frame/dispatch
      [::events/export-private-key address @password])
     (reset! password nil)))

(defn validate-password []
  (let [password       (reagent/atom nil)
        text-input-ref (atom nil)]
    (fn []
      (let [{:keys [address color path type] :as account} @(re-frame/subscribe [:multiaccount/current-account])
            error          @(re-frame/subscribe [:wallet/export-private-key-error])]
        (when (and @text-input-ref error (not @password))
          (.clear ^js @text-input-ref))
        [react/keyboard-avoiding-view {:style {:flex 1}}
         [react/scroll-view {:style {:flex 1}}
           [quo/text-input
             {:style             {:margin-horizontal 36
                                  :margin-top        36}
              :placeholder       (i18n/label :t/export-private-key-password-placeholder)
              :show-cancel       false
              :secure-text-entry true
              :return-key-type   :next
              :on-submit-editing nil
              :auto-focus        true
              :on-change-text    #(reset! password (security/mask-data %))
              :bottom-value      36
              :get-ref           #(reset! text-input-ref %)
              :error             (when (and error (not @password))
                                       (if (= :wrong-password error)
                                         (i18n/label :t/wrong-password)
                                         (str error)))}]]
         [react/view {:style {:align-items :center}}
          [quo/separator]
          [react/view
           {:style {:margin-vertical 8}}
           [quo/button {:on-press            (on-export-private-key address password)
                        :theme               :main
                        :accessibility-label :export-private-key-confirm
                        :disabled ((complement valid-password?) @password)}
            (i18n/label :t/export-key)]]]]))))

(defn display-private-key []
      (let [{:keys [private-key]} (<sub [:popover/popover])]
           [react/view {:style {:padding-top        16
                                :padding-horizontal 16}}
            [quo/text {:size  :x-large
                       :align :center}
             (i18n/label :t/account-private-key)]
            [copyable-text/copyable-text-view
             {:container-style {:padding-vertical 12}
              :copied-text     private-key}
             [quo/text {:number-of-lines     1
                        :ellipsize-mode      :middle
                        :accessibility-label :chat-key
                        :monospace           true}
              private-key]]]))