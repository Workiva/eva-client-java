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

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.workiva.eva.client.TestConstants.CATEGORY;
import static com.workiva.eva.client.TestConstants.LABEL;
import static com.workiva.eva.client.TestConstants.TENANT;

public class ClientTest {

  static Map connConfig = Util.createConnectionConfig(TENANT, CATEGORY, LABEL);
  static EvaClientHelper helper = new TestEvaClientHelper();

  @Before
  public void setup() {
    EvaClient.start(new TestEvaClient());
  }

  @After
  public void teardown() throws IOException {
    EvaClient.stop();
  }

  @Test
  public void createConnectionTest() {
    Connection conn = Client.connect(connConfig);
    Assert.assertEquals(conn.tenant, TENANT);
    Assert.assertEquals(conn.category, CATEGORY);
    Assert.assertEquals(conn.label, LABEL);
  }

  @Test
  public void queryTest() {
    Connection conn = Client.connect(connConfig);
    Database db = conn.db();
    db.clientHelper = helper;

    Object query = eva.Util.read("[:find ?ident :in $ ?arg1 ?arg2 :where [?e :db/ident ?ident]]");
    List result = Client.query(query, db, 1, "String");

    Assert.assertEquals(result.get(0), query);
    Object[] queryArgs = (Object[]) result.get(1);

    Assert.assertEquals(queryArgs[0], db);
    Assert.assertEquals(queryArgs[1], 1);
    Assert.assertEquals(queryArgs[2], "String");
  }

  @Test
  public void queryWithStringTest() {
    Connection conn = Client.connect(connConfig);
    Database db = conn.db();
    db.clientHelper = helper;

    String queryString = "[:find ?ident :in $ ?arg1 ?arg2 :where [?e :db/ident ?ident]]";
    List result = Client.query(queryString, db, 1, "String");

    Assert.assertEquals(result.get(0), eva.Util.read(queryString));
    Object[] queryArgs = (Object[]) result.get(1);

    Assert.assertEquals(queryArgs[0], db);
    Assert.assertEquals(queryArgs[1], 1);
    Assert.assertEquals(queryArgs[2], "String");
  }

  @Test
  public void tempIdTest() {
    final Keyword PART = Keyword.intern("db", "part");
    Object tempId = Client.tempid(PART);
    Object tempId2 = Client.tempid(PART, 1);
    Object sameAsTempId2 = Client.tempid(PART, 1);
    Object differentTempId = Client.tempid(PART, 2);

    Assert.assertNotEquals(tempId, tempId2);
    Assert.assertEquals(tempId2, sameAsTempId2);
    Assert.assertNotEquals(differentTempId, tempId2);
  }
}
