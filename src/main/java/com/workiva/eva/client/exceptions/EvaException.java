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

import clojure.lang.ExceptionInfo;
import clojure.lang.IPersistentMap;
import clojure.lang.Keyword;
import clojure.lang.PersistentArrayMap;
import eva.error.v1.EvaErrorCode;

import java.util.HashMap;
import java.util.Map;

/** Exceptions that are returned from the client service. */
public class EvaException extends ExceptionInfo {

  private String explanation;
  private String type;
  private EvaErrorCode code;
  protected Map exInfo;
  private EvaException cause;

  EvaException(String message, IPersistentMap exData) {
    super(message, exData);
  }

  public EvaException(Map map) {
    this(
        (String) map.getOrDefault(Keyword.intern("message"), "Unknown error."),
        PersistentArrayMap.create(
            (Map) map.getOrDefault(Keyword.intern("ex-data"), new HashMap())));
    this.exInfo = (Map) map.getOrDefault(Keyword.intern("ex-info"), new HashMap());
    this.explanation =
        (String) exInfo.getOrDefault(Keyword.intern("explanation"), "Explanation unavailable.");
    this.type = (String) exInfo.getOrDefault(Keyword.intern("type"), "Unknown type.");
    this.code = EvaErrorCode.getFromLong((Long) exInfo.get((Keyword.intern("code"))));

    Object cause = map.getOrDefault(Keyword.intern("cause"), null);
    if (cause != null) {
      this.cause = new EvaException((Map) cause);
    }
  }

  public String getExplanation() {
    return explanation;
  }

  public String getType() {
    return type;
  }

  public EvaErrorCode getErrorCode() {
    return code;
  }

  @Override
  public EvaException getCause() {
    return cause;
  }

  public Map getExInfo() {
    return exInfo;
  }

  @Override
  public String toString() {
    return String.format(
        "%s\n%s %s\n%s %s\n%s %s\n%s %s\n%s %s",
        this.getClass().getName() + ": " + getMessage(),
        Keyword.intern("explanation"),
        getExplanation(),
        Keyword.intern("type"),
        getType(),
        Keyword.intern("code"),
        getErrorCode(),
        Keyword.intern("ex-data"),
        super.getData(),
        Keyword.intern("ex-info"),
        getExInfo());
  }

  public SanitizedEvaException getSanitized() {
    return new SanitizedEvaException(this);
  }

  public EvaException getUnsanitized() {
    return this;
  }
}
