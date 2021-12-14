(ns status-im.ui.screens.wallet.account-private-key.events
  (:require [status-im.utils.fx :as fx]
            [re-frame.core :as re-frame]
            [status-im.utils.security :as security]
            [status-im.native-module.core :as status]
            [status-im.ethereum.core :as ethereum]
            [status-im.utils.types :as types]
            [taoensso.timbre :as log]
            [clojure.string :as clojure.string]
            [status-im.i18n.i18n :as i18n]))

(defn safe-blank? [s]
  (or (not s)
      (clojure.string/blank? s)))

(re-frame/reg-fx
 ::export-private-key
 (fn [{:keys [address callback masked-password]}]
   (let [hashed-password
         (-> masked-password
             security/safe-unmask-data
             ethereum/sha3)]
     (status/verify
      address
      hashed-password
      (fn [result]
        (let [{:keys [error]} (types/json->clj result)]
          (log/info "[export-private-key] verify-password" result error)
          (if-not (safe-blank? error)
            (callback :wrong-password nil)
            (status/export-private-key
             address
             hashed-password
             (fn [result]
               (let [{:keys [error]} (types/json->clj result)]
                 (callback error result)))))))))))

(fx/defn export-private-key
  {:events [::export-private-key]}
  [{:keys [db] :as cofx} address password]
  (log/info "[export-private-key]" address)
  {:db (dissoc db :wallet/export-private-key-error)
   ::export-private-key
       {:masked-password password
        :address         address
        :callback
                         (fn [error result]
                             (log/info "[export-private-key] callback" error)
                             (if (safe-blank? error)
                               (re-frame/dispatch [::on-export-private-key-success result])
                               (re-frame/dispatch [::on-export-private-key-failure error])))}})

(fx/defn on-export-private-key-success
  {:events [::on-export-private-key-success]}
  [_ private-key]
  (log/info "[export-private-key] on-success")
  (re-frame/dispatch [:show-popover {:view        :display-private-key
                                     :private-key private-key}]))

(fx/defn on-export-private-key-failure
  {:events [::on-export-private-key-failure]}
  [{:keys [db]} error]
  (log/info "[export-private-key] on-failure" error)
  {:db (assoc db :wallet/export-private-key-error error)})


