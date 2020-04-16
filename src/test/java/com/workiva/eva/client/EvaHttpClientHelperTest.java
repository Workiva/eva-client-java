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

import clojure.lang.Keyword;
import clojure.lang.PersistentVector;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.concurrent.ConcurrentUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicStatusLine;
import org.junit.After;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.workiva.eva.client.exceptions.EvaException;
import com.workiva.eva.client.exceptions.SanitizedEvaException;
import com.workiva.eva.client.exceptions.EvaClientException;

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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Ignore(
    "TODO - Tests are not hermetic, the first running test will "
        + "fail in CI, there is likely something incorrect about the setup")
public class EvaHttpClientHelperTest {

  static Map connConfig = Util.createConnectionConfig(TENANT, CATEGORY, LABEL);
  static EvaClient mockClient;

  static ArgumentCaptor postCaptor;

  String transaction = "[[:db/add #db/id[:db.part/user -1000001] :book/title \"First Book\"]]";

  public void createMockClient(String returnVal) throws Exception {
    mockClient = mock(TestEvaClient.class);
    postCaptor = ArgumentCaptor.forClass(HttpPost.class);

    HttpResponse response = mock(CloseableHttpResponse.class);
    HttpEntity entity = mock(HttpEntity.class);

    when(response.getStatusLine())
        .thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "Ok!"));
    when(response.getFirstHeader(HttpHeaders.CONTENT_TYPE))
        .thenReturn(new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/vnd.eva+edn"));
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
  }

  @After
  public void tearDown() throws IOException {
    EvaClient.stop();
  }

  @Test
  public void transactTest() throws Exception {
    createMockClient("{ :test \"passed\" }");
    EvaClient.start(mockClient);

    Connection conn = Client.connect(connConfig);
    conn.transact(transaction);

    verify(mockClient).execute(postCaptor.capture());
    HttpPost post = (HttpPost) postCaptor.getValue();

    Assert.assertEquals(
        post.getURI().toString(), new Endpoints(URL, TENANT, CATEGORY, LABEL).transact);

    Map<String, String> stringParams = getRequestParams(post);

    Assert.assertTrue(stringParams.containsKey("reference"));
    Assert.assertEquals(stringParams.get("reference"), conn.toString());

    Assert.assertTrue(stringParams.containsKey("transaction"));
    Assert.assertEquals(stringParams.get("transaction"), transaction);
  }

  @Test
  public void transactAsyncTest() throws Exception {
    createMockClient("{ :test \"passed\" }");
    EvaClient.start(mockClient);

    Connection conn = Client.connect(connConfig);
    conn.transactAsync(transaction);

    verify(mockClient).execute(postCaptor.capture());
    HttpPost post = (HttpPost) postCaptor.getValue();

    Assert.assertEquals(
        post.getURI().toString(), new Endpoints(URL, TENANT, CATEGORY, LABEL).transact);

    Map<String, String> stringParams = getRequestParams(post);

    Assert.assertTrue(stringParams.containsKey("reference"));
    Assert.assertEquals(stringParams.get("reference"), conn.toString());

    Assert.assertTrue(stringParams.containsKey("transaction"));
    Assert.assertEquals(stringParams.get("transaction"), transaction);
  }

  @Test
  public void queryTest() throws Exception {
    createMockClient("{ :test \"passed\" }");
    EvaClient.start(mockClient);
    Connection conn = Client.connect(connConfig);
    Database db = conn.db();

    String query =
        "[:find ?a :in ?b ?c ?d :where [?e :item/attr1 ?b]"
            + " [?e :item/attr2 ?c]"
            + " [?e :item/attr3 ?d]]";

    Client.query(query, db, 123l, "A string", Keyword.intern("keyword"));

    verify(mockClient).execute(postCaptor.capture());
    HttpPost post = (HttpPost) postCaptor.getValue();

    Assert.assertEquals(
        post.getURI().toString(), new Endpoints(URL, TENANT, CATEGORY, LABEL).query);

    Map<String, String> stringParams = getRequestParams(post);

    Assert.assertTrue(stringParams.containsKey("query"));
    Assert.assertEquals(stringParams.get("query"), query);
  }

  @Test
  public void pullTest() throws Exception {
    createMockClient("{}");
    EvaClient.start(mockClient);
    Connection conn = Client.connect(connConfig);
    Database db = conn.db();

    db.pull("[*]", 123l);

    verify(mockClient).execute(postCaptor.capture());
    HttpPost post = (HttpPost) postCaptor.getValue();

    Assert.assertEquals(post.getURI().toString(), new Endpoints(URL, TENANT, CATEGORY, LABEL).pull);

    Map<String, String> stringParams = getRequestParams(post);
    Assert.assertTrue(stringParams.containsKey("reference"));
    Assert.assertEquals(stringParams.get("reference"), db.toString());

    Assert.assertTrue(stringParams.containsKey("pattern"));
    Assert.assertEquals(stringParams.get("pattern"), "\"[*]\"");

    Assert.assertTrue(stringParams.containsKey("ids"));
    Assert.assertEquals(stringParams.get("ids"), "123");

    Assert.assertEquals(stringParams.size(), 3);
  }

  @Test
  public void pullManyTest() throws Exception {
    createMockClient("[]");
    EvaClient.start(mockClient);

    Connection conn = Client.connect(connConfig);
    Database db = conn.db();

    db.pullMany("[*]", PersistentVector.create(123l, 456l));

    verify(mockClient).execute(postCaptor.capture());
    HttpPost post = (HttpPost) postCaptor.getValue();

    Assert.assertEquals(post.getURI().toString(), new Endpoints(URL, TENANT, CATEGORY, LABEL).pull);

    Map<String, String> stringParams = getRequestParams(post);
    Assert.assertTrue(stringParams.containsKey("reference"));
    Assert.assertEquals(stringParams.get("reference"), db.toString());

    Assert.assertTrue(stringParams.containsKey("pattern"));
    Assert.assertEquals(stringParams.get("pattern"), "\"[*]\"");

    Assert.assertTrue(stringParams.containsKey("ids"));
    Assert.assertEquals(stringParams.get("ids"), "[123 456]");

    Assert.assertEquals(stringParams.size(), 3);
  }

  @Test
  public void invokeTest() throws Exception {
    createMockClient("{ :test \"passed\" }");
    EvaClient.start(mockClient);
    Connection conn = Client.connect(connConfig);
    Database db = conn.db();

    db.invoke(
        Keyword.intern("db.fn", "cas"),
        db,
        0,
        Keyword.intern("db", "doc"),
        "The default database partition.",
        "Testing");

    verify(mockClient).execute(postCaptor.capture());
    HttpPost post = (HttpPost) postCaptor.getValue();

    Assert.assertEquals(
        post.getURI().toString(), new Endpoints(URL, TENANT, CATEGORY, LABEL).invoke);

    Map<String, String> stringParams = getRequestParams(post);
    Assert.assertTrue(stringParams.containsKey("reference"));
    Assert.assertEquals(stringParams.get("reference"), db.toString());

    Assert.assertTrue(stringParams.containsKey("function"));
    Assert.assertEquals(stringParams.get("function"), ":db.fn/cas");

    Assert.assertTrue(stringParams.containsKey("p[0]"));
    Assert.assertEquals(stringParams.get("p[0]"), db.toString());

    Assert.assertTrue(stringParams.containsKey("p[1]"));
    Assert.assertEquals(stringParams.get("p[1]"), "0");

    Assert.assertTrue(stringParams.containsKey("p[2]"));
    Assert.assertEquals(stringParams.get("p[2]"), ":db/doc");

    Assert.assertTrue(stringParams.containsKey("p[3]"));
    Assert.assertEquals(stringParams.get("p[3]"), "\"The default database partition.\"");

    Assert.assertTrue(stringParams.containsKey("p[4]"));
    Assert.assertEquals(stringParams.get("p[4]"), "\"Testing\"");

    Assert.assertEquals(stringParams.size(), 7);
  }

  @Test
  public void entidTest() throws Exception {
    createMockClient("{ :test \"passed\" }");
    EvaClient.start(mockClient);
    Connection conn = Client.connect(connConfig);
    Database db = conn.db();

    Keyword ident = Keyword.intern("db.fn", "cas");
    db.entid(ident);

    verify(mockClient).execute(postCaptor.capture());
    HttpPost post = (HttpPost) postCaptor.getValue();

    Map<String, String> stringParams = getRequestParams(post);

    String x = post.getURI().toString();
    String y = new Endpoints(URL, TENANT, CATEGORY, LABEL).query;
    Assert.assertEquals(
        post.getURI().toString(), new Endpoints(URL, TENANT, CATEGORY, LABEL).entid);

    Assert.assertEquals(stringParams.get("reference"), db.toString());
    Assert.assertEquals(stringParams.get("ident"), ident.toString());
  }

  @Test
  public void identTest() throws Exception {
    createMockClient("{ :test \"passed\" }");
    EvaClient.start(mockClient);
    Connection conn = Client.connect(connConfig);
    Database db = conn.db();

    Long entid = 1L;
    db.ident(entid);

    verify(mockClient).execute(postCaptor.capture());
    HttpPost post = (HttpPost) postCaptor.getValue();

    Map<String, String> stringParams = getRequestParams(post);

    Assert.assertEquals(
        post.getURI().toString(), new Endpoints(URL, TENANT, CATEGORY, LABEL).ident);

    Assert.assertEquals(stringParams.get("reference"), db.toString());
    Assert.assertEquals(stringParams.get("entid"), entid.toString());
  }

  @Test
  public void latestTTest() throws Exception {
    createMockClient("{ :test \"passed\" }");
    mockClient = mock(TestEvaClient.class);
    postCaptor = ArgumentCaptor.forClass(HttpPost.class);

    HttpResponse response = mock(CloseableHttpResponse.class);
    HttpEntity entity = mock(HttpEntity.class);

    when(response.getStatusLine())
        .thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "Ok!"));
    when(response.getFirstHeader(HttpHeaders.CONTENT_TYPE))
        .thenReturn(new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/vnd.eva+edn"));
    try {
      InputStream stream = new ByteArrayInputStream("13".getBytes());
      when(entity.getContent()).thenReturn(stream);
    } catch (IOException e) {
      fail();
    }

    when(response.getEntity()).thenReturn(entity);
    when(mockClient.execute(any(Object.class)))
        .thenReturn(ConcurrentUtils.constantFuture(response));
    when(mockClient.buildClientHelper(TENANT, CATEGORY, LABEL))
        .thenReturn(new EvaHttpClientHelper(URL, TENANT, CATEGORY, LABEL));

    EvaClient.start(mockClient);
    Connection conn = Client.connect(connConfig);

    ArgumentCaptor getCaptor = ArgumentCaptor.forClass(HttpGet.class);
    ;
    conn.latestT();

    verify(mockClient).execute(getCaptor.capture());
    HttpGet get = (HttpGet) getCaptor.getValue();

    Object headers = get.getAllHeaders();
    System.out.println("test");
  }

  @Test
  public void jsonTest() throws Exception {
    mockClient = mock(TestEvaClient.class);
    postCaptor = ArgumentCaptor.forClass(HttpPost.class);

    HttpResponse response = mock(CloseableHttpResponse.class);
    HttpEntity entity = mock(HttpEntity.class);

    when(response.getStatusLine())
        .thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "Ok!"));
    when(response.getFirstHeader(HttpHeaders.CONTENT_TYPE))
        .thenReturn(new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json"));
    try {
      InputStream stream = new ByteArrayInputStream("{\"results\": \"Hello World\"}".getBytes());
      when(entity.getContent()).thenReturn(stream);
    } catch (IOException e) {
      fail();
    }

    when(response.getEntity()).thenReturn(entity);
    when(mockClient.execute(any(Object.class)))
        .thenReturn(ConcurrentUtils.constantFuture(response));
    when(mockClient.buildClientHelper(TENANT, CATEGORY, LABEL))
        .thenReturn(new EvaHttpClientHelper(URL, TENANT, CATEGORY, LABEL));

    EvaClient.start(mockClient);

    Connection conn = Client.connect(connConfig);

    Map<Keyword, Object> result = conn.transact("[]");
    Assert.assertEquals("Hello World", result.get("results"));
  }

  @Test
  public void textTest() throws Exception {
    mockClient = mock(TestEvaClient.class);
    postCaptor = ArgumentCaptor.forClass(HttpPost.class);

    HttpResponse response = mock(CloseableHttpResponse.class);
    HttpEntity entity = mock(HttpEntity.class);

    when(response.getStatusLine())
        .thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "Ok!"));
    try {
      InputStream stream = new ByteArrayInputStream("{\"results\": \"Hello World\"}".getBytes());
      when(entity.getContent()).thenReturn(stream);
    } catch (IOException e) {
      fail();
    }

    when(response.getEntity()).thenReturn(entity);
    when(mockClient.execute(any(Object.class)))
        .thenReturn(ConcurrentUtils.constantFuture(response));
    when(mockClient.buildClientHelper(TENANT, CATEGORY, LABEL))
        .thenReturn(new EvaHttpClientHelper(URL, TENANT, CATEGORY, LABEL));

    EvaClient.start(mockClient);

    Connection conn = Client.connect(connConfig);
    Database db = conn.db();

    Object result = db.query(new EvaContext(), "");
    Assert.assertEquals("{\"results\": \"Hello World\"}", result);
  }

  @Test
  public void handle400IsNotOkTest() throws Exception {
    mockClient = mock(TestEvaClient.class);
    postCaptor = ArgumentCaptor.forClass(HttpPost.class);

    HttpResponse response = mock(CloseableHttpResponse.class);
    HttpEntity entity = mock(HttpEntity.class);

    when(response.getStatusLine())
        .thenReturn(
            new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_BAD_REQUEST, "Not Ok!"));
    when(response.getFirstHeader(HttpHeaders.CONTENT_TYPE))
        .thenReturn(new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/vnd.eva+edn"));
    try {
      InputStream stream = new ByteArrayInputStream("{:test :passed}".getBytes());
      when(entity.getContent()).thenReturn(stream);
    } catch (IOException e) {
      fail();
    }

    when(response.getEntity()).thenReturn(entity);
    when(mockClient.execute(any(Object.class)))
        .thenReturn(ConcurrentUtils.constantFuture(response));
    when(mockClient.buildClientHelper(TENANT, CATEGORY, LABEL))
        .thenReturn(new EvaHttpClientHelper(URL, TENANT, CATEGORY, LABEL));

    EvaClient.start(mockClient);

    Connection conn = Client.connect(connConfig);

    try {
      conn.transact("[]");
      Assert.fail();
    } catch (SanitizedEvaException e) {
      Assert.assertEquals(e.getUnsanitized().getMessage(), "Unknown error.");
      Assert.assertEquals(e.getType(), "Unknown type.");
      Assert.assertEquals(e.getErrorCode(), null);
      Assert.assertEquals(e.getExplanation(), "Explanation unavailable.");
      Assert.assertEquals(e.getData(), new HashMap());
    }
  }

  @Test
  public void handle300IsNotOkTest() throws Exception {
    mockClient = mock(TestEvaClient.class);
    postCaptor = ArgumentCaptor.forClass(HttpPost.class);

    HttpResponse response = mock(CloseableHttpResponse.class);
    HttpEntity entity = mock(HttpEntity.class);

    when(response.getStatusLine())
        .thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, 300, "Not Ok!"));
    when(response.getFirstHeader(HttpHeaders.CONTENT_TYPE))
        .thenReturn(new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/vnd.eva+edn"));
    try {
      InputStream stream = new ByteArrayInputStream("{:test :failed}".getBytes());
      when(entity.getContent()).thenReturn(stream);
    } catch (IOException e) {
      fail();
    }

    when(response.getEntity()).thenReturn(entity);
    when(mockClient.execute(any(Object.class)))
        .thenReturn(ConcurrentUtils.constantFuture(response));
    when(mockClient.buildClientHelper(TENANT, CATEGORY, LABEL))
        .thenReturn(new EvaHttpClientHelper(URL, TENANT, CATEGORY, LABEL));

    EvaClient.start(mockClient);

    Connection conn = Client.connect(connConfig);

    try {
      conn.transact("[]");
      fail();
    } catch (SanitizedEvaException e) {
      Assert.assertEquals(e.getUnsanitized().getMessage(), "Unknown error.");
    }
  }

  @Test
  public void handle199IsNotOkTest() throws Exception {
    mockClient = mock(TestEvaClient.class);
    postCaptor = ArgumentCaptor.forClass(HttpPost.class);

    HttpResponse response = mock(CloseableHttpResponse.class);
    HttpEntity entity = mock(HttpEntity.class);

    when(response.getStatusLine())
        .thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, 199, "Not Ok!"));
    when(response.getFirstHeader(HttpHeaders.CONTENT_TYPE))
        .thenReturn(new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/vnd.eva+edn"));
    try {
      InputStream stream = new ByteArrayInputStream("{:test :failed}".getBytes());
      when(entity.getContent()).thenReturn(stream);
    } catch (IOException e) {
      fail();
    }

    when(response.getEntity()).thenReturn(entity);
    when(mockClient.execute(any(Object.class)))
        .thenReturn(ConcurrentUtils.constantFuture(response));
    when(mockClient.buildClientHelper(TENANT, CATEGORY, LABEL))
        .thenReturn(new EvaHttpClientHelper(URL, TENANT, CATEGORY, LABEL));

    EvaClient.start(mockClient);

    Connection conn = Client.connect(connConfig);

    try {
      conn.transact("[]");
      fail();
    } catch (SanitizedEvaException e) {
      EvaException unsanitized = e.getUnsanitized();
      String message = unsanitized.getMessage();
      Assert.assertEquals(message, "Unknown error.");
    }
  }

  @Test
  public void handleNotValidEdn() throws Exception {
    mockClient = mock(TestEvaClient.class);
    postCaptor = ArgumentCaptor.forClass(HttpPost.class);

    HttpResponse response = mock(CloseableHttpResponse.class);
    HttpEntity entity = mock(HttpEntity.class);

    when(response.getStatusLine())
        .thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, 199, "Not Ok!"));
    when(response.getFirstHeader(HttpHeaders.CONTENT_TYPE))
        .thenReturn(new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/vnd.eva+edn"));
    try {
      InputStream stream =
          new ByteArrayInputStream("[this is most definitely not valid)".getBytes());
      when(entity.getContent()).thenReturn(stream);
    } catch (IOException e) {
      fail();
    }

    when(response.getEntity()).thenReturn(entity);
    when(mockClient.execute(any(Object.class)))
        .thenReturn(ConcurrentUtils.constantFuture(response));
    when(mockClient.buildClientHelper(TENANT, CATEGORY, LABEL))
        .thenReturn(new EvaHttpClientHelper(URL, TENANT, CATEGORY, LABEL));

    EvaClient.start(mockClient);

    Connection conn = Client.connect(connConfig);

    try {
      conn.transact("[]");
      fail();
    } catch (EvaClientException e) {
      Assert.assertEquals("Error occurred reading client service response.", e.getMessage());
    }
  }

  @Test
  public void handleNoAuthToken() throws Exception {
    mockClient = mock(TestEvaClient.class);
    postCaptor = ArgumentCaptor.forClass(HttpPost.class);

    HttpResponse response = mock(CloseableHttpResponse.class);
    HttpEntity entity = mock(HttpEntity.class);

    when(response.getStatusLine())
        .thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, 400, "Not Ok!"));
    when(response.getFirstHeader(HttpHeaders.CONTENT_TYPE))
        .thenReturn(new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/vnd.eva+edn"));
    try {
      InputStream stream =
          new ByteArrayInputStream(
              "No Authorization Header with Bearer token present in request".getBytes());
      when(entity.getContent()).thenReturn(stream);
    } catch (IOException e) {
      fail();
    }

    when(response.getEntity()).thenReturn(entity);
    when(mockClient.execute(any(Object.class)))
        .thenReturn(ConcurrentUtils.constantFuture(response));
    when(mockClient.buildClientHelper(TENANT, CATEGORY, LABEL))
        .thenReturn(new EvaHttpClientHelper(URL, TENANT, CATEGORY, LABEL));

    EvaClient.start(mockClient);

    Connection conn = Client.connect(connConfig);

    try {
      conn.transact("[]");
      fail();
    } catch (EvaClientException e) {
      Assert.assertEquals(
          e.getMessage(), "No Authorization Header with Bearer token present in request");
    }
  }

  @Test
  public void handleNoAuthTokenAsync() throws Exception {
    mockClient = mock(TestEvaClient.class);
    postCaptor = ArgumentCaptor.forClass(HttpPost.class);

    HttpResponse response = mock(CloseableHttpResponse.class);
    HttpEntity entity = mock(HttpEntity.class);

    when(response.getStatusLine())
        .thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, 400, "Not Ok!"));
    when(response.getFirstHeader(HttpHeaders.CONTENT_TYPE))
        .thenReturn(new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/vnd.eva+edn"));
    try {
      InputStream stream =
          new ByteArrayInputStream(
              "No Authorization Header with Bearer token present in request".getBytes());
      when(entity.getContent()).thenReturn(stream);
    } catch (IOException e) {
      fail();
    }

    when(response.getEntity()).thenReturn(entity);
    when(mockClient.execute(any(Object.class)))
        .thenReturn(ConcurrentUtils.constantFuture(response));
    when(mockClient.buildClientHelper(TENANT, CATEGORY, LABEL))
        .thenReturn(new EvaHttpClientHelper(URL, TENANT, CATEGORY, LABEL));

    EvaClient.start(mockClient);

    Connection conn = Client.connect(connConfig);

    try {
      conn.transactAsync("[]").get();
      // TODO - Flakey Test, sometimes does NOT return the no auth header error as expected.
      fail();
    } catch (EvaClientException e) {
      Assert.assertEquals(
          e.getMessage(), "No Authorization Header with Bearer token present in request");
    }
  }

  private Map<String, String> getRequestParams(HttpPost post) throws IOException {
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
