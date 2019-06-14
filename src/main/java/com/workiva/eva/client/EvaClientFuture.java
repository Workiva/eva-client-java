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
import org.apache.http.client.ResponseHandler;

import com.workiva.eva.client.exceptions.EvaClientException;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Client service results need to be deserialized before they can be passed back to users. The
 * HttpAsyncClient returns a future of the HttpResponse, this response needs to be deserialized. By
 * wrapping the Http client's futures with this, users can be given future as they'd expect that
 * deserialize to what they expect.
 *
 * @param <V> The type of the data to be returned.
 */
public class EvaClientFuture<V> implements Future {
  Future<HttpResponse> future;
  ResponseHandler handler;

  EvaClientFuture(Future future, ResponseHandler<Object> handler) {
    this.future = future;
    this.handler = handler;
  }

  /** A wrapper for the future's `isDone`. */
  @Override
  public boolean isDone() {
    return future.isDone();
  }

  /** A wrapper for the future's `isCancelled`. */
  @Override
  public boolean isCancelled() {
    return future.isCancelled();
  }

  /** A wrapper for the future's `cancel`. */
  @Override
  public boolean cancel(boolean cancelled) {
    return future.cancel(cancelled);
  }

  /** A wrapper for the future's `get`. */
  @Override
  public V get() throws EvaClientException {
    try {
      HttpResponse response = future.get();
      return (V) handler.handleResponse(response);
    } catch (InterruptedException e) {
      throw new EvaClientException(
          "Thread interrupted while waiting for response from client service.", e);
    } catch (ExecutionException e) {
      throw new EvaClientException("Error executing request to client service.", e);
    } catch (IOException e) {
      throw new EvaClientException("Error occurred reading client service response.", e);
    }
  }

  /** A wrapper for the future's `get`. */
  @Override
  public V get(long timeout, TimeUnit timeUnit)
      throws ExecutionException, InterruptedException, TimeoutException {
    try {
      return (V) handler.handleResponse(future.get(timeout, timeUnit));
    } catch (IOException e) {
      throw new EvaClientException("Error occurred reading client service response", e);
    }
  }
}
