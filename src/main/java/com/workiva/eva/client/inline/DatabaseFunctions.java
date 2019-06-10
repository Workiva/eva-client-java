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

import com.workiva.eva.client.Database;

/** Contains database functions that have been implemented in the Eva Client Service. */
public class DatabaseFunctions {

  /**
   * Given an ident, or an entity id, return the corresponding entity id.
   *
   * @param db The database to perform this against.
   * @param ident The ident or entid.
   * @return An `InlineFunction` for the client service to run that returns an entity id.
   */
  public static InlineFunction entid(Database db, Object ident) {
    return new InlineFunction("entid", PersistentVector.create(db, ident));
  }

  /**
   * Given an entity id or an ident, return the corresponding ident.
   *
   * @param db The database to perform this against.
   * @param entid The entity id or ident.
   * @return An `InlineFunction` for the client service to run that returns an ident.
   */
  public static InlineFunction ident(Database db, Object entid) {
    return new InlineFunction("ident", PersistentVector.create(db, entid));
  }
}
