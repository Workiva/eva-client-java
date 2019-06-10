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

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.workiva.eva.client.exceptions.EvaClientException;

import java.io.IOException;
import java.util.concurrent.Future;

/** Performs the actual work of sending off requests to the client service. */
public class EvaHttpClient extends EvaClient {
  private static final Logger logger = LoggerFactory.getLogger(EvaHttpClient.class);

  private CloseableHttpAsyncClient asyncClient;

  private final String url;

  public EvaHttpClient(String url) throws EvaClientException {
    this.url = url;

    ConnectingIOReactor ioReactor;
    try {
      ioReactor = new DefaultConnectingIOReactor();
    } catch (IOException e) {
      throw new EvaClientException("Unable to create Eva Http Client.", e.getCause());
    }

    PoolingNHttpClientConnectionManager asyncConnectionManager =
        new PoolingNHttpClientConnectionManager(ioReactor);
    asyncClient = HttpAsyncClients.custom().setConnectionManager(asyncConnectionManager).build();
    asyncClient.start();
  }

  /**
   * Stops the underlying client that communicates with the client service.
   *
   * @throws IOException error related to closing the HTTP client
   */
  @Override
  public void close() throws IOException {
    asyncClient.close();
    EvaClient.evaClient = null;
  }

  /**
   * Creates a client helper that can use this Eva Client.
   *
   * @param tenant The tenant.
   * @param category The category.
   * @return An {@link EvaHttpClientHelper}
   */
  EvaHttpClientHelper buildClientHelper(String tenant, String category, String label) {
    return new EvaHttpClientHelper(url, tenant, category, label);
  }

  /**
   * Executes the request given by the {@link EvaHttpClientHelper}.
   *
   * @param requestObj The request to be executed.
   * @return the response from the client service wrapped in a future.
   */
  Future<HttpResponse> execute(Object requestObj) {
    HttpRequestBase request = (HttpRequestBase) requestObj;

    logger.debug("Request uri: {}", request.getURI());

    Future result =
        asyncClient.execute(
            request,
            new FutureCallback<HttpResponse>() {
              @Override
              public void completed(HttpResponse response) {
                logger.debug(
                    "Client Service responded with status code: {}",
                    response.getStatusLine().getStatusCode());
              }

              @Override
              public void failed(Exception e) {
                logger.error("Eva Http Client request encountered an error.", e);
              }

              @Override
              public void cancelled() {
                logger.warn("Eva Http Client request was cancelled.");
              }
            });

    return result;
  }
}
