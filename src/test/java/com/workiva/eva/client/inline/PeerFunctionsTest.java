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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.workiva.eva.client.Client;
import com.workiva.eva.client.Connection;
import com.workiva.eva.client.Database;
import com.workiva.eva.client.EvaClient;
import com.workiva.eva.client.Util;

import java.io.IOException;
import java.util.Map;

import static com.workiva.eva.client.TestConstants.CATEGORY;
import static com.workiva.eva.client.TestConstants.LABEL;
import static com.workiva.eva.client.TestConstants.TENANT;

public class PeerFunctionsTest {

  @Before
  public void setup() {
    EvaClient.start("url");
  }

  @After
  public void teardown() throws IOException {
    EvaClient.stop();
  }

  @Test
  public void queryTest() {
    Map connConfig = Util.createConnectionConfig(TENANT, CATEGORY, LABEL);
    Connection conn = Client.connect(connConfig);
    Database db = conn.db();

    String queryString = "[:find ?something :in $ ?arg1 $arg2 :where [?something ?arg1 ?arg2]]";

    InlineFunction query = PeerFunctions.query(eva.Util.read(queryString), db, "A string", 123);

    Assert.assertEquals(
        query.toString(),
        String.format(
            "#eva.client.service/inline { :fn query :params [\"%s\" [%s \"%s\" %s]] }",
            queryString, db, "\\\"A string\\\"", 123));
  }

  @Test
  public void queryWithStringTest() {
    Map connConfig = Util.createConnectionConfig(TENANT, CATEGORY, LABEL);
    Connection conn = Client.connect(connConfig);
    Database db = conn.db();

    String queryString = "[:find ?something :in $ ?arg1 $arg2 :where [?something ?arg1 ?arg2]]";

    InlineFunction query = PeerFunctions.query(queryString, db, "A string", 123);

    Assert.assertEquals(
        query.toString(),
        String.format(
            "#eva.client.service/inline { :fn query :params [\"%s\" [%s \"%s\" %s]] }",
            queryString, db, "\\\"A string\\\"", 123));
  }
}
