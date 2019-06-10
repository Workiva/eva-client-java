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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.workiva.eva.client.exceptions.EvaClientException;
import com.workiva.eva.client.exceptions.ReferenceException;

import java.io.IOException;
import java.util.List;

import static com.workiva.eva.client.TestConstants.CATEGORY;
import static com.workiva.eva.client.TestConstants.CONNECTION_REF;
import static com.workiva.eva.client.TestConstants.LABEL;
import static com.workiva.eva.client.TestConstants.SNAPSHOT_REF;
import static com.workiva.eva.client.TestConstants.TENANT;

public class EdnSerializerTest {

  static EdnSerializer ednSerializer;

  @Before
  public void setup() {
    ednSerializer = new EdnSerializer(TENANT, CATEGORY);
    EvaClient.start(new TestEvaClient());
  }

  @After
  public void teardown() throws IOException {
    EvaClient.stop();
  }

  @Test
  public void createDbRefWithMapTest() {
    String dbString =
        String.format(
            SNAPSHOT_REF,
            String.format(
                "{ :label \"%s\" :as-of %s :as-of-t %s :basis-t %s :snapshot-t %s }",
                LABEL, 1L, 2L, 3L, 4L));
    Database db = ednSerializer.deserialize(dbString);

    Assert.assertEquals(db.asOf, 1L);
    Assert.assertEquals(db.tenant, TENANT);
    Assert.assertEquals(db.category, CATEGORY);
    Assert.assertEquals(db.label, LABEL);
    Assert.assertEquals(db.asOfT, Long.valueOf(2));
    Assert.assertEquals(db.basisT, Long.valueOf(3));
    Assert.assertEquals(db.snapshotT, Long.valueOf(4));
  }

  @Test
  public void createDbRefWithListTest() {
    String dbString = String.format(SNAPSHOT_REF, String.format("[ \"%s\" %s ]", LABEL, 1L));

    try {
      Database db = ednSerializer.deserialize(dbString);
      Assert.fail();
    } catch (ReferenceException ex) {
      Assert.assertEquals(ex.getMessage(), "Unsupported Database Reference type");
    }
  }

  @Test(expected = ReferenceException.class)
  public void createDbWithWrongTypeTest() {
    String dbString = String.format(SNAPSHOT_REF, "\"This is a string, and not a list or map\"");
    ednSerializer.deserialize(dbString);
  }

  @Test
  public void createConnRefWithMapTest() {
    String connString = String.format(CONNECTION_REF, String.format("{ :label \"%s\" }", LABEL));

    Connection conn = ednSerializer.deserialize(connString);
    Assert.assertEquals(conn.tenant, TENANT);
    Assert.assertEquals(conn.category, CATEGORY);
    Assert.assertEquals(conn.label, LABEL);
  }

  @Test
  public void createConnRefWithListTest() {
    String connString = String.format(CONNECTION_REF, String.format("[ \"%s\" ]", LABEL));

    try {
      Connection conn = ednSerializer.deserialize(connString);
      Assert.fail();
    } catch (ReferenceException ex) {
      Assert.assertEquals(ex.getMessage(), "Unsupported Connection Reference type");
    }
  }

  @Test(expected = ReferenceException.class)
  public void createConnRefWithWrongTypeTest() {
    String connString =
        String.format(CONNECTION_REF, "\" This is a string and not a list or map. \"");
    ednSerializer.deserialize(connString);
  }

  @Test
  public void deserializeTest() {
    List list = ednSerializer.deserialize("[1 2 3]");
    Assert.assertEquals(list.get(0), 1L);
    Assert.assertEquals(list.get(1), 2L);
    Assert.assertEquals(list.get(2), 3L);
  }

  @Test
  public void createMapDbRefWithNullAsOf() {
    String dbString =
        String.format(SNAPSHOT_REF, String.format("{ :label \"%s\" :as-of %s }", LABEL, "nil"));
    Database db = ednSerializer.deserialize(dbString);
    Assert.assertEquals(db.getTenant(), TENANT);
    Assert.assertEquals(db.getCategory(), CATEGORY);
    Assert.assertEquals(db.getLabel(), LABEL);

    try {
      db.basisT();
      Assert.fail();
    } catch (EvaClientException e) {
      Assert.assertEquals(e.getMessage(), "basis-t Value was never set on Database object.");
    }
  }

  @Test
  public void createVectorDbRefWithNullAsOf() {
    String dbString = String.format(SNAPSHOT_REF, String.format("[ \"%s\" %s ]", LABEL, "nil"));

    try {
      Database db = ednSerializer.deserialize(dbString);
      Assert.fail();
    } catch (ReferenceException ex) {
      Assert.assertEquals(ex.getMessage(), "Unsupported Database Reference type");
    }
  }

  @Test
  public void deserializeObjectEdn() {
    String objEdn = "#object[some-java-object]";
    String objString = ednSerializer.deserialize(objEdn);
    Assert.assertEquals(objString, "[some-java-object]");
  }
}
