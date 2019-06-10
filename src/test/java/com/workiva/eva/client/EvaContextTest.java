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
import org.apache.http.client.methods.HttpPost;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class EvaContextTest {

  // WTracer tracer = new WTracer(new DebuggingRecorder());

  @Before
  public void setup() {
    // GlobalTracer.setGlobalTracer(tracer);
  }

  @After
  public void teardown() throws IOException {
    // GlobalTracer.setGlobalTracer(null);
  }

  @Test
  public void getTracingHeadersTest() {
    // Span span = GlobalTracer.getGlobalTracer().buildSpan("words").start();
    // WSpanContext spanContext = (WSpanContext) span.context();
    EvaContext context = new EvaContext("cid");
    HttpPost post = new HttpPost("some-endpoint");
    post.setHeader("Some-Previous-Header", "Please Don't Remove!");
    // context.addTracingHeaders(post);
    boolean previousHeaderNotRemoved = true;
    for (Header header : post.getAllHeaders()) {
      // if (header.getName().equalsIgnoreCase(TracingConstants.FIELD_NAME_TRACE_ID)) {
      //   Assert.assertEquals(spanContext.getTraceId(), header.getValue());
      // } else if (header.getName().equalsIgnoreCase(TracingConstants.FIELD_NAME_PARENT_SPAN_ID)) {
      //   Assert.assertEquals(Long.toString(spanContext.getParentSpanId()), header.getValue());
      // } else if (header.getName().equalsIgnoreCase(TracingConstants.FIELD_NAME_SPAN_ID)) {
      //   Assert.assertEquals(Long.toString(spanContext.getSpanId()), header.getValue());
      // } else if (header.getName().equalsIgnoreCase(TracingConstants.FIELD_NAME_SAMPLED)) {
      //   Assert.assertEquals(Boolean.toString(spanContext.getSampled()), header.getValue());
      // }
      if (header.getName().equalsIgnoreCase("Some-Previous-Header")) {
        Assert.assertEquals("Please Don't Remove!", header.getValue());
        previousHeaderNotRemoved = false;
      }
    }
    Assert.assertFalse(previousHeaderNotRemoved);
  }

  @Test
  public void getTracingHeadersNullTest() {
    EvaContext context = new EvaContext("cid");
    HttpPost post = new HttpPost("some-endpoint");
    // context.addTracingHeaders(post);
    Assert.assertTrue(post.getAllHeaders().length == 0);
  }
}
