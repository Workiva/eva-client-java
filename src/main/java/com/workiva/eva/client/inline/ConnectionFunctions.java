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

import com.workiva.eva.client.Connection;

/** Contains connection functions that have been implemented in the Eva Client Service. */
public class ConnectionFunctions {
  /**
   * Returns the latest tx id for a particular function.
   *
   * @param conn The connection to the Eva.
   * @return An `InlineFunction` for the client service to run that returns the latest tx id.
   */
  public static InlineFunction latestT(Connection conn) {
    return new InlineFunction("latestT", String.format("[ %s ]", conn));
  }
}
