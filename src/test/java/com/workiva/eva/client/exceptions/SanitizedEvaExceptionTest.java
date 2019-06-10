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

package com.workiva.eva.client.exceptions;

import clojure.lang.Keyword;
import clojure.lang.PersistentArrayMap;
import eva.error.v1.EvaErrorCode;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class SanitizedEvaExceptionTest {

  @Test
  public void exceptionsAreSanitizedTest() {
    Map exInfo =
        new HashMap() {
          {
            put(Keyword.intern("explanation"), "explanation");
            put(Keyword.intern("type"), "type");
            put(Keyword.intern("code"), 500l);
          }
        };
    Map exData =
        new HashMap() {
          {
            put(Keyword.intern("data"), "data");
          }
        };
    Map map =
        new HashMap() {
          {
            put(Keyword.intern("message"), "message");
            put(Keyword.intern("ex-info"), exInfo);
            put(Keyword.intern("ex-data"), exData);
          }
        };
    EvaException ex = new EvaException(map);
    SanitizedEvaException sanitizedEx = ex.getSanitized();

    Assert.assertTrue(sanitizedEx.getMessage().isEmpty());
    Assert.assertEquals(sanitizedEx.getType(), "type");
    Assert.assertEquals(sanitizedEx.getErrorCode(), EvaErrorCode.getFromLong(500l));
    Assert.assertEquals(sanitizedEx.getExplanation(), "explanation");
    Assert.assertEquals(sanitizedEx.getData(), PersistentArrayMap.create(new HashMap()));
  }
}
