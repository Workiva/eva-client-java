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

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * Interacts with the {@link Database} and {@link Connection} to help create requests for an {@link
 * EvaClient} to use.
 */
public interface EvaClientHelper {
  Map<Keyword, Object> transact(
      EvaContext context, List<Header> headers, List txData, Connection conn);

  Future<Map<Keyword, Object>> transactAsync(
      EvaContext context, List<Header> headers, List txData, Connection conn);

  Object query(EvaContext context, List<Header> headers, Object q, Object... args);

  Map pull(
      EvaContext context,
      List<Header> headers,
      Database db,
      Object pattern,
      Object entityId,
      Object... queryArgs);

  List<Map<Keyword, Object>> pullMany(
      EvaContext context,
      List<Header> headers,
      Database db,
      Object pattern,
      Object entityIds,
      Object... queryArgs);

  Object invoke(
      EvaContext context, List<Header> headers, Database db, Object function, Object... invokeArgs);

  Object entid(EvaContext context, List<Header> headers, Database db, Object ident, boolean strict);

  Object ident(EvaContext context, List<Header> headers, Database db, Object entid, boolean strict);

  Long latestT(EvaContext context, List<Header> headers);
}
