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

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.util.ArrayList;
import java.util.List;

public class TestConstants {
  public static String TENANT = "tenant";
  public static String CATEGORY = "category";
  public static String LABEL = "label";
  public static String URL = "URL";

  public static String SNAPSHOT_REF = "#eva.client.service/snapshot-ref %s";
  public static String CONNECTION_REF = "#eva.client.service/connection-ref %s";

  public static BasicHeader header1 = new BasicHeader("key1", "val1");
  public static BasicHeader header2 = new BasicHeader("key2", "val2");
  public static List<Header> HEADERS = new ArrayList<>();

  public static EvaContext CONTEXT = new EvaContext();

  static {
    HEADERS.add(header1);
    HEADERS.add(header2);
  }
}
