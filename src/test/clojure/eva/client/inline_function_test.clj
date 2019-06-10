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

(ns eva.client.inline-function-test
  (:require [clojure.test :refer :all]
            [eva.client.core :as eva]
            [eva.client.test-constants :refer :all]
            [eva.client.inline-function :as inline])
  (:import
           (com.workiva.eva.client TestEvaClient)))

(defn once-fixture [f]
  ; Need to create instance of EdnSerializer for inline print-method to work...
  (eva/start "url")
  (eva/stop)
  (eva/start (TestEvaClient.))
  (f)
  (eva/stop))

(use-fixtures :once once-fixture)

(def config {:ecs/url  URL
             :tenant   TENANT
             :category CATEGORY
             :label LABEL})

(deftest unit:inline-ident
  (let [conn (eva/connect config)
        db (eva/db conn)
        ident (inline/ident db 1)]
    (is (= (str ident) (format "#eva.client.service/inline { :fn ident :params [%s %s] }" db 1)))))

(deftest unit:inline-entid
  (let [conn (eva/connect config)
        db (eva/db conn)
        entid (inline/entid db :db.part/tx)]
    (is (= (str entid) (format "#eva.client.service/inline { :fn entid :params [%s %s] }" db :db.part/tx)))))

(deftest unit:inline-q
  (let [conn (eva/connect config)
        db (eva/db conn)
        query '[:find ?a :in $ ?b :where [?a :db/ident ?b]]
        q (inline/q query db :db.part/tx)]
    (is (= (str q) (format "#eva.client.service/inline { :fn query :params [\"%s\" [%s %s]] }"
                           query db :db.part/tx)))))

(deftest unit:inline-latest-t
  (let [conn (eva/connect config)
        latest-t (inline/latest-t conn)]
    (is (= (str latest-t) (format "#eva.client.service/inline { :fn latestT :params [ %s ] }" conn)))))

(deftest unit:inline-first
  (let [conn (eva/connect config)
        db (eva/db conn)
        query '[:find ?a :in $ ?b :where [?a :db/ident ?b]]
        inline-q (inline/q query db :db.part/tx)
        inline-first (inline/first inline-q)]
    (is (= (str inline-first) (format "#eva.client.service/inline { :fn first :params [ %s ] }" inline-q)))))

(deftest unit:inline-ffirst
  (let [conn (eva/connect config)
        db (eva/db conn)
        query '[:find ?a :in $ ?b :where [?a :db/ident ?b]]
        inline-q (inline/q query db :db.part/tx)
        inline-ffirst (inline/ffirst inline-q)]
    (is (= (str inline-ffirst) (format "#eva.client.service/inline { :fn ffirst :params [ %s ] }" inline-q)))))
