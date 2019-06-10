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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.concurrent.ConcurrentUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicStatusLine;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.workiva.eva.client.TestConstants.CATEGORY;
import static com.workiva.eva.client.TestConstants.LABEL;
import static com.workiva.eva.client.TestConstants.TENANT;
import static com.workiva.eva.client.TestConstants.URL;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestUtils {

  public static EvaClient createMockClient(String returnVal) throws Exception {
    EvaClient mockClient = mock(TestEvaClient.class);

    HttpResponse response = mock(CloseableHttpResponse.class);
    HttpEntity entity = mock(HttpEntity.class);

    when(response.getStatusLine())
        .thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "Ok!"));
    try {
      InputStream stream = new ByteArrayInputStream(returnVal.getBytes());
      when(entity.getContent()).thenReturn(stream);
    } catch (IOException e) {
      fail();
    }

    when(response.getEntity()).thenReturn(entity);
    when(mockClient.execute(any(Object.class)))
        .thenReturn(ConcurrentUtils.constantFuture(response));
    when(mockClient.buildClientHelper(TENANT, CATEGORY, LABEL))
        .thenReturn(new EvaHttpClientHelper(URL, TENANT, CATEGORY, LABEL));

    return mockClient;
  }

  public static Map<String, String> getRequestParams(HttpPost post) throws IOException {
    InputStream stream = post.getEntity().getContent();
    String contentString = IOUtils.toString(stream, StandardCharsets.UTF_8);

    String[] splitString =
        URLDecoder.decode(contentString, StandardCharsets.UTF_8.name()).split("&");

    Map<String, String> map = new HashMap();
    for (String string : splitString) {
      String[] keyVal = string.split("=");
      map.put(keyVal[0], keyVal[1]);
    }

    return map;
  }
}
