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
import clojure.lang.PersistentArrayMap;
import clojure.lang.PersistentVector;
import eva.Datom;
import eva.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.workiva.eva.client.exceptions.ReferenceException;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/** Serializes and deserializes Edn. */
public class EdnSerializer {

  private static final Logger logger = LoggerFactory.getLogger(EdnSerializer.class);

  private static final IFn datomFunction;
  private static final IFn dbFn;
  private static final IFn prnStrVar = Clojure.var("clojure.core", "pr-str");
  private Map<String, Function> deserializers;

  private static final Keyword LABEL_KEY = Keyword.intern("label");
  private static final Keyword AS_OF_KEY = Keyword.intern("as-of");
  private static final Keyword AS_OF_T_KEY = Keyword.intern("as-of-t");
  private static final Keyword BASIS_T_KEY = Keyword.intern("basis-t");
  private static final Keyword SNAPSHOT_T_KEY = Keyword.intern("snapshot-t");

  static {
    IFn require = Clojure.var("clojure.core", "require");
    require.invoke(Clojure.read("eva.datom"));
    datomFunction = Clojure.var("eva.datom", "datom");

    require.invoke(Clojure.read("eva.functions"));
    dbFn = Clojure.var("eva.functions", "compile-db-fn");

    // required to properly 'Edn'ify Database and Connection references to what
    // the client service expects
    require.invoke(Clojure.read("eva.client.reference"));
    require.invoke(Clojure.read("eva.client.inline_function"));
    require.invoke(Clojure.read("eva.client.functions"));
  }

  public EdnSerializer(String tenant, String category) {
    deserializers =
        new HashMap<String, Function>() {
          {
            put("eva.functions.DBFn", (param) -> createDBFn(param));
            put("datom", (param) -> createDatom(param));
            put("eva.client.service/snapshot-ref", (param) -> createDbRef(param, tenant, category));
            put(
                "eva.client.service/connection-ref",
                (param) -> createConnRef(param, tenant, category));
            put("object", (param) -> objectStringifier(param));
          }
        };
  }

  // Ideally we don't send object EDN tags back from the client service... but if we do
  // we should be able to handle them instead of blowing up because there isn't a reader function.
  private String objectStringifier(Object obj) {
    logger.warn("Converting #object EDN tag to String.");
    return obj.toString();
  }

  public String serialize(Object obj) {
    return (String) prnStrVar.invoke(obj);
  }

  public <T> T deserialize(String obj) {
    return (T) Util.read(deserializers, obj);
  }

  private static Object createDBFn(Object param) {
    PersistentArrayMap dbFnMap = (PersistentArrayMap) param;

    return dbFn.invoke(dbFnMap);
  }

  // TODO: A Datom reader function should probably be added to Eva
  private static Datom createDatom(Object param) {
    PersistentVector datomVec = (PersistentVector) param;

    return (Datom) datomFunction.applyTo(datomVec.seq());
  }

  /**
   * Creates a {@link Database}.
   *
   * @param param The string representation of the Database.
   * @param tenant The tenant of the database.
   * @param category The category of the database.
   * @return A {@link Database}
   */
  private static Database createDbRef(Object param, String tenant, String category) {
    if (param instanceof Map) {
      Map dbMap = (Map) param;
      String label = (String) dbMap.get(LABEL_KEY);
      Long asOf = (Long) dbMap.get(AS_OF_KEY);
      Long asOfT = (Long) dbMap.get(AS_OF_T_KEY);
      Long basisT = (Long) dbMap.get(BASIS_T_KEY);
      Long snapshotT = (Long) dbMap.get(SNAPSHOT_T_KEY);
      return new Database(tenant, category, label, asOf, false, asOfT, basisT, snapshotT);
    }
    throw new ReferenceException("Unsupported Database Reference type");
  }

  /**
   * Creates a {@link Connection}.
   *
   * @param param The string representation of the Connection.
   * @param tenant The tenant of the connection.
   * @param category The category of the connection.
   * @return A {@link Connection}
   */
  private static Connection createConnRef(Object param, String tenant, String category) {
    if (param instanceof Map) {
      Map connMap = (Map) param;
      Keyword labelKey = Keyword.intern("label");
      String label = (String) connMap.get(labelKey);
      return new Connection(tenant, category, label);
    }
    throw new ReferenceException("Unsupported Connection Reference type");
  }
}
