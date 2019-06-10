// Copyright 2018-2019 Workiva Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package examples.eva101;

public class Queries {
  static String queryFirstBook = "[:find ?b :in $ ?t :where [?b :book/title ?t]]";

  static String queryBooksFrom2017 =
      "[:find ?title :in $ ?year\n"
          + "         :where\n"
          + "         [?b :book/year_published ?year]\n"
          + "         [?b :book/title ?title]]";

  static String queryAuthorDataIntensiveApplications =
      "[:find ?name\n"
          + "         :where\n"
          + "         [?b :book/title \"Designing Data-Intensive Applications\"]\n"
          + "         [?b :book/author ?a]\n"
          + "         [?a :author/name ?name]]";

  static String queryAuthorIdDataIntensiveApplications =
      "[:find ?a\n"
          + "         :where\n"
          + "         [?b :book/title \"Designing Data-Intensive Applications\"]\n"
          + "         [?b :book/author ?a]]";

  static String queryBooksFromAuthor =
      "[:find ?books\n"
          + "         :where\n"
          + "         [?b :book/title ?books]\n"
          + "         [?b :book/author ?a]\n"
          + "         [?a :author/name \"Steve McConnell\"]]";

  static String queryAllBookNames =
      "[:find ?name\n" + "         :where\n" + "         [_ :book/title ?name]]";

  static String findWhenBookCommitted =
      "[:find ?timestamp\n"
          + "         :where\n"
          + "         [_ :book/title \"Process Mining: Data Science in Action\" ?tx]\n"
          + "         [?tx :db/txInstant ?timestamp]]";

  static String findBooksBefore2005 =
      "[:find ?book ?year\n"
          + "         :where\n"
          + "         [?b :book/title ?book]\n"
          + "         [?b :book/year_published ?year]\n"
          + "         [(< ?year 2005)]]";

  static String findBooksOlderThanSurvivalGuide =
      "[:find ?book ?y1\n"
          + "         :where\n"
          + "         [?b1 :book/title ?book]\n"
          + "         [?b1 :book/year_published ?y1]\n"
          + "         [?b2 :book/title \"Software Project Survival Guide\"]\n"
          + "         [?b2 :book/year_published ?y2]\n"
          + "         [(< ?y1 ?y2)]]";

  static String queryForOldestBook =
      "[:find (min ?year)\n" + "         :where\n" + "         [_ :book/year_published ?year]]";

  static String findBooksWithRules =
      "[:find ?name\n"
          + "         :in $ %\n"
          + "         :where\n"
          + "         (book-author \"Modeling Business Processes: A Petri-Net Oriented Approach\" ?name)]";

  static String queryRule =
      "[[(book-author ?book ?name)\n"
          + "              [?b :book/title ?book]\n"
          + "              [?b :book/author ?a]\n"
          + "              [?a :author/name ?name]]]";
}
