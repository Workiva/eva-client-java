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

import clojure.java.api.Clojure;
import clojure.lang.IFn;
import clojure.lang.Keyword;
import eva.Peer;

import com.workiva.eva.client.exceptions.EvaClientException;

import java.util.Map;

/** Used to create connections and query Eva. */
public class Client {

  private static final IFn tempid;

  static {
    IFn require = Clojure.var("clojure.core", "require");
    require.invoke(Clojure.read("eva.client.core"));
    tempid = Clojure.var("eva.client.core", "tempid");
  }

  /** This shouldn't ever be instantiated. */
  private Client() {}

  /**
   * Creates a connection object that can be used to transact and create Databases.
   *
   * @param config The tenant, category, and label of the Eva db.
   * @return Returns a Connection to an Eva db.
   */
  public static Connection connect(Map<Keyword, String> config) {
    return new Connection(
        config.get(Keyword.intern("tenant")),
        config.get(Keyword.intern("category")),
        config.get(Keyword.intern("label")));
  }

  /**
   * Performs a query against an Eva db.
   *
   * @param q The query to be performed.
   * @param args Any arguments for the query, including the Database.
   * @return Returns the results of the query from the client service.
   * @throws EvaClientException client exception
   */
  public static <T> T query(Object q, Object... args) throws EvaClientException {
    return query(new EvaContext(), q, args);
  }

  /**
   * Performs a query against an Eva db.
   *
   * @param ctx The EvaContext used to add to request headers.
   * @param q The query to be performed.
   * @param args Any arguments for the query, including the Database.
   * @return Returns the results of the query from the client service.
   * @throws EvaClientException client exception
   */
  public static <T> T query(EvaContext ctx, Object q, Object... args) throws EvaClientException {
    Database db = null;
    for (int i = 0; i < args.length; i++) {
      if (args[i] instanceof Database) {
        db = (Database) args[i];
      }
    }
    return (T) db.query(ctx, q, args);
  }

  /**
   * Performs a query against an Eva db.
   *
   * @param q The query to be performed.
   * @param args Any arguments for the query, including the Database.
   * @return Returns the results of the query from the client service.
   * @throws EvaClientException client exception
   */
  public static <T> T query(String q, Object... args) throws EvaClientException {
    return query(new EvaContext(), eva.Util.read(q), args);
  }

  /**
   * Performs a query against an Eva db.
   *
   * @param ctx The EvaContext used to add to request headers.
   * @param q The query to be performed.
   * @param args Any arguments for the query, including the Database.
   * @return Returns the results of the query from the client service.
   * @throws EvaClientException client exception
   */
  public static <T> T query(EvaContext ctx, String q, Object... args) throws EvaClientException {
    return query(ctx, eva.Util.read(q), args);
  }

  /**
   * Gets the entity id given a tx id. This differs from the Eva Api which does not require a
   * database.
   *
   * @param tx The transaction id.
   * @return The entity id for that transaction.
   */
  public static Long toTxEid(Long tx) {
    return Peer.toTxEid(tx);
  }

  public static Object tempid(Object partition) {
    return tempid.invoke(partition);
  }

  public static Object tempid(Object partition, long idNumber) {
    return tempid.invoke(partition, idNumber);
  }
}
