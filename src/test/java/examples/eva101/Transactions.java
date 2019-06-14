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

public class Transactions {
  static String defaultSchema =
      "[\n"
          + "{:db/id #db/id [:db.part/user]\n"
          + " :db/ident :book/title\n"
          + " :db/doc \"Title of a book\"\n"
          + " :db/valueType :db.type/string\n"
          + " :db/cardinality :db.cardinality/one\n"
          + " :db.install/_attribute :db.part/db}\n"
          + "\n"
          + "{:db/id #db/id [:db.part/user]\n"
          + " :db/ident :book/year_published\n"
          + " :db/doc \"Date book was published\"\n"
          + " :db/valueType :db.type/long\n"
          + " :db/cardinality :db.cardinality/one\n"
          + " :db.install/_attribute :db.part/db}\n"
          + "\n"
          + "{:db/id #db/id [:db.part/user]\n"
          + " :db/ident :book/author\n"
          + " :db/doc \"Author of a book\"\n"
          + " :db/valueType :db.type/ref\n"
          + " :db/cardinality :db.cardinality/one\n"
          + " :db.install/_attribute :db.part/db}\n"
          + "\n"
          + "{:db/id #db/id [:db.part/user]\n"
          + " :db/ident :author/name\n"
          + " :db/doc \"Name of author\"\n"
          + " :db/valueType :db.type/string\n"
          + " :db/cardinality :db.cardinality/one\n"
          + " :db.install/_attribute :db.part/db}\n"
          + " ]";

  static String addBook =
      "["
          + "\t[:db/add #db/id [:db.part/user] :book/title \"First Book\"]\n"
          + "\t[:db/add #db/id [:db.part/tx] :author/name \"Martin Name\"]\n"
          + "]";

  static String addManyBooks =
      "[{:db/id #db/id [:db.part/user -1] :author/name \"Martin Kleppman\"}\n"
          + "{:db/id #db/id [:db.part/user -2] :book/title \"Designing Data-Intensive Applications\" :book/year_published 2017 :book/author #db/id[:db.part/user -1]}\n"
          + "\n"
          + "{:db/id #db/id [:db.part/user -3] :author/name \"Aurelien Geron\"}\n"
          + "{:db/id #db/id [:db.part/user -4] :book/title \"Hands-On Machine Learning\" :book/year_published 2017 :book/author #db/id[ :db.part/user -3]}\n"
          + "\n"
          + "{:db/id #db/id [:db.part/user -5] :author/name \"Wil van der Aalst\"}\n"
          + "{:db/id #db/id [:db.part/user -6] :book/title \"Process Mining: Data Science in Action\" :book/year_published 2016 :book/author #db/id[ :db.part/user -5]}\n"
          + "{:db/id #db/id [:db.part/user -7] :book/title \"Modeling Business Processes: A Petri-Net Oriented Approach\" :book/year_published 2011 :book/author #db/id[ :db.part/user -5]}\n"
          + "\n"
          + "{:db/id #db/id [:db.part/user -8] :author/name \"Edward Tufte\"}\n"
          + "{:db/id #db/id [:db.part/user -9] :book/title \"The Visual Display of Quantitative Information\" :book/year_published 2001 :book/author #db/id[ :db.part/user -8]}\n"
          + "{:db/id #db/id [:db.part/user -10] :book/title \"Envisioning Information\" :book/year_published 1990 :book/author #db/id[ :db.part/user -8]}\n"
          + "\n"
          + "{:db/id #db/id [:db.part/user -11] :author/name \"Ramez Elmasri\"}\n"
          + "{:db/id #db/id [:db.part/user -12] :book/title \"Operating Systems: A Spiral Approach\" :book/year_published 2009 :book/author #db/id[ :db.part/user -11]}\n"
          + "{:db/id #db/id [:db.part/user -13] :book/title \"Fundamentals of Database Systems\" :book/year_published 2006 :book/author #db/id[ :db.part/user -11]}\n"
          + "\n"
          + "{:db/id #db/id [:db.part/user -14] :author/name \"Steve McConnell\"}\n"
          + "{:db/id #db/id [:db.part/user -15] :book/title \"Code Complete: A Practical Handbook of Software Construction\" :book/year_published 2004 :book/author #db/id[:db.part/user -14]}\n"
          + "{:db/id #db/id [:db.part/user -16] :book/title \"Software Estimation: Demystifying the Black Art\" :book/year_published 2006 :book/author #db/id[ :db.part/user -14]}\n"
          + "{:db/id #db/id [:db.part/user -17] :book/title \"Rapid Development: Taming Wild Software Schedules\" :book/year_published 1996 :book/author #db/id[:db.part/user -14]}\n"
          + "{:db/id #db/id [:db.part/user -18] :book/title \"Software Project Survival Guide\" :book/year_published 1997 :book/author #db/id[ :db.part/user -14]}\n"
          + "{:db/id #db/id [:db.part/user -19] :book/title \"After the Gold Rush: Creating a True Profession of Software Engineering\" :book/year_published 1999 :book/author #db/id[ :db.part/user -14]}\n"
          + "\n"
          + "{:db/id #db/id [:db.part/user -20] :author/name \"Don Miguel Ruiz\"}\n"
          + "{:db/id #db/id [:db.part/user -21] :book/title \"The Four Agreements: A Practical Guide to Personal Freedom\" :book/year_published 2011 :book/author #db/id[ :db.part/user -20]}\n"
          + "\n"
          + "{:db/id #db/id [:db.part/user -22] :author/name \"Charles Petzold\"}\n"
          + "{:db/id #db/id [:db.part/user -23] :book/title \"Code: The Hidden Language of Computer Hardware and Software\" :book/year_published 2000 :book/author #db/id[ :db.part/user -22]}\n"
          + "\n"
          + "{:db/id #db/id [:db.part/user -24] :author/name \"Anil Maheshwari\"}\n"
          + "{:db/id #db/id [:db.part/user -25] :book/title \"Data Analytics Made Accessible\" :book/year_published 2014 :book/author #db/id[ :db.part/user -24]}\n"
          + "\n"
          + "{:db/id #db/id [:db.part/user -26] :author/name \"Jeremy Anderson\"}\n"
          + "{:db/id #db/id [:db.part/user -27] :book/title \"Professional Clojure\" :book/year_published 2016 :book/author #db/id[:db.part/user -26]}]";
}
