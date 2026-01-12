package com.salesforce.multicloudj.sts.driver;

import com.salesforce.multicloudj.common.provider.Provider;
import com.salesforce.multicloudj.sts.model.AssumeRoleWebIdentityRequest;
import com.salesforce.multicloudj.sts.model.AssumedRoleRequest;
import com.salesforce.multicloudj.sts.model.CallerIdentity;
import com.salesforce.multicloudj.sts.model.GetAccessTokenRequest;
import com.salesforce.multicloudj.sts.model.GetCallerIdentityRequest;
import com.salesforce.multicloudj.sts.model.StsCredentials;
import java.net.URI;
import lombok.Generated;

public abstract class AbstractSts implements Provider {
  protected final String providerId;
  
  protected final String region;
  
  public AbstractSts(Builder<?, ?> builder) {
    this(builder.providerId, builder.region);
  }
  
  public AbstractSts(String providerId, String region) {
    this.providerId = providerId;
    this.region = region;
  }
  
  public String getProviderId() {
    return this.providerId;
  }
  
  public StsCredentials assumeRole(AssumedRoleRequest request) {
    return getSTSCredentialsWithAssumeRole(request);
  }
  
  public CallerIdentity getCallerIdentity(GetCallerIdentityRequest request) {
    return getCallerIdentityFromProvider(request);
  }
  
  public StsCredentials getAccessToken(GetAccessTokenRequest request) {
    return getAccessTokenFromProvider(request);
  }
  
  public StsCredentials assumeRoleWithWebIdentity(AssumeRoleWebIdentityRequest request) {
    return getSTSCredentialsWithAssumeRoleWebIdentity(request);
  }
  
  protected abstract StsCredentials getSTSCredentialsWithAssumeRole(AssumedRoleRequest paramAssumedRoleRequest);
  
  protected abstract CallerIdentity getCallerIdentityFromProvider(GetCallerIdentityRequest paramGetCallerIdentityRequest);
  
  protected abstract StsCredentials getAccessTokenFromProvider(GetAccessTokenRequest paramGetAccessTokenRequest);
  
  protected abstract StsCredentials getSTSCredentialsWithAssumeRoleWebIdentity(AssumeRoleWebIdentityRequest paramAssumeRoleWebIdentityRequest);
  
  public static abstract class Builder<A extends AbstractSts, T extends Builder<A, T>> implements Provider.Builder {
    protected String region;
    
    protected URI endpoint;
    
    protected String providerId;
    
    @Generated
    public String getRegion() {
      return this.region;
    }
    
    @Generated
    public URI getEndpoint() {
      return this.endpoint;
    }
    
    public T withRegion(String region) {
      this.region = region;
      return self();
    }
    
    public T withEndpoint(URI endpoint) {
      this.endpoint = endpoint;
      return self();
    }
    
    public T providerId(String providerId) {
      this.providerId = providerId;
      return self();
    }
    
    public abstract T self();
    
    public abstract A build();
  }
}
