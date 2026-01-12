package com.salesforce.multicloudj.sts.model;

import lombok.Generated;

public class CallerIdentity {
  String userId;
  
  String cloudResourceName;
  
  String accountId;
  
  @Generated
  public String getUserId() {
    return this.userId;
  }
  
  @Generated
  public String getCloudResourceName() {
    return this.cloudResourceName;
  }
  
  @Generated
  public String getAccountId() {
    return this.accountId;
  }
  
  public CallerIdentity(String userId, String cloudResourceName, String accountId) {
    this.userId = userId;
    this.cloudResourceName = cloudResourceName;
    this.accountId = accountId;
  }
}
