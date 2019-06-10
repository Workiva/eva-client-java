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
import org.apache.http.StatusLine;
import org.apache.http.client.ResponseHandler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EvaClientFutureTest {

  HttpResponse httpResponse;

  @Before
  public void setup() {
    httpResponse = mock(HttpResponse.class);
    StatusLine statusLine = mock(StatusLine.class);
    when(statusLine.getStatusCode()).thenReturn(200);

    doReturn(statusLine).when(httpResponse).getStatusLine();
    when(httpResponse.getStatusLine()).thenReturn(statusLine);
  }

  private ResponseHandler<Object> responseHandler =
      response -> {
        return response.getStatusLine();
      };

  @Test
  public void FutureIsDoneTest() {
    HttpResponse httpResponse = mock(HttpResponse.class);
    StatusLine statusLine = mock(StatusLine.class);

    doReturn(statusLine).when(httpResponse).getStatusLine();
    when(httpResponse.getStatusLine()).thenReturn(statusLine);
    TestFuture tFuture = new TestFuture(httpResponse);
    EvaClientFuture future = new EvaClientFuture(tFuture, responseHandler);
    Assert.assertEquals(future.isDone(), tFuture.isDone());
  }

  @Test
  public void FutureIsCancelledTest() {
    TestFuture tFuture = new TestFuture(httpResponse);
    EvaClientFuture future = new EvaClientFuture(tFuture, responseHandler);
    Assert.assertEquals(future.isCancelled(), tFuture.isCancelled());
  }

  @Test
  public void FutureCancelTest() {
    TestFuture tFuture = new TestFuture(httpResponse);
    EvaClientFuture future = new EvaClientFuture(tFuture, responseHandler);
    Assert.assertEquals(future.cancel(true), tFuture.cancel(true));
  }

  @Test
  public void FutureGetTest() throws ExecutionException, InterruptedException {
    TestFuture tFuture = new TestFuture(httpResponse);
    EvaClientFuture future = new EvaClientFuture(tFuture, responseHandler);
    Assert.assertEquals(((StatusLine) future.get()).getStatusCode(), 200);
    Assert.assertEquals(((StatusLine) future.get()).getStatusCode(), 200);
  }

  class TestFuture<T> implements Future {
    HttpResponse val;

    TestFuture(HttpResponse val) {
      this.val = val;
    }

    @Override
    public boolean isDone() {
      return true;
    }

    @Override
    public boolean isCancelled() {
      return false;
    }

    @Override
    public boolean cancel(boolean cancelled) {
      return cancelled;
    }

    @Override
    public T get() throws ExecutionException, InterruptedException {
      return (T) val;
    }

    @Override
    public T get(long timeout, TimeUnit timeUnit) throws ExecutionException, InterruptedException {
      return (T) val;
    }
  }
}
