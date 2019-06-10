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

/** Contains utility functions that have been implemented in the Eva Client Service. */
public class UtilFunctions {
  /**
   * Returns the first result of another `InlineFunction`.
   *
   * @param function Another `InlineFunction` to get the results from.
   * @return A function for the client service to run the first result of an `InlineFunction`.
   */
  public static InlineFunction first(InlineFunction function) {
    return new InlineFunction("first", String.format("[ %s ]", function));
  }

  /**
   * Returns the first result of the first result of another `InlineFunction`. This is the same as
   * chaining together two `first`s.
   *
   * @param function Another `InluneFunction` to get the results from.
   * @return A function for the client service to run and get the ffirst result of an
   *     `InlineFunction`.
   */
  public static InlineFunction ffirst(InlineFunction function) {
    return new InlineFunction("ffirst", String.format("[ %s ]", function));
  }
}
