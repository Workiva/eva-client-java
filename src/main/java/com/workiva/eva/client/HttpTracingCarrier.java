package com.workiva.eva.client;

import org.apache.http.client.methods.HttpRequestBase;

import java.util.Iterator;
import java.util.Map;

/**
 * Used to inject Tracing spans into request headers.
 * This only supports injecting tracing headers into the request header, and not extracting.
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
