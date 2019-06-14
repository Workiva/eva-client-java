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

import com.workiva.eva.client.Database;
import com.workiva.eva.client.RequestBuilder;

/** Creates entid requests. */
public class EntidBuilder extends RequestBuilder<EntidBuilder, Object> {
  private final Database db;
  private final Object ident;
  private boolean strict = false;

  public EntidBuilder(Database db, Object ident) {
    super(db);
    this.db = db;
    this.ident = ident;
  }

  /**
   * Performs an ident-strict instead of ident which will throw an exception if the ident is not
   * found.
   *
   * @return The ident if found, else an exception.
   */
  public EntidBuilder asStrict() {
    strict = true;
    return this;
  }

  public Object execute() {
    return clientHelper.entid(buildContext(), getRequestHeaders(), db, ident, strict);
  }
}
