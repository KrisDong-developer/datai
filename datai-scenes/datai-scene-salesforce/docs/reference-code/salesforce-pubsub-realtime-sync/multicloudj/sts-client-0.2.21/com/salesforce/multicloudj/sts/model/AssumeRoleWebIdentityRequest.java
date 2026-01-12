package com.salesforce.multicloudj.sts.model;

import lombok.Generated;

public class AssumeRoleWebIdentityRequest {
  private final String role;
  
  private final String webIdentityToken;
  
  private final String sessionName;
  
  private final int expiration;
  
  @Generated
  AssumeRoleWebIdentityRequest(String role, String webIdentityToken, String sessionName, int expiration) {
    this.role = role;
    this.webIdentityToken = webIdentityToken;
    this.sessionName = sessionName;
    this.expiration = expiration;
  }
  
  @Generated
  public static AssumeRoleWebIdentityRequestBuilder builder() {
    return new AssumeRoleWebIdentityRequestBuilder();
  }
  
  @Generated
  public static class AssumeRoleWebIdentityRequestBuilder {
    @Generated
    private String role;
    
    @Generated
    private String webIdentityToken;
    
    @Generated
    private String sessionName;
    
    @Generated
    private int expiration;
    
    @Generated
    public AssumeRoleWebIdentityRequestBuilder role(String role) {
      this.role = role;
      return this;
    }
    
    @Generated
    public AssumeRoleWebIdentityRequestBuilder webIdentityToken(String webIdentityToken) {
      this.webIdentityToken = webIdentityToken;
      return this;
    }
    
    @Generated
    public AssumeRoleWebIdentityRequestBuilder sessionName(String sessionName) {
      this.sessionName = sessionName;
      return this;
    }
    
    @Generated
    public AssumeRoleWebIdentityRequestBuilder expiration(int expiration) {
      this.expiration = expiration;
      return this;
    }
    
    @Generated
    public AssumeRoleWebIdentityRequest build() {
      return new AssumeRoleWebIdentityRequest(this.role, this.webIdentityToken, this.sessionName, this.expiration);
    }
    
    @Generated
    public String toString() {
      return "AssumeRoleWebIdentityRequest.AssumeRoleWebIdentityRequestBuilder(role=" + this.role + ", webIdentityToken=" + this.webIdentityToken + ", sessionName=" + this.sessionName + ", expiration=" + this.expiration + ")";
    }
  }
  
  @Generated
  public String getRole() {
    return this.role;
  }
  
  @Generated
  public String getWebIdentityToken() {
    return this.webIdentityToken;
  }
  
  @Generated
  public String getSessionName() {
    return this.sessionName;
  }
  
  @Generated
  public int getExpiration() {
    return this.expiration;
  }
}
