package com.salesforce.multicloudj.sts.model;

import java.util.function.Supplier;
import lombok.Generated;

public class CredentialsOverrider {
  protected CredentialsType type;
  
  protected StsCredentials sessionCredentials;
  
  protected String role;
  
  protected Integer durationSeconds;
  
  protected String sessionName;
  
  protected Supplier<String> webIdentityTokenSupplier;
  
  @Generated
  public CredentialsType getType() {
    return this.type;
  }
  
  @Generated
  public StsCredentials getSessionCredentials() {
    return this.sessionCredentials;
  }
  
  @Generated
  public String getRole() {
    return this.role;
  }
  
  @Generated
  public Integer getDurationSeconds() {
    return this.durationSeconds;
  }
  
  @Generated
  public String getSessionName() {
    return this.sessionName;
  }
  
  @Generated
  public Supplier<String> getWebIdentityTokenSupplier() {
    return this.webIdentityTokenSupplier;
  }
  
  public CredentialsOverrider(Builder builder) {
    this.type = builder.type;
    this.sessionCredentials = builder.sessionCredentials;
    this.role = builder.role;
    this.durationSeconds = builder.durationSeconds;
    this.webIdentityTokenSupplier = builder.webIdentityTokenSupplier;
    this.sessionName = builder.sessionName;
  }
  
  public static class Builder {
    private final CredentialsType type;
    
    private StsCredentials sessionCredentials;
    
    private String role;
    
    private Integer durationSeconds;
    
    protected String sessionName;
    
    protected Supplier<String> webIdentityTokenSupplier;
    
    public Builder(CredentialsType type) {
      this.type = type;
    }
    
    public Builder withSessionCredentials(StsCredentials sessionCredentials) {
      this.sessionCredentials = sessionCredentials;
      return this;
    }
    
    public Builder withRole(String role) {
      this.role = role;
      return this;
    }
    
    public Builder withDurationSeconds(Integer durationSeconds) {
      this.durationSeconds = durationSeconds;
      return this;
    }
    
    public Builder withWebIdentityTokenSupplier(Supplier<String> tokenSupplier) {
      this.webIdentityTokenSupplier = tokenSupplier;
      return this;
    }
    
    public Builder withSessionName(String sessionName) {
      this.sessionName = sessionName;
      return this;
    }
    
    public CredentialsOverrider build() {
      return new CredentialsOverrider(this);
    }
  }
}
