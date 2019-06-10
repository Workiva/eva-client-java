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

package com.workiva.eva.client;

import clojure.lang.Keyword;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.workiva.eva.client.inline.ConnectionFunctions;
import com.workiva.eva.client.inline.InlineFunction;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import static com.workiva.eva.client.TestConstants.CATEGORY;
import static com.workiva.eva.client.TestConstants.LABEL;
import static com.workiva.eva.client.TestConstants.TENANT;

public class ConnectionTest {

  static Map connConfig = Util.createConnectionConfig(TENANT, CATEGORY, LABEL);
  static String txData = "[[:db/add #db/id [:db.part/user] :book/title \"First Book\"]]";

  @Before
  public void setup() {
    EvaClient.start(new TestEvaClient());
  }

  @After
  public void teardown() throws IOException {
    EvaClient.stop();
  }

  @Test
  public void createDbTest() {
    Connection conn = Client.connect(connConfig);
    Database db = conn.db();

    Assert.assertEquals(db.tenant, TENANT);
    Assert.assertEquals(db.category, CATEGORY);
    Assert.assertEquals(db.label, LABEL);
    Assert.assertEquals(db.asOf, null);
    Assert.assertFalse(db.syncDb);
  }

  @Test
  public void createSyncDbTest() {
    Connection conn = Client.connect(connConfig);
    Database db = conn.syncDb();

    Assert.assertEquals(db.tenant, TENANT);
    Assert.assertEquals(db.category, CATEGORY);
    Assert.assertEquals(db.label, LABEL);
    Assert.assertTrue(db.syncDb);
  }

  @Test
  public void createDbAtLongTest() {
    Connection conn = Client.connect(connConfig);
    Database db = conn.dbAt(1L);

    Assert.assertEquals(db.tenant, TENANT);
    Assert.assertEquals(db.category, CATEGORY);
    Assert.assertEquals(db.label, LABEL);
    Assert.assertEquals(db.asOf, 1L);
  }

  @Test
  public void createDbAtInlineFuncTest() {
    Connection conn = Client.connect(connConfig);

    InlineFunction latestT = ConnectionFunctions.latestT(conn);
    Database db = conn.dbAt(latestT);

    Assert.assertEquals(db.tenant, TENANT);
    Assert.assertEquals(db.category, CATEGORY);
    Assert.assertEquals(db.label, LABEL);
    Assert.assertEquals(db.asOf, latestT);
  }

  @Test
  public void transactAsyncWithListTest() throws Exception {
    Connection conn = Client.connect(connConfig);

    List txList = (List) eva.Util.read(txData);
    Future<Map<Keyword, Object>> futureResults = conn.transactAsync(txList);
    Map results = futureResults.get();
    Assert.assertEquals(results.get("tx"), txList);
    Assert.assertEquals(results.get("connection"), conn);
  }

  @Test
  public void transactAsyncWithStringTest() throws Exception {
    Connection conn = Client.connect(connConfig);

    Future<Map<Keyword, Object>> futureResults = conn.transactAsync(txData);
    Map results = futureResults.get();
    Assert.assertEquals(results.get("connection"), conn);

    List<List> txList = (List<List>) eva.Util.read(txData);
    List firstTxList = txList.get(0);
    Assert.assertEquals(firstTxList.get(0), Keyword.intern("db", "add"));
    Assert.assertEquals(firstTxList.get(2), Keyword.intern("book", "title"));
    Assert.assertEquals(firstTxList.get(3), "First Book");
  }

  @Test
  public void transactWithListTest() {
    Connection conn = Client.connect(connConfig);

    List txList = (List) eva.Util.read(txData);
    Map results = conn.transact(txList);
    Assert.assertEquals(results.get("tx"), txList);
    Assert.assertEquals(results.get("connection"), conn);
  }

  @Test
  public void transactWithStringTest() {
    Connection conn = Client.connect(connConfig);

    Map results = conn.transact(txData);
    Assert.assertEquals(results.get("connection"), conn);

    List<List> txList = (List<List>) eva.Util.read(txData);
    List firstTxList = txList.get(0);
    Assert.assertEquals(firstTxList.get(0), Keyword.intern("db", "add"));
    Assert.assertEquals(firstTxList.get(2), Keyword.intern("book", "title"));
    Assert.assertEquals(firstTxList.get(3), "First Book");
  }

  @Test
  public void latestTTest() {
    Connection conn = Client.connect(connConfig);
    Assert.assertEquals(1L, (long) conn.latestT());
  }

  @Test
  public void serializationTest() {
    Connection conn = Client.connect(connConfig);
    EdnSerializer serializer = new EdnSerializer(TENANT, CATEGORY);
    String serializedConn = serializer.serialize(conn);
    Assert.assertEquals(
        serializedConn,
        String.format("#eva.client.service/connection-ref { :label \"%s\" }", LABEL));
  }
}
