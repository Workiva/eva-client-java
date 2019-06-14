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

import clojure.lang.IPersistentMap;
import clojure.lang.Keyword;
import clojure.lang.PersistentArrayMap;
import eva.error.v1.EvaErrorCode;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class EvaExceptionTest {

  @Test
  public void testConstructor() {
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

    Assert.assertEquals(ex.getMessage(), "message");
    Assert.assertEquals(ex.getType(), "type");
    Assert.assertEquals(ex.getErrorCode(), EvaErrorCode.getFromLong(500l));
    Assert.assertEquals(ex.getExplanation(), "explanation");
    Assert.assertEquals(ex.getData(), exData);
  }

  @Test
  public void testDefaults() {
    Map map = new HashMap();
    EvaException ex = new EvaException(map);

    Assert.assertEquals(ex.getMessage(), "Unknown error.");
    Assert.assertEquals(ex.getType(), "Unknown type.");
    Assert.assertEquals(ex.getErrorCode(), null);
    Assert.assertEquals(ex.getExplanation(), "Explanation unavailable.");
    Assert.assertEquals(ex.getData(), new HashMap());
  }

  @Test
  public void testToString() {
    Map exInfo =
        new HashMap() {
          {
            put(Keyword.intern("explanation"), "explanation");
            put(Keyword.intern("type"), "type");
            put(Keyword.intern("code"), -1l);
            put(Keyword.intern("cause"), null);
          }
        };
    Map exDataMap =
        new HashMap() {
          {
            put(Keyword.intern("key"), "val");
          }
        };

    IPersistentMap persistentExData = PersistentArrayMap.create(exDataMap);

    Map map =
        new HashMap() {
          {
            put(Keyword.intern("message"), "message");
            put(Keyword.intern("ex-info"), exInfo);
            put(Keyword.intern("ex-data"), persistentExData);
          }
        };
    EvaException ex = new EvaException(map);

    Assert.assertEquals(
        ex.toString(),
        String.format(
            "%s\n%s %s\n%s %s\n%s %s\n%s %s\n%s %s",
            EvaException.class.getName() + ": message",
            Keyword.intern("explanation"),
            "explanation",
            Keyword.intern("type"),
            "type",
            Keyword.intern("code"),
            "UNKNOWN_ERROR",
            Keyword.intern("ex-data"),
            persistentExData,
            Keyword.intern("ex-info"),
            exInfo));
  }
}
