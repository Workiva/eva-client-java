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
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

import static com.workiva.eva.client.TestConstants.CATEGORY;
import static com.workiva.eva.client.TestConstants.LABEL;
import static com.workiva.eva.client.TestConstants.TENANT;

public class UtilTest {
  @Test
  public void createConnectionConfigTest() {
    Map connectionConfig = Util.createConnectionConfig(TENANT, CATEGORY, LABEL);
    Assert.assertEquals(connectionConfig.get(Keyword.intern("tenant")), TENANT);
    Assert.assertEquals(connectionConfig.get(Keyword.intern("category")), CATEGORY);
    Assert.assertEquals(connectionConfig.get(Keyword.intern("label")), LABEL);
  }
}
