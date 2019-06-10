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

/** Creates the endpoints for a specific tenant and category. */
class Endpoints {
  private final String version = "v.1";
  public final String transact;
  public final String query;
  public final String pull;
  public final String invoke;
  public final String entity;
  public final String latestT;
  public final String ident;
  public final String entid;

  Endpoints(String url, String tenant, String category, String label) {
    transact = String.format("%s/eva/%s/transact/%s/%s", url, version, tenant, category);
    query = String.format("%s/eva/%s/q/%s/%s", url, version, tenant, category);
    pull = String.format("%s/eva/%s/pull/%s/%s", url, version, tenant, category);
    invoke = String.format("%s/eva/%s/invoke/%s/%s", url, version, tenant, category);
    entity = String.format("%s/eva/%s/entity/%s/%s", url, version, tenant, category);
    ident = String.format("%s/eva/%s/ident/%s/%s", url, version, tenant, category);
    entid = String.format("%s/eva/%s/entid/%s/%s", url, version, tenant, category);
    latestT = String.format("%s/eva/%s/latestT/%s/%s/%s", url, version, tenant, category, label);
  }
}
