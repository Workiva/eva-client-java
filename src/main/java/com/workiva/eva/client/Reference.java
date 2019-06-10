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

/**
 * Contains the basic information required by {@link Connection} and {@link Database} to perform
 * their requests.
 */
class Reference {
  protected EvaClientHelper clientHelper;
  protected final String tenant;
  protected final String category;
  protected final String label;

  public Reference(String tenant, String category, String label) {
    this.clientHelper = EvaClient.getEvaClientHelper(tenant, category, label);

    this.tenant = tenant;
    this.category = category;
    this.label = label;
  }

  public String getTenant() {
    return tenant;
  }

  public String getCategory() {
    return category;
  }

  public String getLabel() {
    return label;
  }
}
