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

import java.util.ArrayList;
import java.util.List;

import static com.workiva.eva.client.TestConstants.CATEGORY;
import static com.workiva.eva.client.TestConstants.HEADERS;
import static com.workiva.eva.client.TestConstants.LABEL;
import static com.workiva.eva.client.TestConstants.TENANT;

public class QueryBuilderTest {

  private Database db;

  @Before
  public void setup() {
    EvaClient.start(new TestEvaClient());
    db = new Database(TENANT, CATEGORY, LABEL);
  }

  @Test
  public void executeTest() {
    List query = new ArrayList();
    query.add("some-query");
    QueryBuilder builder =
        new QueryBuilder(db, query)
            .withArgs(1, 2, 3)
            .withRequestHeaders(HEADERS)
            .withCorrelationId("cid");
    List results = (List) builder.execute();
    Assert.assertEquals(results.get(0), query);
    Assert.assertArrayEquals((Object[]) results.get(1), new Object[] {db, 1, 2, 3});
    EvaContext context = (EvaContext) results.get(2);
    Assert.assertEquals(context.getCorrelationId(), "cid");
    Assert.assertEquals(results.get(3), HEADERS);
  }
}
