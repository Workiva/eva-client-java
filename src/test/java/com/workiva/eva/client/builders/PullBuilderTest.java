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

package com.workiva.eva.client.builders;

import com.workiva.eva.client.Database;
import com.workiva.eva.client.EvaClient;
import com.workiva.eva.client.EvaContext;
import com.workiva.eva.client.TestEvaClient;
import com.workiva.eva.client.inline.DatabaseFunctions;
import com.workiva.eva.client.inline.InlineFunction;
import org.apache.http.message.BasicHeader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.workiva.eva.client.TestConstants.CATEGORY;
import static com.workiva.eva.client.TestConstants.HEADERS;
import static com.workiva.eva.client.TestConstants.LABEL;
import static com.workiva.eva.client.TestConstants.TENANT;

public class PullBuilderTest {

  private Database db;

  @Before
  public void setup() {
    EvaClient.start(new TestEvaClient());
    db = new Database(TENANT, CATEGORY, LABEL);

    BasicHeader header1 = new BasicHeader("key1", "value1");
    BasicHeader header2 = new BasicHeader("key2", "value2");
  }

  @Test
  public void pullSingleLongExecuteTest() {
    PullBuilder builder = new PullBuilder(db, "pattern");

    PullBuilder.PullSingle singlePull =
        builder.withEntityId(1L).withRequestHeaders(HEADERS).withCorrelationId("cid");

    Map result = singlePull.execute();

    Assert.assertEquals(result.get("pattern"), "pattern");
    Assert.assertEquals(result.get("id"), 1L);
    Assert.assertEquals(result.get("db"), db);
    EvaContext context = (EvaContext) result.get("context");
    Assert.assertEquals(context.getCorrelationId(), "cid");
    Assert.assertEquals(result.get("headers"), HEADERS);
  }

  @Test
  public void pullSingleInlineFuncExecuteTest() {
    PullBuilder builder = new PullBuilder(db, "pattern");
    InlineFunction func = DatabaseFunctions.entid(db, 1L);
    PullBuilder.PullSingle singlePull =
        builder.withEntityId(func).withRequestHeaders(HEADERS).withCorrelationId("cid");

    Map result = singlePull.execute();
    Assert.assertEquals(result.get("pattern"), "pattern");
    Assert.assertEquals(result.get("id"), func);
    Assert.assertEquals(result.get("db"), db);
    EvaContext context = (EvaContext) result.get("context");
    Assert.assertEquals(context.getCorrelationId(), "cid");
    Assert.assertEquals(result.get("headers"), HEADERS);
  }

  @Test
  public void pullManyVariadicExecuteTest() {
    PullBuilder builder = new PullBuilder(db, "pattern");
    PullBuilder.PullMany pullMany =
        builder.withEntityIds(1L, 2L, 3L).withRequestHeaders(HEADERS).withCorrelationId("cid");

    List result = pullMany.execute();
    Assert.assertEquals(result.get(0), db);
    Assert.assertEquals(result.get(1), "pattern");
    Assert.assertArrayEquals((Object[]) result.get(2), new Object[] {1L, 2L, 3L});
    Assert.assertArrayEquals((Object[]) result.get(3), new Object[0]);
    EvaContext context = (EvaContext) result.get(4);
    Assert.assertEquals(context.getCorrelationId(), "cid");
    Assert.assertEquals(result.get(5), HEADERS);
  }

  @Test
  public void pullManyListExecuteTest() {
    List list = new ArrayList();
    list.add(1L);
    list.add(2L);
    list.add(3L);
    PullBuilder builder = new PullBuilder(db, "pattern");
    PullBuilder.PullMany pullMany =
        builder.withEntityIds(list).withRequestHeaders(HEADERS).withCorrelationId("cid");

    List result = pullMany.execute();
    Assert.assertEquals(result.get(0), db);
    Assert.assertEquals(result.get(1), "pattern");
    Assert.assertEquals(result.get(2), list);
    Assert.assertArrayEquals((Object[]) result.get(3), new Object[0]);
    EvaContext context = (EvaContext) result.get(4);
    Assert.assertEquals(context.getCorrelationId(), "cid");
    Assert.assertEquals(result.get(5), HEADERS);
  }
}
