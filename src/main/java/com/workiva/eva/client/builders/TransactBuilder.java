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

package com.workiva.eva.client.builders;

import clojure.lang.Keyword;

import com.workiva.eva.client.Connection;
import com.workiva.eva.client.RequestBuilder;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/** Creates Transaction requests. */
public class TransactBuilder extends RequestBuilder<TransactBuilder, Map> {
  final List txData;
  final Connection conn;

  public TransactBuilder(Connection conn, List txData) {
    super(conn);
    this.txData = txData;
    this.conn = conn;
  }

  public Map execute() {
    return clientHelper.transact(buildContext(), getRequestHeaders(), txData, conn);
  }

  Future<Map<Keyword, Object>> executeAsync() {
    return clientHelper.transactAsync(buildContext(), getRequestHeaders(), txData, conn);
  }
}
