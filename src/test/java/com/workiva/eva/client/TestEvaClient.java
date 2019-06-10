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

import org.apache.commons.lang3.concurrent.ConcurrentUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicStatusLine;
import org.junit.Assert;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Future;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestEvaClient extends EvaClient {
  Future<HttpResponse> execute(Object arg) {
    HttpRequestBase req = (HttpRequestBase) arg;

    HttpResponse response = mock(CloseableHttpResponse.class);
    HttpEntity entity = mock(HttpEntity.class);

    when(response.getStatusLine())
        .thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "Ok!"));
    try {
      InputStream stream = new ByteArrayInputStream("nil".getBytes());
      when(entity.getContent()).thenReturn(stream);
    } catch (IOException e) {
      Assert.fail();
    }

    when(response.getEntity()).thenReturn(entity);
    return ConcurrentUtils.constantFuture(response);
  }

  @Override
  EvaClientHelper buildClientHelper(String tenant, String category, String label) {
    return new TestEvaClientHelper();
  }

  @Override
  public void close() {}
}
