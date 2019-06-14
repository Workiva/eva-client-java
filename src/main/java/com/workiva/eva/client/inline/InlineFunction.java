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

/**
 * A function for the client service to run. These functions can be passed as arguments to queries,
 * db as-of values, etc.
 */
public class InlineFunction {
  private final String functionName;
  private final Object parameters;

  InlineFunction(String name, Object parameters) {
    this.functionName = name;
    this.parameters = parameters;
  }

  @Override
  public String toString() {
    return String.format(
        "#eva.client.service/inline { :fn %s :params %s }", functionName, parameters);
  }
}
