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

(ns eva.client.core-test
  (:require [clojure.test :refer :all]
            [eva.client.test-constants :refer :all]
            [eva.client.core :as eva-client]
            [eva.readers :refer :all])
  (:import (com.workiva.eva.client TestEvaClient Database EdnSerializer)))

(def config {:tenant TENANT
             :category CATEGORY
             :label LABEL})

(defn once-fixture [f]
  (eva-client/start (TestEvaClient.))
  (f)
  (eva-client/stop))

(use-fixtures :once once-fixture)

(deftest unit:connect
         (let [conn (eva-client/connect config)]
               (is (= (.getTenant conn) TENANT))
               (is (= (.getCategory conn) CATEGORY))
               (is (= (.getLabel conn) LABEL))))

(deftest unit:db
  (let [conn (eva-client/connect config)
        db (eva-client/db conn)
        db-str (str db)]
    (is (= (.getTenant db) TENANT))
    (is (= (.getCategory db) CATEGORY))
    (is (= (.getLabel db) LABEL))
    (is (false? (.contains (str db) ":sync-db true")))))

(deftest unit:sync-db
  (let [conn (eva-client/connect config)
        db (eva-client/sync-db conn)]
    (is (= (.getTenant db) TENANT))
    (is (= (.getCategory conn) CATEGORY))
    (is (= (.getLabel conn) LABEL))
    (is (true? (.contains (str db) ":sync-db true")))))

(deftest unit:as-of-t-and-basis-t-and-snapshot-t
  (let [serializer (EdnSerializer. "tenant" "category")
        db-string "#eva.client.service/snapshot-ref { :label \"label\" :as-of 1 :as-of-t 2 :basis-t 3 :snapshot-t 4 }"
        db (.deserialize serializer db-string)]
    (is (= (eva-client/as-of-t db) 2))
    (is (= (eva-client/basis-t db) 3))
    (is (= (eva-client/snapshot-t db) 4))))


(deftest unit:q
  (let [conn (eva-client/connect config)
        db (eva-client/db conn)
        query '[:find ?e :in $ ?ident :where [?e :db/ident ?ident]]
        results (eva-client/q query
                       db :db.part/tx)]
    (is (= query (first results)))
    (is (= 2 (count (second results))))
    (let [args (second results)]
      (is (= db (first args)))
      (is (= :db.part/tx (second args))))))

(deftest unit:transact
  (let [tx '[[:db/add #db/id[:db.part/user] :book/title "First Book"]]
        conn (eva-client/connect config)
        results @(eva-client/transact conn tx)]
    (is (= (get results "tx") tx))
    (is (= (get results "connection") conn))))

(deftest unit:pull
  (eva-client/start (TestEvaClient.))
  (let [conn (eva-client/connect config)
        db (eva-client/db conn)
        results (eva-client/pull db [*] 123)]
    (is (= (get results "db") db))
    (is (= (get results "pattern" [*])))
    (is (= (get results "id") 123))))

(deftest unit:pull-many
  (let [conn (eva-client/connect config)
        db (eva-client/db conn)
        results (eva-client/pull-many db [*] 123 456)]
    (is (= (first results) db))
    (is (= (second results) [*]))
    (is (= (nth results 2) [123 456]))
    (is (= (count (nth results 3)) 0))))

(deftest unit:invoke
  (let [conn (eva-client/connect config)
        db (eva-client/db conn)
        results (eva-client/invoke db :db.fn/cas db 0 :db/doc
                                   "Test1" "Test2")
        args (get results "args")]
    (is (= (get results "db") db))
    (is (= (get results "function") :db.fn/cas))
    (is (= (count args) 5))
    (is (= (first args) db))
    (is (= (second args) 0))
    (is (= (nth args 2) :db/doc))
    (is (= (nth args 3) "Test1"))
    (is (= (nth args 4) "Test2"))))

(deftest unit:tempid
  (let [id (eva-client/tempid :db.part/user 123)]
    (is (= id #db/id[:db.part/user 123]))))
