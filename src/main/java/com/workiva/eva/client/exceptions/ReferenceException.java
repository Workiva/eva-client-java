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

import com.workiva.eva.client.Connection;
import com.workiva.eva.client.Database;

/** Exceptions created when {@link Connection} or {@link Database} can't be deserialized. */
public class ReferenceException extends EvaClientException {

  public ReferenceException(String message) {
    super(message);
  }

  public ReferenceException(String message, Throwable t) {
    super(message, t);
  }
}
