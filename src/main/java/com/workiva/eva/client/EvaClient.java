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

import java.io.IOException;

/** Performs the actual work of sending requests to the client service. */
public abstract class EvaClient implements AutoCloseable {

  static EvaClient evaClient;

  /** Creates and starts an {@link EvaHttpClient}. */
  public static EvaClient start(String url) {
    return start(new EvaHttpClient(url));
  }

  /** Sets the {@link EvaClient}. */
  public static EvaClient start(EvaClient client) {
    if (evaClient == null) {
      evaClient = client;
    }
    return evaClient;
  }

  /**
   * Stops the {@link EvaClient}, and sets it to null.
   *
   * @throws IOException error relating to closing the client
   */
  public static void stop() throws IOException {
    if (evaClient != null) {
      evaClient.close();
      evaClient = null;
    }
  }

  /**
   * Closes the Eva Client.
   *
   * @throws IOException error relating to closing the client
   */
  public abstract void close() throws IOException;

  /**
   * Gets the {@link EvaClient} being used.
   *
   * @return An {@link EvaClient}.
   */
  static EvaClient getEvaClient() {
    return evaClient;
  }

  /**
   * Executes a request against the client service.
   *
   * @param arg The request to be done.
   * @param <T> The type your {@link EvaClientHelper} expects.
   * @return the response from the client service.
   */
  abstract <T> T execute(Object arg);

  static EvaClientHelper getEvaClientHelper(String tenant, String category, String label) {
    return getEvaClient().buildClientHelper(tenant, category, label);
  }

  abstract EvaClientHelper buildClientHelper(String tenant, String category, String label);
}
