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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static com.workiva.eva.client.TestConstants.CATEGORY;
import static com.workiva.eva.client.TestConstants.HEADERS;
import static com.workiva.eva.client.TestConstants.LABEL;
import static com.workiva.eva.client.TestConstants.TENANT;

public class IdentBuilderTest {

  private Database db;

  @Before
  public void setup() {
    EvaClient.start(new TestEvaClient());
    db = new Database(TENANT, CATEGORY, LABEL);
  }

  @Test
  public void executeTest() {
    IdentBuilder invokeBuilder =
        new IdentBuilder(db, 1).withRequestHeaders(HEADERS).withCorrelationId("cid");

    Map results = (Map) invokeBuilder.execute();
    Assert.assertEquals(results.get("db"), db);
    Assert.assertEquals(results.get("entid"), 1);
    Assert.assertEquals(results.get("strict"), false);
    EvaContext context = (EvaContext) results.get("context");
    Assert.assertEquals(context.getCorrelationId(), "cid");
    Assert.assertEquals(results.get("headers"), HEADERS);
  }

  @Test
  public void executeStrictTest() {
    IdentBuilder invokeBuilder =
        new IdentBuilder(db, 1).asStrict().withRequestHeaders(HEADERS).withCorrelationId("cid");

    Map results = (Map) invokeBuilder.execute();
    Assert.assertEquals(results.get("db"), db);
    Assert.assertEquals(results.get("entid"), 1);
    Assert.assertEquals(results.get("strict"), true);
    EvaContext context = (EvaContext) results.get("context");
    Assert.assertEquals(context.getCorrelationId(), "cid");
    Assert.assertEquals(results.get("headers"), HEADERS);
  }
}
