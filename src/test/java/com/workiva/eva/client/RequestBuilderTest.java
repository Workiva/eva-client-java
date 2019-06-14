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

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.workiva.eva.client.TestConstants.CATEGORY;
import static com.workiva.eva.client.TestConstants.LABEL;
import static com.workiva.eva.client.TestConstants.TENANT;
import static com.workiva.eva.client.TestConstants.header1;
import static com.workiva.eva.client.TestConstants.header2;

public class RequestBuilderTest {

  private Database db;

  @Before
  public void setup() {
    EvaClient.start(new TestEvaClient());
    db = new Database(TENANT, CATEGORY, LABEL);
  }

  @Test
  public void additionalHeadersAndContextTest() {
    EvaContext ctx = new EvaContext("cid");

    List<Header> listHeaders = new ArrayList();
    Header listHeader = new BasicHeader("listHeaderKey", "listHeaderVal");
    listHeaders.add(listHeader);

    Header singleHeader = new BasicHeader("singleHeaderKey", "singleHeaderVal");

    TestRequestBuilder testBuilder =
        new TestRequestBuilder(db)
            .withRequestHeaders(header1, header2)
            .withRequestHeaders(listHeaders)
            .withRequestHeader(singleHeader)
            .withCorrelationId("cid");

    Assert.assertEquals(testBuilder.getRequestHeaders().size(), 4);
    Assert.assertTrue(testBuilder.getRequestHeaders().contains(header1));
    Assert.assertTrue(testBuilder.getRequestHeaders().contains(header2));
    Assert.assertTrue(testBuilder.getRequestHeaders().contains(listHeader));
    Assert.assertTrue(testBuilder.getRequestHeaders().contains(singleHeader));
  }

  @Test
  public void buildContextWithCidTest() {
    TestRequestBuilder builder = new TestRequestBuilder(db);
    builder.withCorrelationId("cid");
    EvaContext ctx = builder.buildContext();
    Assert.assertEquals(ctx.getCorrelationId(), "cid");
  }

  @Test
  public void buildContextWithoutCidTest() {
    TestRequestBuilder builder = new TestRequestBuilder(db);
    EvaContext ctx = builder.buildContext();
    Assert.assertNotNull(ctx.getCorrelationId());
  }

  @Test
  public void buildContextWithSpanContextTest() {
    // WSpanContext spanContext = mock(WSpanContext.class);
    TestRequestBuilder builder = new TestRequestBuilder(db);
    // builder.withSpanContext(spanContext);
    EvaContext ctx = builder.buildContext();
    // Assert.assertEquals(ctx.getSpanContext(), spanContext);
  }

  @Test
  public void buildContextWithCidAndSpanContextTest() {
    // WSpanContext spanContext = mock(WSpanContext.class);
    TestRequestBuilder builder = new TestRequestBuilder(db);
    builder.withCorrelationId("cid");
    EvaContext ctx = builder.buildContext();
    Assert.assertEquals(ctx.getCorrelationId(), "cid");
    // Assert.assertEquals(ctx.getSpanContext(), spanContext);
  }

  class TestRequestBuilder extends RequestBuilder<TestRequestBuilder, Long> {
    TestRequestBuilder(Database db) {
      super(db);
    }

    public Long execute() {
      return 1L;
    }
  }
}
