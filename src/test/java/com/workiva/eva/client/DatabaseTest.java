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
import clojure.lang.PersistentVector;
import com.workiva.eva.client.exceptions.EvaClientException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.workiva.eva.client.inline.ConnectionFunctions;
import com.workiva.eva.client.inline.InlineFunction;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.workiva.eva.client.TestConstants.CATEGORY;
import static com.workiva.eva.client.TestConstants.LABEL;
import static com.workiva.eva.client.TestConstants.TENANT;

public class DatabaseTest {

  static Map connConfig = Util.createConnectionConfig(TENANT, CATEGORY, LABEL);

  @Before
  public void setup() {
    EvaClient.start(new TestEvaClient());
  }

  @After
  public void teardown() throws IOException {
    EvaClient.stop();
  }

  @Test
  public void createDatabaseWithLongAsOfTest() {
    Connection conn = Client.connect(connConfig);
    Database db = conn.db().asOf(1L);

    Assert.assertEquals(db.asOf, 1L);
    Assert.assertEquals(db.tenant, TENANT);
    Assert.assertEquals(db.category, CATEGORY);
    Assert.assertEquals(db.label, LABEL);
  }

  @Test
  public void createDatabaseWithInlineAsOfTest() {
    Connection conn = Client.connect(connConfig);
    InlineFunction latestT = ConnectionFunctions.latestT(conn);
    Database db = conn.db().asOf(latestT);

    Assert.assertEquals(db.asOf, latestT);
    Assert.assertEquals(db.tenant, TENANT);
    Assert.assertEquals(db.category, CATEGORY);
    Assert.assertEquals(db.label, LABEL);
  }

  @Test
  public void pullTest() {
    Connection conn = Client.connect(connConfig);
    Database db = conn.db();

    Map map = db.pull("[*]", 123L);
    Assert.assertEquals(map.get("db"), db);
    Assert.assertEquals(map.get("pattern"), "[*]");
    Assert.assertEquals(map.get("id"), 123L);
    Assert.assertEquals(((Object[]) map.get("args")).length, 0);
  }

  @Test
  public void pullManyTest() {
    Connection conn = Client.connect(connConfig);
    Database db = conn.db();

    List list = db.pullMany("[*]", PersistentVector.create(123L, 456L));
    Assert.assertEquals(list.get(0), db);
    Assert.assertEquals(list.get(1), "[*]");
    Assert.assertEquals(list.get(2), PersistentVector.create(123L, 456L));
    Assert.assertEquals(((Object[]) list.get(3)).length, 0);
  }

  @Test
  public void invokeTest() {
    Connection conn = Client.connect(connConfig);
    Database db = conn.db();

    Map result =
        (Map)
            db.invoke(
                Keyword.intern("db.fn", "cas"),
                db,
                0,
                Keyword.intern("db", "doc"),
                "The default database partition.",
                "Testing");
    Assert.assertEquals(result.get("db"), db);
    Assert.assertEquals(result.get("function"), Keyword.intern("db.fn", "cas"));

    Object[] invokeArgs = (Object[]) result.get("args");
    Assert.assertEquals(invokeArgs[0], db);
    Assert.assertEquals(invokeArgs[1], 0);
    Assert.assertEquals(invokeArgs[2], Keyword.intern("db", "doc"));
    Assert.assertEquals(invokeArgs[3], "The default database partition.");
    Assert.assertEquals(invokeArgs[4], "Testing");
  }

  @Test
  public void entidTest() {
    Connection conn = Client.connect(connConfig);
    Database db = conn.db();

    Keyword ident = Keyword.intern("db.fn", "cas");
    Map result = (Map) db.entid(ident);
    Assert.assertEquals(result.get("db"), db);
    Assert.assertEquals(result.get("ident"), ident);
  }

  @Test
  public void identTest() {
    Connection conn = Client.connect(connConfig);
    Database db = conn.db();

    Long entid = 1L;
    Map result = (Map) db.ident(entid);
    Assert.assertEquals(result.get("db"), db);
    Assert.assertEquals(result.get("entid"), entid);
  }

  @Test
  public void dbStaysWithSyncDbAfterAsOf() {
    Connection conn = Client.connect(connConfig);
    Database db = conn.syncDb();
    Database asOfDb = db.asOf(1L);

    Assert.assertTrue(asOfDb.syncDb);
  }

  @Test
  public void dbAsOfT() {
    Database db = new Database(TENANT, CATEGORY, LABEL, 1, false, 2L, 3L, 4L);
    Long basisT = db.asOfT();
    Assert.assertEquals(basisT, Long.valueOf(2));
  }

  @Test
  public void dbBasisT() {
    Database db = new Database(TENANT, CATEGORY, LABEL, 1, false, 2L, 3L, 4L);
    Long basisT = db.basisT();
    Assert.assertEquals(basisT, Long.valueOf(3));
  }

  @Test(expected = EvaClientException.class)
  public void dbBasisTException() {
    Database db = new Database(TENANT, CATEGORY, LABEL, 1, false, 2L, null, 4L);
    db.basisT();
  }

  @Test
  public void dbSnapshotT() {
    Database db = new Database(TENANT, CATEGORY, LABEL, 1, false, 2L, 3L, 4L);
    Long basisT = db.snapshotT();
    Assert.assertEquals(basisT, Long.valueOf(4));
  }

  @Test(expected = EvaClientException.class)
  public void dbSnapshotTException() {
    Database db = new Database(TENANT, CATEGORY, LABEL, 1, false, 2L, 3L, null);
    db.snapshotT();
  }

  @Test
  public void serializationWithoutAsOfTest() {
    Connection conn = Client.connect(connConfig);
    Database db = conn.db();
    EdnSerializer serializer = new EdnSerializer(TENANT, CATEGORY);
    String serializedDb = serializer.serialize(db);
    Assert.assertEquals(
        serializedDb, String.format("#eva.client.service/snapshot-ref { :label \"%s\" }", LABEL));
  }

  @Test
  public void serializationWithAsOfTest() {
    Connection conn = Client.connect(connConfig);
    Database db = conn.dbAt(1L);
    EdnSerializer serializer = new EdnSerializer(TENANT, CATEGORY);
    String serializedDb = serializer.serialize(db);
    Assert.assertEquals(
        serializedDb,
        String.format("#eva.client.service/snapshot-ref { :label \"%s\" :as-of %s }", LABEL, 1L));
  }

  @Test
  public void serializationWithSyncDbTest() {
    Connection conn = Client.connect(connConfig);
    Database db = conn.syncDb();
    EdnSerializer serializer = new EdnSerializer(TENANT, CATEGORY);
    String dbString = serializer.serialize(db);
    Assert.assertEquals(
        dbString,
        String.format(
            "#eva.client.service/snapshot-ref { :label \"%s\" :sync-db %s }", LABEL, true));
  }
}
