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

import clojure.lang.Keyword;
import eva.Util;

import com.workiva.eva.client.Client;
import com.workiva.eva.client.Connection;
import com.workiva.eva.client.Database;
import com.workiva.eva.client.EvaClient;
import com.workiva.eva.client.builders.InvokeBuilder;
import com.workiva.eva.client.builders.LatestTBuilder;
import com.workiva.eva.client.builders.PullBuilder;
import com.workiva.eva.client.builders.QueryBuilder;
import com.workiva.eva.client.builders.TransactBuilder;
import com.workiva.eva.client.exceptions.EvaClientException;
import com.workiva.eva.client.inline.ConnectionFunctions;
import com.workiva.eva.client.inline.DatabaseFunctions;
import com.workiva.eva.client.inline.InlineFunction;
import com.workiva.eva.client.inline.PeerFunctions;
import com.workiva.eva.client.inline.UtilFunctions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Eva101 {
  // TODO - at some point we should put these into actual @Test's so we can report on them, have
  // code-coverage, etc.
  public static void main(String[] args) throws EvaClientException, IOException {

    try (EvaClient client =
        EvaClient.start(System.getenv().getOrDefault("ECS_URL", "http://localhost:8080"))) {
      Map<Keyword, String> connectionConfig =
          new HashMap<Keyword, String>() {
            {
              put(Keyword.intern("tenant"), "eva-test");
              put(Keyword.intern("category"), "eva-test");
              put(Keyword.intern("label"), "eva-test-49");
            }
          };

      // WTracer tracer = new WTracer();

      // Transact Schema
      Connection conn = Client.connect(connectionConfig);

      Object result;

      // try (Span span = tracer.buildSpan("tx-schema").start()) {
      result =
          new TransactBuilder(conn, (List) eva.Util.read(Transactions.defaultSchema))
              .withCorrelationId("tx-schema")
              // .withSpanContext(span.context())
              .execute();
      System.out.println("Transact Schema: " + result + "\n\n");

      // Snapshot Reference Metadata
      Database afterDb = (Database) ((Map) result).get(Keyword.intern("db-after"));
      System.out.println("as-of-t: " + afterDb.asOfT());
      System.out.println("basis-t: " + afterDb.basisT());
      System.out.println("snapshot-t: " + afterDb.snapshotT());

      // Add First Book
      // try (Span span = tracer.buildSpan("tx-book").start()) {
      result =
          new TransactBuilder(conn, (List) eva.Util.read(Transactions.addBook))
              .withCorrelationId("tx-book")
              // .withSpanContext(span.context())
              .execute();
      System.out.println("Add First Book: " + result + "\n\n");

      // Query First Book
      Database db = conn.db();
      // try (Span span = tracer.buildSpan("query-first-book").start()) {
      result =
          new QueryBuilder(db, (List) eva.Util.read(Queries.queryFirstBook))
              .withCorrelationId("query-first-book")
              // .withSpanContext(span.context())
              .withArgs("First Book")
              .execute();
      System.out.println("Query First Book: " + result + "\n\n");

      // Add Many Books
      // try (Span span = tracer.buildSpan("tx-many-books").start()) {
      result =
          new TransactBuilder(conn, (List) eva.Util.read(Transactions.addManyBooks))
              .withCorrelationId("tx-many-books")
              // .withSpanContext(span.context())
              .execute();
      System.out.println("Add Many Books: " + result + "\n\n");

      // Query Books From 2017
      db = conn.db();
      // try (Span span = tracer.buildSpan("query-books-2017").start()) {}
      result =
          new QueryBuilder(db, (List) eva.Util.read(Queries.queryBooksFrom2017))
              .withCorrelationId("query-books-2017")
              // .withSpanContext(span.context())
              .withArgs(2017)
              .execute();
      System.out.println("Query Books From 2017: " + result + "\n\n");

      // Query Author of "Designing Data-Intensive Applications"
      // try (Span span = tracer.buildSpan("query-author-data-intensive-book").start()) {}
      result =
          new QueryBuilder(db, (List) eva.Util.read(Queries.queryAuthorDataIntensiveApplications))
              .withCorrelationId("query-author-data-intensive-book")
              // .withSpanContext(span.context())
              .execute();
      System.out.println(
          "Query Author of Designing Data-Intensive Applications: " + result + "\n\n");

      // Query Author ID of "Designing Data-Intensive Applications"
      // try (Span span = tracer.buildSpan("query-author-id-data-intensive-book").start()) {}
      result =
          new QueryBuilder(db, (List) eva.Util.read(Queries.queryAuthorIdDataIntensiveApplications))
              .withCorrelationId("query-author-id-data-intensive-book")
              // .withSpanContext(span.context())
              .execute();
      System.out.println(
          "Query Author Id of Desgining Data-Intensive Applications: " + result + "\n\n");

      // Query Books from Author Steve McConnell
      // try (Span span = tracer.buildSpan("query-books-steve-mcconnell").start()) {}
      result =
          new QueryBuilder(db, (List) eva.Util.read(Queries.queryBooksFromAuthor))
              .withCorrelationId("query-books-steve-mcconnell")
              // .withSpanContext(span.context())
              .execute();
      System.out.println("Query Books from Author Steve McConnell: " + result + "\n\n");

      // Pull Book by First Id
      // try (Span span = tracer.buildSpan("tx-schema").start()) {}

      result = new PullBuilder(db, "[*]").withEntityId(8796093023236L).execute();
      System.out.println("Pull Book By First Id: " + result + "\n\n");

      // Query All Book Names
      // try (Span span = tracer.buildSpan("query-all-book-names").start()) {}
      result =
          new QueryBuilder(db, (List) eva.Util.read(Queries.queryAllBookNames))
              .withCorrelationId("query-all-book-names")
              // .withSpanContext(span.context())
              .execute();
      System.out.println("Query All Book Names: " + result + "\n\n");

      // Find when a book was commited to the database
      // try (Span span = tracer.buildSpan("query-find-book-committed").start()) {}
      result =
          new QueryBuilder(db, (List) eva.Util.read(Queries.findWhenBookCommitted))
              .withCorrelationId("query-find-book-committed")
              // .withSpanContext(span.context())
              .execute();
      System.out.println("Find when a book was commited to the database: " + result + "\n\n");

      // Find book titles and their year released prior to 2005
      // try (Span span = tracer.buildSpan("query-books-before-2005").start()) {}
      result =
          new QueryBuilder(db, (List) eva.Util.read(Queries.findBooksBefore2005))
              .withCorrelationId("query-books-before-2005")
              // .withSpanContext(span.context())
              .execute();
      System.out.println(
          "Find book titles and their year released prior to 2005: " + result + "\n\n");

      // Find books older than “Software Project Survival Guide”
      // try (Span span = tracer.buildSpan("query-books-older-than-survival-guide").start()) {}
      result =
          new QueryBuilder(db, (List) eva.Util.read(Queries.findBooksOlderThanSurvivalGuide))
              .withCorrelationId("query-books-older-than-survival-guide")
              // .withSpanContext(span.context())
              .execute();
      System.out.println(
          "Find books older than “Software Project Survival Guide: " + result + "\n\n");

      // Query for the oldest book
      // try (Span span = tracer.buildSpan("query-oldest-book").start()) {}
      result =
          new QueryBuilder(db, (List) eva.Util.read(Queries.queryForOldestBook))
              .withCorrelationId("query-oldest-book")
              // .withSpanContext(span.context())
              .execute();
      System.out.println("Query for the oldest book: " + result + "\n\n");

      // Get Books with Rules
      // try (Span span = tracer.buildSpan("query-books-with-rules").start()) {}
      result =
          new QueryBuilder(db, (List) eva.Util.read(Queries.findBooksWithRules))
              .withArgs(Util.read(Queries.queryRule))
              .withCorrelationId("query-books-with-rules")
              // .withSpanContext(span.context())
              .execute();
      System.out.println("Get Books with Rules: " + result + "\n\n");

      // Invoke CAS
      // try (Span span = tracer.buildSpan("tx-schema").start()) {}

      result =
          new InvokeBuilder(db, Keyword.intern("db.fn", "cas"))
              .withCorrelationId("invoke-cas")
              // .withSpanContext(tracer.buildSpan("invoke-cas").start().context())
              .withArgs(
                  db, 0, Keyword.intern("db", "doc"), "The default database partition.", "Testing")
              .execute();
      System.out.println("Invoke CAS: " + result + "\n\n");

      // Inline functions!
      // try (Span span = tracer.buildSpan("query-ffirst-query-inline-func").start()) {}
      InlineFunction inlineAsOf =
          UtilFunctions.ffirst(
              PeerFunctions.query(
                  "[:find ?tx :in $ ?t :where [?b :book/title ?t ?tx]]", db, "First Book"));
      Database inlineAsOfDb = conn.dbAt(inlineAsOf);

      result =
          new QueryBuilder(
                  inlineAsOfDb,
                  (List) eva.Util.read("[:find ?b :in $ ?t :where [?b :book/title ?t]]"))
              .withCorrelationId("query-ffirst-query-inline-func")
              // .withSpanContext(span.context())
              .withArgs("First Book")
              .execute();
      System.out.println("Ffirst and Query Inline Function: " + result);

      // try (Span span = tracer.buildSpan("query-ident-inline-func").start()) {
      InlineFunction identFunc = DatabaseFunctions.ident(db, 1);
      result =
          new QueryBuilder(
                  db,
                  (List)
                      eva.Util.read(
                          "[:find ?ident :in $ ?my-ident :where [?my-ident :db/ident ?ident]]"))
              .withCorrelationId("query-ident-inline-func")
              // .withSpanContext(span.context())
              .withArgs(identFunc)
              .execute();
      System.out.println("Ident func Inline function: " + result);

      // try (Span span = tracer.buildSpan("query-entid-inline-func").start()) {}
      InlineFunction entidFunc = DatabaseFunctions.entid(db, Keyword.intern("db.part", "tx"));
      result =
          new QueryBuilder(
                  db, (List) eva.Util.read("[:find ?ident :in $ ?e :where [?e :db/ident ?ident]]"))
              .withCorrelationId("query-entid-inline-func")
              // .withSpanContext(span.context())
              .withArgs(entidFunc)
              .execute();
      System.out.println("Entid func Inline function: " + result);

      // try (Span span = tracer.buildSpan("query-latestt-inline-func").start()) {}
      InlineFunction latestTFunc = ConnectionFunctions.latestT(conn);
      Database latestTDb = conn.dbAt(latestTFunc);
      result =
          new QueryBuilder(
                  latestTDb,
                  (List) eva.Util.read("[:find ?e :in $ :where [?e :db/ident :db.part/tx]]"))
              .withCorrelationId("query-latestt-inline-func")
              // .withSpanContext(span.context())
              .execute();
      System.out.println("LatestT Inline function: " + result);

      result = Client.toTxEid(1L);
      System.out.println("toTxEid Result: " + result);

      result = db.entid(Keyword.intern("db.fn", "cas"));
      System.out.println("entid results: " + result);

      result = db.ident(1);
      System.out.println("ident results: " + result);

      // try (Span span = tracer.buildSpan("latestT").start()) {}
      result =
          new LatestTBuilder(conn)
              .withCorrelationId("latestT")
              // .withSpanContext(span.context())
              .execute();
      System.out.println("latestT results: " + result);

      // Ident and Entid endpoints
      result = db.entid(Keyword.intern("db.part", "tx"));
      System.out.println("entid results: " + result);

      result = db.ident(1);
      System.out.println("ident results: " + result);
    }
  }
}
