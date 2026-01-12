package com.salesforce.multicloudj.sts.model;

import lombok.Generated;

public class GetCallerIdentityRequest {
  private String aud;
  
  @Generated
  GetCallerIdentityRequest(String aud) {
    this.aud = aud;
  }
  
  @Generated
  public static GetCallerIdentityRequestBuilder builder() {
    return new GetCallerIdentityRequestBuilder();
  }
  
  @Generated
  public static class GetCallerIdentityRequestBuilder {
    @Generated
    private String aud;
    
    @Generated
    public GetCallerIdentityRequestBuilder aud(String aud) {
      this.aud = aud;
      return this;
    }
    
    @Generated
    public GetCallerIdentityRequest build() {
      return new GetCallerIdentityRequest(this.aud);
    }
    
    @Generated
    public String toString() {
      return "GetCallerIdentityRequest.GetCallerIdentityRequestBuilder(aud=" + this.aud + ")";
    }
  }
  
  @Generated
  public String getAud() {
    return this.aud;
  }
}
