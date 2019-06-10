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
import org.apache.http.Header;

import com.workiva.eva.client.exceptions.EvaClientException;
import com.workiva.eva.client.inline.InlineFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/** Represents a connection to an Eva database. */
public class Connection extends Reference {

  // Methods in this class don't allow for custom headers, but they still need to be passed.
  private static final List<Header> EMPTY_HEADERS = new ArrayList<>();

  public Connection(String tenant, String category, String label) {
    super(tenant, category, label);
  }

  /**
   * Returns the latest `Database` for this connection.
   *
   * @return a database
   */
  public Database db() {
    return new Database(tenant, category, label, null, false);
  }

  /**
   * Returns a `Database` at a particular transaction id.
   *
   * @param t The transaction id.
   * @return A database.
   */
  public Database dbAt(Long t) {
    return new Database(tenant, category, label, t, false);
  }

  /**
   * Returns a `Database` where the as-of value is to be determined based on an `InlineFunction`.
   *
   * @param f The `InlineFunction` that will be used to determine the as-of value.
   * @return A database.
   */
  public Database dbAt(InlineFunction f) {
    return new Database(tenant, category, label, f, false);
  }

  /** Returns a `Database` where the sync-db value is set to true. */
  public Database syncDb() {
    return new Database(tenant, category, label, null, true);
  }

  /**
   * Performs a transaction against the Eva Client Service.
   *
   * @param tx The transaction data.
   * @return The results of the Eva transaction.
   * @throws EvaClientException client exception
   */
  public Map<Keyword, Object> transact(List tx) throws EvaClientException {
    return transact(new EvaContext(), tx);
  }

  /**
   * Performs a transaction against the Eva Client Service.
   *
   * @param ctx The EvaContext used to add to request headers.
   * @param tx The transaction data.
   * @return The results of the Eva transaction.
   * @throws EvaClientException client exception
   */
  public Map<Keyword, Object> transact(EvaContext ctx, List tx) throws EvaClientException {
    return clientHelper.transact(ctx, EMPTY_HEADERS, tx, this);
  }

  /**
   * Performs a transaction against the Eva Client Service.
   *
   * @param tx The transaction data string.
   * @return The results of the Eva transaction.
   * @throws EvaClientException client exception
   */
  public Map<Keyword, Object> transact(String tx) throws EvaClientException {
    return transact(new EvaContext(), (List) eva.Util.read(tx));
  }

  /**
   * Performs a transaction against the Eva Client Service.
   *
   * @param ctx The EvaContext used to add to request headers.
   * @param tx The transaction data string.
   * @return The results of the Eva transaction.
   * @throws EvaClientException client exception
   */
  public Map<Keyword, Object> transact(EvaContext ctx, String tx) throws EvaClientException {
    return clientHelper.transact(ctx, EMPTY_HEADERS, (List) eva.Util.read(tx), this);
  }

  /**
   * Asynchronously performs a transaction to the Eva Client Service.
   *
   * @param tx The transaction data.
   * @return The result of the Eva transaction.
   */
  public Future<Map<Keyword, Object>> transactAsync(List tx) {
    return transactAsync(new EvaContext(), tx);
  }

  /**
   * Asynchronously performs a transaction to the Eva Client Service.
   *
   * @param ctx The EvaContext used to add to request headers.
   * @param tx The transaction data.
   * @return The result of the Eva transaction.
   */
  public Future<Map<Keyword, Object>> transactAsync(EvaContext ctx, List tx) {
    return clientHelper.transactAsync(ctx, EMPTY_HEADERS, tx, this);
  }

  /**
   * Asynchronously performs a transaction to the Eva Client Service.
   *
   * @param tx The transaction data string.
   * @return The result of the Eva transaction.
   */
  public Future<Map<Keyword, Object>> transactAsync(String tx) {
    return transactAsync(new EvaContext(), (List) eva.Util.read(tx));
  }

  /**
   * Asynchronously performs a transaction to the Eva Client Service.
   *
   * @param ctx The EvaContext used to add to request headers.
   * @param tx The transaction data string.
   * @return The result of the Eva transaction.
   */
  public Future<Map<Keyword, Object>> transactAsync(EvaContext ctx, String tx) {
    return clientHelper.transactAsync(ctx, EMPTY_HEADERS, (List) eva.Util.read(tx), this);
  }

  /**
   * Performs an Eva latestT action.
   *
   * @return the result of the Eva latestT.
   */
  public Long latestT() {
    return latestT(new EvaContext());
  }

  /**
   * Performs an Eva latestT action.
   *
   * @param ctx The EvaContext used to add to request headers.
   * @return the result of the Eva latestT.
   */
  public Long latestT(EvaContext ctx) {
    return clientHelper.latestT(ctx, EMPTY_HEADERS);
  }

  public String toString() {
    return String.format("#eva.client.service/connection-ref { :label \"%s\" }", this.label);
  }
}
