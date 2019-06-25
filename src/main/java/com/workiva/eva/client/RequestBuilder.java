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

import io.opentracing.SpanContext;
import org.apache.http.Header;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Parent builder that allows for customization of requests.
 *
 * @param <T> The builder type.
 * @param <V> The type that the execute function returns.
 */
public abstract class RequestBuilder<T extends RequestBuilder<T, V>, V> {
  private String correlationId;
  private SpanContext spanContext = null;
  private EvaContext ctx;
  private List<Header> headers = new ArrayList();
  protected EvaClientHelper clientHelper;

  protected RequestBuilder(Reference reference) {
    clientHelper = reference.clientHelper;
  }

  public T withRequestHeaders(Header... headers) {
    return this.withRequestHeaders(Arrays.asList(headers));
  }

  public T withRequestHeaders(List<Header> headers) {
    this.headers.addAll(headers);
    return (T) this;
  }

  public T withRequestHeader(Header header) {
    this.headers.add(header);
    return (T) this;
  }

  public T withCorrelationId(String correlationId) {
    this.correlationId = correlationId;

    return (T) this;
  }

  public T withSpanContext(SpanContext spanContext) {
    this.spanContext = spanContext;
    return (T) this;
  }

  protected List<Header> getRequestHeaders() {
    return headers;
  }

  protected EvaContext buildContext() {
    if (correlationId == null) {
      ctx = new EvaContext();
      ctx.setSpanContext(spanContext);
    } else {
      ctx = new EvaContext(correlationId);
    }
    return ctx;
  }

  public abstract V execute();
}
