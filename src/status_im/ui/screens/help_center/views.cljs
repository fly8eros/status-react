(ns status-im.ui.screens.help-center.views
  (:require [re-frame.core :as re-frame]
            [status-im.i18n.i18n :as i18n]
            [quo.core :as quo]
            [status-im.ui.components.react :as react]
            [status-im.ui.components.list.views :as list]
            [status-im.constants :as constants]))

(def data
  [
   {:size                :small
    :title               (i18n/label :t/request-feature)
    :accessibility-label :request-a-feature-button
    :on-press
    #(re-frame/dispatch [:chat.ui/start-public-chat
                         "eros-support"
                         {:navigation-reset? false}])
    :chevron             true}
   {:size                :small
    :title               (i18n/label :t/submit-bug)
    :accessibility-label :submit-bug-button
    :on-press
                         #(re-frame/dispatch [:logging.ui/send-logs-pressed])
    :chevron             true}])

(defn help-center []
  [list/flat-list
   {:data      data
    :key-fn    (fn [_ i] (str i))
    :render-fn quo/list-item}])
