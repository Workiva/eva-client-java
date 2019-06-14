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

package com.workiva.eva.client;

import clojure.lang.Keyword;

import java.util.HashMap;
import java.util.Map;

/** Utility functions to make life easier. */
public class Util {

  /**
   * Creates a connection map that can be used to create a {@link Connection}.
   *
   * @param tenant The tenant of your database.
   * @param category The category of your database.
   * @param label The label of your database.
   * @return A map.
   */
  public static Map createConnectionConfig(String tenant, String category, String label) {
    HashMap map = new HashMap();
    map.put(Keyword.intern("tenant"), tenant);
    map.put(Keyword.intern("category"), category);
    map.put(Keyword.intern("label"), label);

    return map;
  }
}
