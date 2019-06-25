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

import clojure.lang.ExceptionInfo;
import clojure.lang.Keyword;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.workiva.eva.client.exceptions.EvaClientException;
import com.workiva.eva.client.exceptions.EvaException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/** Creates the requests to be executed and passes them to the {@link EvaHttpClient}. */
public class EvaHttpClientHelper implements EvaClientHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(EvaHttpClient.class);

  private final Endpoints endpoints;
  private EdnSerializer ednSerializer;

  private static final String CORRELATION_ID_HEADER = "_cid";

  EvaHttpClientHelper(String url, String tenant, String category, String label) {
    this.endpoints = new Endpoints(url, tenant, category, label);
    this.ednSerializer = new EdnSerializer(tenant, category);
  }

  private Object deserializeResponse(HttpResponse response, String responseString) {
    Header contentTypeHeader = response.getFirstHeader(HttpHeaders.CONTENT_TYPE);
    String contentType =
        contentTypeHeader == null
            ? ContentType.TEXT_PLAIN.getMimeType()
            : contentTypeHeader.getValue();
    if (contentType.startsWith("application/json")) {
      Type type = new TypeToken<Map<String, Object>>() {}.getType();
      return new Gson().fromJson(responseString, type);
    } else if (contentType.startsWith("application/vnd.eva+edn")
        || contentType.startsWith("application/edn")) {
      return ednSerializer.deserialize(responseString);
    }
    return responseString;
  }

  /** Deserializes the Http Responses. */
  private ResponseHandler<Object> responseHandler =
      response -> {
        StatusLine statusLine = response.getStatusLine();
        int status = statusLine.getStatusCode();
        String responseString = "";

        try {
          responseString = EntityUtils.toString(response.getEntity());
          Object deserializedResponse = deserializeResponse(response, responseString);
          if (status < 200 || status >= 300) {
            if (deserializedResponse instanceof Map) {
              EvaException exception = new EvaException((Map) deserializedResponse);
              if (Environment.shouldSanitizeExceptions()) {
                exception = exception.getSanitized();
              }
              throw exception;
            } else {
              throw new EvaClientException(responseString);
            }
          }
          return deserializedResponse;
        } catch (ExceptionInfo ex) {
          throw ex;
        } catch (EvaClientException ex) {
          throw ex;
        } catch (Exception ex) {
          if (responseString.equals("")) {
            throw new ClientProtocolException(
                String.format("\nEmpty Client Service Response with status code: %d", status), ex);
          } else {
            throw new ClientProtocolException(
                String.format(
                    "\nUnexpected exception occurred with status code: %d\nStacktrace: %s",
                    status, responseString),
                ex);
          }
        }
      };

  private void addHeaders(EvaContext context, List<Header> headers, HttpRequestBase request) {

    request.setHeader(CORRELATION_ID_HEADER, context.getCorrelationId());
    context.addTracingHeaders(request);
    requestMiddleware(request);

    headers.forEach((h) -> request.setHeader(h));
  }

  /**
   * Operations to be applied to all requests prior to being sent off to client-service.
   *
   * @param request The request in question.
   */
  private void requestMiddleware(HttpRequestBase request) {
    return;
  }

  /**
   * Gets the {@link EvaHttpClient} to execute the response, and deserializes it.
   *
   * @param request The request to be executed.
   * @return response
   * @throws EvaClientException client exception
   */
  private <T> T doWork(EvaContext context, List<Header> headers, HttpRequestBase request)
      throws EvaClientException {
    addHeaders(context, headers, request);
    Future future = EvaHttpClient.getEvaClient().execute(request);
    return (T) new EvaClientFuture(future, responseHandler).get();
  }

  /**
   * Asynchronously get the {@link EvaHttpClient} to execute the response, and deserializes it.
   *
   * @param request The request to be executed.
   * @return response
   */
  private EvaClientFuture doWorkAsync(
      EvaContext context, List<Header> headers, HttpEntityEnclosingRequestBase request) {
    addHeaders(context, headers, request);
    Future future = EvaHttpClient.getEvaClient().execute(request);
    return new EvaClientFuture(future, responseHandler);
  }

  /**
   * Executes a transaction synchronously.
   *
   * @param txData The tx data.
   * @param conn The {@link Connection} for the transaction.
   * @return The result of the transaction
   * @throws EvaClientException client exception
   */
  @Override
  public Map transact(EvaContext context, List<Header> headers, List txData, Connection conn) {
    HttpPost post = transactHelper(txData, conn);
    post.setHeader(HttpHeaders.ACCEPT, "application/vnd.eva+edn");
    return doWork(context, headers, post);
  }

  /**
   * Executes a transaction asynchronously.
   *
   * @param txData The tx data.
   * @param conn The {@link Connection} for the transaction.
   * @return The promise to handle the result of the transaction
   */
  @Override
  public Future<Map<Keyword, Object>> transactAsync(
      EvaContext context, List<Header> headers, List txData, Connection conn) {
    HttpPost post = transactHelper(txData, conn);
    post.setHeader(HttpHeaders.ACCEPT, "application/vnd.eva+edn");
    return doWorkAsync(context, headers, post);
  }

  /**
   * Creates the transaction request to be done either synchronously or asynchronously.
   *
   * @param txData The tx data.
   * @param conn The {@link Connection} for the transaction.
   * @return The transaction results.
   */
  private HttpPost transactHelper(List txData, Connection conn) {
    List<NameValuePair> formFields = new ArrayList<>();
    formFields.add(new BasicNameValuePair("reference", ednSerializer.serialize(conn)));
    formFields.add(new BasicNameValuePair("transaction", ednSerializer.serialize(txData)));

    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formFields, Consts.UTF_8);
    HttpPost post = new HttpPost(endpoints.transact);
    post.setHeader(HttpHeaders.ACCEPT, "application/vnd.eva+edn");
    post.setEntity(entity);
    return post;
  }

  /**
   * Executes a query request.
   *
   * @param q The query to be done.
   * @param args The arguments for the query.
   * @return The query results.
   * @throws EvaClientException client exception
   */
  @Override
  public Object query(EvaContext context, List<Header> headers, Object q, Object... args) {
    List<NameValuePair> formFields = new ArrayList<>();
    formFields.add(new BasicNameValuePair("query", ednSerializer.serialize(q)));

    for (int i = 0; i < args.length; i++) {
      formFields.add(
          new BasicNameValuePair(String.format("p[%d]", i), ednSerializer.serialize(args[i])));
    }

    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formFields, Consts.UTF_8);
    HttpPost post = new HttpPost(endpoints.query);
    post.setHeader(HttpHeaders.ACCEPT, "application/vnd.eva+edn");
    post.setEntity(entity);
    return doWork(context, headers, post);
  }

  /**
   * Executes a pull request.
   *
   * @param db The {@link Database} to do the pull with.
   * @param pattern The pattern to be used.
   * @param entityId The entity id (or query).
   * @param queryArgs Arguments for the query.
   * @return The pull results.
   * @throws EvaClientException client exception
   */
  @Override
  public Map pull(
      EvaContext context,
      List<Header> headers,
      Database db,
      Object pattern,
      Object entityId,
      Object... queryArgs) {
    return pullHelper(context, headers, db, pattern, entityId, queryArgs);
  }

  /**
   * Executes a pull many request.
   *
   * @param db The {@link Database} to do the pull with.
   * @param pattern The pattern to be used.
   * @param entityIds The entity ids (or query).
   * @param queryArgs Arguments for the query.
   * @return The pull results.
   * @throws EvaClientException client exception
   */
  @Override
  public List<Map<Keyword, Object>> pullMany(
      EvaContext context,
      List<Header> headers,
      Database db,
      Object pattern,
      Object entityIds,
      Object... queryArgs) {
    return pullHelper(context, headers, db, pattern, entityIds, queryArgs);
  }

  /**
   * Executes a pull or pull many request.
   *
   * @param db The {@link Database} to do the pull with.
   * @param pattern The pattern to be used.
   * @param entityIds The entity ids (or query).
   * @param queryArgs Arguments for the query.
   * @return The pull results.
   * @throws EvaClientException client exception
   */
  <T> T pullHelper(
      EvaContext context,
      List<Header> headers,
      Database db,
      Object pattern,
      Object entityIds,
      Object... queryArgs)
      throws EvaClientException {
    List<NameValuePair> formFields = new ArrayList<>();
    formFields.add(new BasicNameValuePair("reference", ednSerializer.serialize(db)));
    formFields.add(new BasicNameValuePair("pattern", ednSerializer.serialize(pattern)));
    formFields.add(new BasicNameValuePair("ids", ednSerializer.serialize(entityIds)));

    for (int i = 0; i < queryArgs.length; i++) {
      formFields.add(
          new BasicNameValuePair(String.format("p[%d]", i), ednSerializer.serialize(queryArgs[i])));
    }

    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formFields, Consts.UTF_8);
    HttpPost post = new HttpPost(endpoints.pull);
    post.setHeader(HttpHeaders.ACCEPT, "application/vnd.eva+edn");
    post.setEntity(entity);
    return doWork(context, headers, post);
  }

  /**
   * Executes an invoke request.
   *
   * @param db The {@link Database} to do the invoke with.
   * @param function The function to be invoked.
   * @param invokeArgs Any arguments to be used by the function.
   * @return The invoke results.
   * @throws EvaClientException client exception
   */
  @Override
  public Object invoke(
      EvaContext context,
      List<Header> headers,
      Database db,
      Object function,
      Object... invokeArgs) {
    List<NameValuePair> formFields = new ArrayList<>();
    formFields.add(new BasicNameValuePair("reference", ednSerializer.serialize(db)));
    formFields.add(new BasicNameValuePair("function", ednSerializer.serialize(function)));
    for (int i = 0; i < invokeArgs.length; i++) {
      formFields.add(
          new BasicNameValuePair(
              String.format("p[%d]", i), ednSerializer.serialize(invokeArgs[i])));
    }

    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formFields, Consts.UTF_8);
    HttpPost post = new HttpPost(endpoints.invoke);
    post.setHeader(HttpHeaders.ACCEPT, "application/vnd.eva+edn");
    post.setEntity(entity);
    return doWork(context, headers, post);
  }

  @Override
  public Object entid(
      EvaContext context, List<Header> headers, Database db, Object ident, boolean strict) {
    List<NameValuePair> formFields = new ArrayList<>();
    formFields.add(new BasicNameValuePair("reference", ednSerializer.serialize(db)));
    formFields.add(new BasicNameValuePair("ident", ednSerializer.serialize(ident)));
    formFields.add(new BasicNameValuePair("strict", ednSerializer.serialize(strict)));

    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formFields, Consts.UTF_8);
    HttpPost post = new HttpPost(endpoints.entid);
    post.setEntity(entity);
    return doWork(context, headers, post);
  }

  @Override
  public Object ident(
      EvaContext context, List<Header> headers, Database db, Object entid, boolean strict) {
    List<NameValuePair> formFields = new ArrayList<>();
    formFields.add(new BasicNameValuePair("reference", ednSerializer.serialize(db)));
    formFields.add(new BasicNameValuePair("entid", ednSerializer.serialize(entid)));
    formFields.add(new BasicNameValuePair("strict", ednSerializer.serialize(strict)));

    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formFields, Consts.UTF_8);
    HttpPost post = new HttpPost(endpoints.ident);
    post.setHeader(HttpHeaders.ACCEPT, "application/vnd.eva+edn");
    post.setEntity(entity);
    return doWork(context, headers, post);
  }

  @Override
  public Long latestT(EvaContext context, List<Header> headers) {
    HttpGet get = new HttpGet(endpoints.latestT);
    get.setHeader(HttpHeaders.ACCEPT, "application/vnd.eva+edn");
    return doWork(context, headers, get);
  }
}
