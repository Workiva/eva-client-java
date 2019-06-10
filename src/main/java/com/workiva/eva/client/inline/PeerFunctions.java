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

package com.workiva.eva.client.inline;

import clojure.lang.PersistentVector;

/** Contains Peer functions that have been implemented in the Eva Client Service. */
public class PeerFunctions {

  /**
   * Performs a query.
   *
   * @param query The query.
   * @param parameters Any arguments for that query.
   * @return An `InlineFunction` for the Eva Client Service to run that returns the query results.
   */
  public static InlineFunction query(Object query, Object... parameters) {
    Object[] parametersWithQuery = new Object[2];
    parametersWithQuery[0] = String.format("%s", query);

    for (int i = 0; i < parameters.length; i++) {
      if (parameters[i] instanceof String) {
        parameters[i] = String.format("\"%s\"", parameters[i]);
      }
    }

    parametersWithQuery[1] = PersistentVector.create(parameters);
    return new InlineFunction("query", PersistentVector.create(parametersWithQuery));
  }

  /**
   * Performs a query.
   *
   * @param query The query.
   * @param parameters Any arguments for that query.
   * @return An `InlineFunction` for the Eva Client Service to run that returns the query results.
   */
  public static InlineFunction query(String query, Object... parameters) {
    return query(eva.Util.read(query), parameters);
  }
}
