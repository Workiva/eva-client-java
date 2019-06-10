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
import org.apache.commons.lang3.concurrent.ConcurrentUtils;
import org.apache.http.Header;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public class TestEvaClientHelper implements EvaClientHelper {

  EdnSerializer serializer = new EdnSerializer("test", "test");

  @Override
  public List query(EvaContext context, List<Header> headers, Object q, Object... args) {
    return PersistentVector.create(q, args, context, headers);
  }

  @Override
  public Map transact(EvaContext context, List<Header> headers, List tx, Connection conn) {
    return new HashMap() {
      {
        put("tx", tx);
        put("connection", conn);
        put("context", context);
        put("headers", headers);
      }
    };
  }

  @Override
  public Future<Map<Keyword, Object>> transactAsync(
      EvaContext context, List<Header> headers, List tx, Connection conn) {
    Map map =
        new HashMap() {
          {
            put("tx", tx);
            put("connection", conn);
            put("context", context);
            put("headers", headers);
          }
        };

    return ConcurrentUtils.constantFuture(map);
  }

  @Override
  public Map pull(
      EvaContext context,
      List<Header> headers,
      Database db,
      Object pattern,
      Object entityId,
      Object... queryArgs) {
    return new HashMap() {
      {
        put("db", db);
        put("pattern", pattern);
        put("id", entityId);
        put("args", queryArgs);
        put("context", context);
        put("headers", headers);
      }
    };
  }

  @Override
  public List pullMany(
      EvaContext context,
      List<Header> headers,
      Database db,
      Object pattern,
      Object entityIds,
      Object... queryArgs) {
    return PersistentVector.create(db, pattern, entityIds, queryArgs, context, headers);
  }

  @Override
  public Object invoke(
      EvaContext context,
      List<Header> headers,
      Database db,
      Object function,
      Object... invokeArgs) {
    HashMap map = new HashMap();
    map.put("db", db);
    map.put("function", function);
    map.put("args", invokeArgs);
    map.put("context", context);
    map.put("headers", headers);
    return map;
  }

  @Override
  public Object entid(
      EvaContext context, List<Header> headers, Database db, Object ident, boolean strict) {
    Map map = new HashMap();
    map.put("db", db);
    map.put("ident", ident);
    map.put("strict", strict);
    map.put("context", context);
    map.put("headers", headers);

    return map;
  }

  @Override
  public Object ident(
      EvaContext context, List<Header> headers, Database db, Object entid, boolean strict) {
    Map map = new HashMap();
    map.put("db", db);
    map.put("entid", entid);
    map.put("strict", strict);
    map.put("context", context);
    map.put("headers", headers);

    return map;
  }

  @Override
  public Long latestT(EvaContext context, List<Header> headers) {
    return 1L;
  }
}
