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

import org.apache.http.client.methods.HttpRequestBase;

import java.util.Iterator;
import java.util.Map;

/**
 * Used to inject Tracing spans into request headers. This only supports injecting tracing headers
 * into the request header, and not extracting.
 */
class HttpTracingCarrier implements io.opentracing.propagation.TextMap {
  HttpRequestBase requestBase;

  HttpTracingCarrier(HttpRequestBase requestBase) {
    this.requestBase = requestBase;
  }

  public void put(String key, String val) {
    requestBase.setHeader(key, val);
  }

  @Override
  public Iterator<Map.Entry<String, String>> iterator() {
    throw new UnsupportedOperationException("Carrier can only be used for writes.");
  }
}
