;; Copyright 2018-2019 Workiva Inc.
;; 
;; Licensed under the Apache License, Version 2.0 (the "License");
;; you may not use this file except in compliance with the License.
;; You may obtain a copy of the License at
;; 
;;     http://www.apache.org/licenses/LICENSE-2.0
;; 
;; Unless required by applicable law or agreed to in writing, software
;; distributed under the License is distributed on an "AS IS" BASIS,
;; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
;; See the License for the specific language governing permissions and
;; limitations under the License.

(ns eva.client.core
    (:require [eva.api :as eva])
    (:import
      (com.workiva.eva.client Client Connection Database EvaClient)))

(defn q [query & args]
      (Client/query query (into-array Object args)))

(defn connect [config]
      (Client/connect config))

(defn db [^Connection conn]
      (.db conn))

(defn sync-db [^Connection conn]
      (.syncDb conn))

(defn as-of [^Database db tx-or-inline]
      (.asOf db tx-or-inline))

(defn pull [^Database db pattern eid]
      (.pull db pattern eid))

(defn pull-many [^Database db pattern & eids]
      (.pullMany db pattern eids))

(defn transact [^Connection conn tx]
      (.transactAsync conn tx))

(defn as-of-t [^Database db]
      (.asOfT db))

(defn basis-t [^Database db]
      (.basisT db))

(defn snapshot-t [^Database db]
      (.snapshotT db))

(defn latest-t [^Connection conn]
      (.latestT conn))

(defn invoke [^Database db dbFn & args]
      (.invoke db dbFn (into-array Object args)))

(defn to-tx-eid [tx]
      (Client/toTxEid tx))

(defn entid [^Database db ident]
      (.entid db ident))

(defn ident [^Database db entid]
      (.ident db entid))

(defn tempid
      ([k]
        (eva/tempid k))
      ([k num]
        (eva/tempid k num)))

(defn start
      ([url-or-client]
        (EvaClient/start url-or-client)))

(defn stop []
      (EvaClient/stop))
