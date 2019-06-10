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
import com.workiva.eva.client.inline.InlineFunction;

import java.util.List;
import java.util.Map;

/** Creates Pull, Pull Many, and Pull with Query requests. */
public class PullBuilder {
  final Object pattern;
  final Database db;

  public PullBuilder(Database db, Object pattern) {
    this.db = db;
    this.pattern = pattern;
  }

  public PullSingle withEntityId(Long entityId) {
    return new PullSingle(pattern, entityId, db);
  }

  public PullSingle withEntityId(InlineFunction func) {
    return new PullSingle(pattern, func, db);
  }

  public PullMany withEntityIds(List entityIds) {
    return new PullMany(pattern, entityIds, db);
  }

  public PullMany withEntityIds(Object... entityIds) {
    return new PullMany(pattern, entityIds, db);
  }

  /** Creates a Pull Many Request. */
  public class PullMany extends RequestBuilder<PullMany, List> {
    private Object entityIds;
    private Object pattern;

    private PullMany(Object pattern, Object entityIds, Database db) {
      super(db);
      this.pattern = pattern;
      this.entityIds = entityIds;
    }

    public List execute() {
      return clientHelper.pullMany(buildContext(), getRequestHeaders(), db, pattern, entityIds);
    }
  }

  /** Creates a Pull request. */
  public class PullSingle extends RequestBuilder<PullSingle, Map> {
    private Object entityIds;
    private Object pattern;

    private PullSingle(Object pattern, Object entityId, Database db) {
      super(db);
      this.pattern = pattern;
      this.entityIds = entityId;
    }

    public Map execute() {
      return clientHelper.pull(buildContext(), getRequestHeaders(), db, pattern, entityIds);
    }
  }
}
