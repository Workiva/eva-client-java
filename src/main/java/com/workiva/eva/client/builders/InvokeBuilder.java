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

import com.workiva.eva.client.Database;
import com.workiva.eva.client.RequestBuilder;

/** Creates Invoke requests. */
public class InvokeBuilder extends RequestBuilder<InvokeBuilder, Object> {
  private Database db;
  private Keyword dbFunction;
  private Object[] args = new Object[0];

  public InvokeBuilder(Database db, Keyword dbFunction) {
    super(db);
    this.db = db;
    this.dbFunction = dbFunction;
  }

  public InvokeBuilder withArgs(Object... args) {
    this.args = args;
    return this;
  }

  public Object execute() {
    return clientHelper.invoke(buildContext(), getRequestHeaders(), db, dbFunction, args);
  }
}
