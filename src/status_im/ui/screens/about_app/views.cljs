(ns status-im.ui.screens.about-app.views
  (:require [re-frame.core :as re-frame]
            [status-im.i18n.i18n :as i18n]
            [quo.design-system.colors :as colors]
            [status-im.ui.components.copyable-text :as copyable-text]
            [status-im.ui.components.icons.icons :as icons]
            [status-im.ui.components.webview :refer [webview]]
            [status-im.constants :refer [privacy-policy-link principles-link terms-of-service-link]]
            [quo.core :as quo]
            [status-im.ui.components.react :as react])
  (:require-macros [status-im.utils.views :as views]))

(views/defview about-app []
  (views/letsubs [app-version  [:get-app-short-version]
                  node-version [:get-app-node-version]]
    [react/scroll-view
     [copyable-text/copyable-text-view
      {:copied-text app-version}
      [quo/list-item
       {:size                :small
        :accessibility-label :app-version
        :title               (i18n/label :t/version)
        :accessory           :text
        :accessory-text      app-version}]]
     [copyable-text/copyable-text-view
      {:copied-text (clojure.string/replace node-version "StatusIM" "Eros")}
      [quo/list-item
       {:size                :small
        :accessibility-label :node-version
        :title               (i18n/label :t/node-version)
        :accessory           :text
        :accessory-text      (clojure.string/replace node-version "StatusIM" "Eros")}]]]))

(views/defview learn-more-sheet []
  (views/letsubs [{:keys [title content]} [:bottom-sheet/options]]
    [react/view {:style {:padding-left  16 :padding-top    16
                         :padding-right 34 :padding-bottom 0}}
     [react/view {:style {:align-items :center :flex-direction :row :margin-bottom 16}}
      [icons/icon :main-icons/info {:color           colors/blue
                                    :container-style {:margin-right 13}}]
      [react/text {:style {:typography :title-bold}} title]]
     [react/text {:style {:color colors/gray}} content]]))

(def learn-more
  {:content learn-more-sheet})

(views/defview privacy-policy []
  [webview
   {:source              {:uri privacy-policy-link}
    :java-script-enabled true}])

(views/defview tos []
  [webview
   {:source              {:uri terms-of-service-link}
    :java-script-enabled true}])

(views/defview principles []
  [webview
   {:source              {:uri principles-link}
    :java-script-enabled true}])
