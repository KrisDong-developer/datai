package com.salesforce.multicloudj.sts.driver;

import com.salesforce.multicloudj.common.provider.Provider;
import com.salesforce.multicloudj.sts.model.CredentialsOverrider;
import com.salesforce.multicloudj.sts.model.SignedAuthRequest;
import java.net.http.HttpRequest;

public abstract class AbstractStsUtilities<T extends AbstractStsUtilities<T>> implements Provider {
  protected final String providerId;
  
  protected final String region;
  
  protected final CredentialsOverrider credentialsOverrider;
  
  public AbstractStsUtilities(Builder<T> builder) {
    this.providerId = builder.providerId;
    this.region = builder.region;
    this.credentialsOverrider = builder.credentialsOverrider;
  }
  
  public String getProviderId() {
    return this.providerId;
  }
  
  public SignedAuthRequest cloudNativeAuthSignedRequest(HttpRequest request) {
    return newCloudNativeAuthSignedRequest(request);
  }
  
  protected abstract SignedAuthRequest newCloudNativeAuthSignedRequest(HttpRequest paramHttpRequest);
  
  public static abstract class Builder<T extends AbstractStsUtilities<T>> implements Provider.Builder {
    protected String providerId;
    
    protected String region;
    
    protected CredentialsOverrider credentialsOverrider;
    
    public Builder withRegion(String region) {
      this.region = region;
      return this;
    }
    
    public Builder withCredentialsOverrider(CredentialsOverrider credentialsOverrider) {
      this.credentialsOverrider = credentialsOverrider;
      return this;
    }
    
    public String getProviderId() {
      return this.providerId;
    }
    
    public Builder<T> providerId(String providerId) {
      this.providerId = providerId;
      return this;
    }
    
    public abstract T build();
  }
}
