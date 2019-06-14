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
import org.apache.http.Header;

import com.workiva.eva.client.exceptions.EvaClientException;
import com.workiva.eva.client.inline.InlineFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents an Eva Database object. Is able to create databases at new points in time, perform
 * pulls, and invoke.
 */
public class Database extends Reference {

  // Methods in this class don't allow for custom headers, but they still need to be passed.
  private static final List<Header> EMPTY_HEADERS = new ArrayList<>();

  private static final String SNAPSHOT_REF_STRING_PREFIX = "#eva.client.service/snapshot-ref { ";

  protected final Object asOf;
  protected boolean syncDb = false;
  protected Long asOfT = null;
  protected Long basisT = null;
  protected Long snapshotT = null;

  public Database(String tenant, String category, String label) {
    super(tenant, category, label);
    this.asOf = null;
  }

  public Database(String tenant, String category, String label, Object asOf, boolean syncDb) {
    super(tenant, category, label);
    this.asOf = asOf;
    this.syncDb = syncDb;
  }

  Database(
      String tenant,
      String category,
      String label,
      Object asOf,
      boolean syncDb,
      Long asOfT,
      Long basisT,
      Long snapshotT) {
    this(tenant, category, label, asOf, syncDb);
    this.asOfT = asOfT;
    this.basisT = basisT;
    this.snapshotT = snapshotT;
  }

  /**
   * Creates a Database at a specific point in time based on an InlineFunction.
   *
   * @param f The InlineFunction that will determine the point in time of this Database.
   * @return A Database snapshot.
   */
  public Database asOf(InlineFunction f) {
    return new Database(this.tenant, this.category, this.label, f, this.syncDb);
  }

  /**
   * Creates a Database at a specific point in time based on a tx id.
   *
   * @param t The tx id.
   * @return A Database snapshot.
   */
  public Database asOf(Long t) {
    return new Database(this.tenant, this.category, this.label, t, this.syncDb);
  }

  /**
   * Performs an Eva Pull.
   *
   * @param pattern The pattern to be used in the pull.
   * @param id The entity id for the pull.
   * @return The result of an Eva Pull.
   * @throws EvaClientException client exception
   */
  public Map<Keyword, Object> pull(Object pattern, Object id) throws EvaClientException {
    return clientHelper.pull(new EvaContext(), EMPTY_HEADERS, this, pattern, id);
  }

  /**
   * Performs an Eva Pull.
   *
   * @param ctx The EvaContext used to add to request headers.
   * @param pattern The pattern to be used in the pull.
   * @param id The entity id for the pull.
   * @return The result of an Eva Pull.
   * @throws EvaClientException client exception
   */
  public Map<Keyword, Object> pull(EvaContext ctx, Object pattern, Object id)
      throws EvaClientException {
    return clientHelper.pull(ctx, EMPTY_HEADERS, this, pattern, id);
  }

  /**
   * Performs an Eva Pull with multiple entity ids.
   *
   * @param pattern The pattern to be used in the pull.
   * @param entityIds The entity ids for the pull.
   * @return The result of an Eva Pull.
   * @throws EvaClientException client exception
   */
  public List<Map<Keyword, Object>> pullMany(Object pattern, List entityIds)
      throws EvaClientException {
    return clientHelper.pullMany(
        new EvaContext(), EMPTY_HEADERS, this, pattern, PersistentVector.create(entityIds));
  }

  /**
   * Performs an Eva Pull with multiple entity ids.
   *
   * @param ctx The EvaContext used to add to request headers.
   * @param pattern The pattern to be used in the pull.
   * @param entityIds The entity ids for the pull.
   * @return The result of an Eva Pull.
   * @throws EvaClientException client exception
   */
  public List<Map<Keyword, Object>> pullMany(EvaContext ctx, Object pattern, List entityIds)
      throws EvaClientException {
    return clientHelper.pullMany(
        ctx, EMPTY_HEADERS, this, pattern, PersistentVector.create(entityIds));
  }

  /**
   * Returns the latest tx id of this Database object that the Peer knows about. Database objects
   * created by Connection objects do not have an as-of value set on them, unless the asOf value was
   * specified upon its creation.
   *
   * @return The basis-t value of the Database if it has one.
   * @throws EvaClientException client exception
   */
  public Long basisT() throws EvaClientException {
    if (basisT != null) {
      return basisT;
    }

    throw new EvaClientException("basis-t Value was never set on Database object.");
  }

  /**
   * Returns the as of value if this snapshot is not the most recent.
   *
   * @return the as-of-t value of the Database.
   * @throws EvaClientException client exception
   */
  public Long asOfT() throws EvaClientException {
    return asOfT;
  }

  public Long snapshotT() throws EvaClientException {
    if (snapshotT != null) {
      return snapshotT;
    }

    throw new EvaClientException("snapshot-t Value was never set on Database object.");
  }

  /**
   * Looks up database function identified by entityId and invokes the function with args.
   *
   * @param dbFunction The entity-identifier of the function.
   * @param args The arguments for the database function.
   * @return The result of an Eva invoke.
   * @throws EvaClientException client exception
   */
  public Object invoke(Object dbFunction, Object... args) throws EvaClientException {
    return invoke(new EvaContext(), dbFunction, args);
  }

  /**
   * Looks up database function identified by entityId and invokes the function with args.
   *
   * @param ctx The EvaContext used to add to request headers.
   * @param dbFunction The entity-identifier of the function.
   * @param args The arguments for the database function.
   * @return The result of an Eva invoke.
   * @throws EvaClientException client exception
   */
  public Object invoke(EvaContext ctx, Object dbFunction, Object... args)
      throws EvaClientException {
    return clientHelper.invoke(ctx, EMPTY_HEADERS, this, dbFunction, args);
  }

  /**
   * Performs an Eva query.
   *
   * @param ctx The EvaContext used to add to request headers.
   * @param query The query to be performed.
   * @param args The arguments for that query.
   * @return The results of the query.
   * @throws EvaClientException client exception
   */
  Object query(EvaContext ctx, Object query, Object... args) throws EvaClientException {
    return clientHelper.query(ctx, EMPTY_HEADERS, query, args);
  }

  /**
   * Performs an Eva entid.
   *
   * @param ident The ident of the entity.
   * @return The results of an Eva ident.
   * @throws EvaClientException client exception
   */
  public Object entid(Object ident) throws EvaClientException {
    return entid(new EvaContext(), ident);
  }

  /**
   * Performs an Eva entid.
   *
   * @param ctx The EvaContext used to add to request headers.
   * @param ident The ident of the entity.
   * @return The results of an Eva ident.
   * @throws EvaClientException client exception
   */
  public Object entid(EvaContext ctx, Object ident) throws EvaClientException {
    return clientHelper.entid(ctx, EMPTY_HEADERS, this, ident, false);
  }

  /**
   * Performs an Eva ident.
   *
   * @param entid The id of the entity.
   * @return The results of an Eva entid.
   * @throws EvaClientException client exception
   */
  public Object ident(Object entid) throws EvaClientException {
    return ident(new EvaContext(), entid);
  }

  /**
   * Performs an Eva ident.
   *
   * @param ctx The EvaContext used to add to request headers.
   * @param entid The id of the entity.
   * @return The results of an Eva entid.
   * @throws EvaClientException client exception
   */
  public Object ident(EvaContext ctx, Object entid) throws EvaClientException {
    return clientHelper.ident(ctx, EMPTY_HEADERS, this, entid, false);
  }

  @Override
  public String toString() {
    StringBuilder dbEdnStringBuilder = new StringBuilder(SNAPSHOT_REF_STRING_PREFIX);
    dbEdnStringBuilder.append(String.format(":label \"%s\" ", this.label));

    if (this.asOf != null) {
      dbEdnStringBuilder.append(String.format(":as-of %s ", this.asOf));
    }

    if (this.syncDb != false) {
      dbEdnStringBuilder.append(String.format(":sync-db %s ", this.syncDb));
    }

    dbEdnStringBuilder.append("}");

    return dbEdnStringBuilder.toString();
  }
}
