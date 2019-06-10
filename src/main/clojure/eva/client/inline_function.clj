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

(ns eva.client.inline-function
    (:import (com.workiva.eva.client Connection Database)
      (com.workiva.eva.client.inline InlineFunction UtilFunctions PeerFunctions
                                     ConnectionFunctions DatabaseFunctions)))

(defmethod print-method InlineFunction [func ^java.io.Writer w]
           (.write w (.toString func)))

; Util functions
(defn ffirst [^InlineFunction fn]
      (UtilFunctions/ffirst fn))

(defn first [^InlineFunction fn]
      (UtilFunctions/first fn))

; Peer functions
(defn q [q & args]
      (PeerFunctions/query q (into-array Object args)))

; Connection functions
(defn latest-t [^Connection conn]
      (ConnectionFunctions/latestT conn))

; Database functions
(defn ident [^Database db id]
      (DatabaseFunctions/ident db id))

(defn entid [^Database db ident]
      (DatabaseFunctions/entid db ident))
