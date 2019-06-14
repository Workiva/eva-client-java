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

import clojure.lang.PersistentArrayMap;
import eva.error.v1.EvaErrorCode;

import java.util.HashMap;

/** Exceptions that are returned from the client service and have been sanitized. */
public class SanitizedEvaException extends EvaException {
  EvaException unsanitizedException;

  public SanitizedEvaException(EvaException e) {
    super("", PersistentArrayMap.create(new HashMap()));
    this.unsanitizedException = e;
  }

  public String getExplanation() {
    return unsanitizedException.getExplanation();
  }

  public String getType() {
    return unsanitizedException.getType();
  }

  public EvaErrorCode getErrorCode() {
    return unsanitizedException.getErrorCode();
  }

  @Override
  public SanitizedEvaException getCause() {
    if (unsanitizedException.getCause() != null) {
      return new SanitizedEvaException(unsanitizedException.getCause());
    }
    return null;
  }

  @Override
  public EvaException getUnsanitized() {
    return unsanitizedException;
  }
}
