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

(ns examples.eva101.core
  (:require [eva.client.core :as eva]
            [eva.client.inline-function :as inline]))

(def config {:tenant "tenant"
             :category "category"
             :label "label"})

(def schema [
             {:db/id (eva/tempid :db.part/db)
              :db/ident :book/title
              :db/doc "Title of a book"
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one
              :db.install/_attribute :db.part/db}

             {:db/id (eva/tempid :db.part/db)
              :db/ident :book/year_published
              :db/doc "Date book was published"
              :db/valueType :db.type/long
              :db/cardinality :db.cardinality/one
              :db.install/_attribute :db.part/db}

             {:db/id (eva/tempid :db.part/db)
              :db/ident :book/author
              :db/doc "Author of a book"
              :db/valueType :db.type/ref
              :db/cardinality :db.cardinality/one
              :db.install/_attribute :db.part/db}

             {:db/id (eva/tempid :db.part/db)
              :db/ident :author/name
              :db/doc "Name of author"
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one
              :db.install/_attribute :db.part/db}
             ])

(def dataset
  [{:db/id (eva/tempid :db.part/user -1) :author/name "Martin Kleppman"}
   {:db/id (eva/tempid :db.part/user -2) :book/title "Designing Data-Intensive Applications" :book/year_published 2017 :book/author (eva/tempid :db.part/user -1)}

   {:db/id (eva/tempid :db.part/user -3) :author/name "Aurelien Geron"}
   {:db/id (eva/tempid :db.part/user -4) :book/title "Hands-On Machine Learning" :book/year_published 2017 :book/author (eva/tempid :db.part/user -3)}

   {:db/id (eva/tempid :db.part/user -5) :author/name "Wil van der Aalst"}
   {:db/id (eva/tempid :db.part/user -6) :book/title "Process Mining: Data Science in Action" :book/year_published 2016 :book/author (eva/tempid :db.part/user -5)}
   {:db/id (eva/tempid :db.part/user -7) :book/title "Modeling Business Processes: A Petri-Net Oriented Approach" :book/year_published 2011 :book/author (eva/tempid :db.part/user -5)}

   {:db/id (eva/tempid :db.part/user -8) :author/name "Edward Tufte"}
   {:db/id (eva/tempid :db.part/user -9) :book/title "The Visual Display of Quantitative Information" :book/year_published 2001 :book/author (eva/tempid :db.part/user -8)}
   {:db/id (eva/tempid :db.part/user -10) :book/title "Envisioning Information" :book/year_published 1990 :book/author (eva/tempid :db.part/user -8)}

   {:db/id (eva/tempid :db.part/user -11) :author/name "Ramez Elmasri"}
   {:db/id (eva/tempid :db.part/user -12) :book/title "Operating Systems: A Spiral Approach" :book/year_published 2009 :book/author (eva/tempid :db.part/user -11)}
   {:db/id (eva/tempid :db.part/user -13) :book/title "Fundamentals of Database Systems" :book/year_published 2006 :book/author (eva/tempid :db.part/user -11)}
   {:db/id (eva/tempid :db.part/user -14) :author/name "Steve McConnell"}
   {:db/id (eva/tempid :db.part/user -15) :book/title "Code Complete: A Practical Handbook of Software Construction" :book/year_published 2004 :book/author (eva/tempid :db.part/user -14)}
   {:db/id (eva/tempid :db.part/user -16) :book/title "Software Estimation: Demystifying the Black Art" :book/year_published 2006 :book/author (eva/tempid :db.part/user -14)}
   {:db/id (eva/tempid :db.part/user -17) :book/title "Rapid Development: Taming Wild Software Schedules" :book/year_published 1996 :book/author (eva/tempid :db.part/user -14)}
   {:db/id (eva/tempid :db.part/user -18) :book/title "Software Project Survival Guide" :book/year_published 1997 :book/author (eva/tempid :db.part/user -14)}
   {:db/id (eva/tempid :db.part/user -19) :book/title "After the Gold Rush: Creating a True Profession of Software Engineering" :book/year_published 1999 :book/author (eva/tempid :db.part/user -14)}

   {:db/id (eva/tempid :db.part/user -20)  :author/name "Don Miguel Ruiz"}
   {:db/id (eva/tempid :db.part/user -21) :book/title "The Four Agreements: A Practical Guide to Personal Freedom" :book/year_published 2011 :book/author (eva/tempid :db.part/user -20)}

   {:db/id (eva/tempid :db.part/user -22) :author/name "Charles Petzold"}
   {:db/id (eva/tempid :db.part/user -23) :book/title "Code: The Hidden Language of Computer Hardware and Software"
    :book/year_published 2000 :book/author (eva/tempid :db.part/user -22)}

   {:db/id (eva/tempid :db.part/user -24) :author/name "Anil Maheshwari"}
   {:db/id (eva/tempid :db.part/user -25) :book/title "Data Analytics Made Accessible" :book/year_published 2014 :book/author (eva/tempid :db.part/user -24)}

   {:db/id (eva/tempid :db.part/user -26) :author/name "Jeremy Anderson"}
   {:db/id (eva/tempid :db.part/user -27) :book/title "Professional Clojure" :book/year_published 2016 :book/author (eva/tempid :db.part/user -26)}])


(defn -main []
  (eva/start "http://localhost:8080")
  (def conn (eva/connect config))

  ; Transact Schema
  (println (str "Transact schema: "
                @(eva/transact conn schema)
                "\n"))

  ; Transact first book
  (def first-book [[:db/add (eva/tempid :db.part/user) :book/title "First Book"]])

  (println (str "Transact first book: "
                @(eva/transact conn first-book)
                "\n"))

  (def get-book '[:find ?b
                  :where [?b :book/title "First Book"]])

  ; Create database to be used in queries, etc.
  (def db (eva/db conn))

  ; Query for first book added.
  (println (str "Query for first book: "
                (eva/q get-book db)
                "\n"))

  ; Transact datasets
  (eva/transact conn dataset)

  ; Find book titles released in 2017
  (println (str "Books released in 2017: "
                (eva/q '[:find ?title :in $ :where
                         [?b :book/year_published 2017]
                         [?b :book/title ?title]], db))
           "\n")

  ; Find author of a particular book
  (println (str "Find author of a particular book: "
           (eva/q '[:find ?name
                    :where
                    [?b :book/title "Designing Data-Intensive Applications"]
                    [?b :book/author ?a]
                    [?a :author/name ?name]] db)
           "\n"))

  ; Get Author Id of "Designing Data-Intensive Applications"
  (println (str "Get all the books of a particular author: "
                (eva/q '[:find ?a
                         :where
                         [?b :book/title "Designing Data-Intensive Applications"]
                         [?b :book/author ?a]] db)
                "\n"))

  ; Pull first book by Id
  (println (str "Pull first book by id: "
                (eva/pull db '[*] 8796093023236)
                "\n"))

  ; Query all book names
  (println (str "Query all book names: "
                (eva/q '[:find ?name
                         :where
                         [_ :book/title ?name]]
                       db)
                "\n"))

  ; Find when a book was commited to the database
  (println (str "Find when a book was commited to the database: "
                (eva/q '[:find ?timestamp
                         :where
                         [_ :book/title "Process Mining: Data Science in Action" ?tx]
                         [?tx :db/txInstant ?timestamp]]
                       db)
                "\n"))

  ; Find book titles and their year prior to 2005
  (println (str "Find book titles and their year prior to 2005: "
                (eva/q '[:find ?book ?year
                         :where
                         [?b :book/title ?book]
                         [?b :book/year_published ?year]
                         [(< ?year 2005)]]
                       db)
                "\n"))

  ; Find books older than "Software Project Survival Guide"
  (println (str "Find books older than \"Software Project Survival Guide\""
                (eva/q '[:find ?book ?y1
                         :where
                         [?b1 :book/title ?book]
                         [?b1 :book/year_published ?y1]
                         [?b2 :book/title "Software Project Survival Guide"]
                         [?b2 :book/year_published ?y2]
                         [(< ?y1 ?y2)]]
                       db)
                "\n"))

  ; Query for the oldest book
  (println (str "Query for the oldest book: "
                (eva/q '[:find (min ?year)
                         :where
                         [_ :book/year_published ?year]]
                       db)
                "\n"))

  ; Get books with rules
  (def rules '[[(book-author ?book ?name)
                [?b :book/title ?book]
                [?b :book/author ?a]
                [?a :author/name ?name]]])

  (println (str "Get books with rules: "
                (eva/q '[:find ?name
                         :in $ %
                         :where
                         (book-author "Modeling Business Processes: A Petri-Net Oriented Approach" ?name)]
                       db rules)))

  ; Invoke CAS
  (println (str "Invoke CAS: "
                (eva/invoke db :db.fn/cas db 0 :db/doc "The default database partition." "Testing")
                "\n"))

  ; Inline functions!
  ; ffirst and query
  (def ffirstQuery (inline/ffirst
                     (inline/q '[:find ?tx :in $ ?t :where [?b :book/title ?t ?tx]]
                               db "First Book")))

  (def firstBookDb (eva/as-of db ffirstQuery))

  (println (str "ffirst and query inline functions: "
                (eva/q '[:find ?b :in $ ?t :where [?b :book/title ?t]] firstBookDb "First Book")
                "\n"))

  ; Inline Ident
  (println (str "Ident inline: "
                (eva/q '[:find ?ident :in $ ?my-ident :where [?my-ident :db/ident ?ident]]
                       db (inline/ident db 1))
                "\n"))

  ; Inline entid
  (println (str "Entid inline: "
                (eva/q '[:find ?ident :in $ ?e :where [?e :db/ident ?ident]]
                       db (inline/entid db :db.part/tx))
                "\n"))

  ; Inline latest-t
  (def latest-t-db (eva/as-of db (inline/latest-t conn)))

  (println (str "latestT inline: "
                (eva/q '[:find ?e :in $ :where [?e :db/ident :db.part/tx]]
                       latest-t-db)
                "\n"))

  (println (str "to-tx-eid: "
                (eva/to-tx-eid 1)
                "\n"))

  (println (str "ident: "
                (eva/ident db 1)
                "\n"))

  (println (str "entid: "
                (eva/entid db :db.fn/cas)
                "\n"))

  (eva/stop))
