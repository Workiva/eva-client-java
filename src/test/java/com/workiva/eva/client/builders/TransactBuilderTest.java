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

import clojure.lang.Keyword;
import com.workiva.eva.client.Connection;
import com.workiva.eva.client.EvaClient;
import com.workiva.eva.client.EvaContext;
import com.workiva.eva.client.TestEvaClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import static com.workiva.eva.client.TestConstants.CATEGORY;
import static com.workiva.eva.client.TestConstants.HEADERS;
import static com.workiva.eva.client.TestConstants.LABEL;
import static com.workiva.eva.client.TestConstants.TENANT;

public class TransactBuilderTest {

  private Connection conn;

  @Before
  public void setup() {
    EvaClient.start(new TestEvaClient());
    conn = new Connection(TENANT, CATEGORY, LABEL);
  }

  @Test
  public void executeTest() {
    List tx = new ArrayList();
    TransactBuilder transact =
        new TransactBuilder(conn, tx).withRequestHeaders(HEADERS).withCorrelationId("cid");
    Map result = transact.execute();
    Assert.assertEquals(result.get("tx"), tx);
    Assert.assertEquals(result.get("connection"), conn);
    EvaContext context = (EvaContext) result.get("context");
    Assert.assertEquals(context.getCorrelationId(), "cid");
    Assert.assertEquals(result.get("headers"), HEADERS);
  }

  @Test
  public void executeAsync() throws Exception {
    List tx = new ArrayList();
    TransactBuilder transact =
        new TransactBuilder(conn, tx).withRequestHeaders(HEADERS).withCorrelationId("cid");
    Future<Map<Keyword, Object>> futureResult = transact.executeAsync();
    Map result = futureResult.get();
    Assert.assertEquals(result.get("tx"), tx);
    Assert.assertEquals(result.get("connection"), conn);
    EvaContext context = (EvaContext) result.get("context");
    Assert.assertEquals(context.getCorrelationId(), "cid");
    Assert.assertEquals(result.get("headers"), HEADERS);
  }
}
