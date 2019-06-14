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

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore("javax.net.ssl.*")
public class EvaClientTest {

  @After
  public void tearDown() throws IOException {
    EvaClient.stop();
  }

  @Test
  public void createClientTest() throws IOException {
    EvaHttpClient client = new EvaHttpClient("http://localhost:8080");
    EvaClient.start(client);
    Assert.assertEquals(client, EvaClient.getEvaClient());

    EvaClient.stop();
  }

  @Test
  public void createClientWithStringTest() throws IOException {
    EvaClient.start("http://localhost:8080");
    Assert.assertTrue(EvaClient.getEvaClient() instanceof EvaHttpClient);
    EvaClient.stop();
  }

  @Test
  public void stopEvaClient() throws IOException {
    EvaClient.start("http://localhost:8080");
    Assert.assertNotNull(EvaClient.getEvaClient());
    EvaClient.stop();
    Assert.assertNull(EvaClient.getEvaClient());
    EvaClient.stop();
    Assert.assertNull(EvaClient.getEvaClient());
  }
}
