package com.salesforce.multicloudj.sts.model;

public class StsCredentials {
  String accessKeyId;
  
  String accessKeySecret;
  
  String securityToken;
  
  public StsCredentials(String accessKeyId, String accessKeySecret, String securityToken) {
    this.accessKeyId = accessKeyId;
    this.accessKeySecret = accessKeySecret;
    this.securityToken = securityToken;
  }
  
  public String getAccessKeyId() {
    return this.accessKeyId;
  }
  
  public String getSecurityToken() {
    return this.securityToken;
  }
  
  public String getAccessKeySecret() {
    return this.accessKeySecret;
  }
}
