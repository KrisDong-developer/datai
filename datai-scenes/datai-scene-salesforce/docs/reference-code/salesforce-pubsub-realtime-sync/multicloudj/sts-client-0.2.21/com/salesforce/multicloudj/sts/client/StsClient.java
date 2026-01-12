package com.salesforce.multicloudj.sts.client;

import com.google.common.collect.ImmutableSet;
import com.salesforce.multicloudj.common.exceptions.ExceptionHandler;
import com.salesforce.multicloudj.common.exceptions.SubstrateSdkException;
import com.salesforce.multicloudj.sts.driver.AbstractSts;
import com.salesforce.multicloudj.sts.model.AssumeRoleWebIdentityRequest;
import com.salesforce.multicloudj.sts.model.AssumedRoleRequest;
import com.salesforce.multicloudj.sts.model.CallerIdentity;
import com.salesforce.multicloudj.sts.model.GetAccessTokenRequest;
import com.salesforce.multicloudj.sts.model.GetCallerIdentityRequest;
import com.salesforce.multicloudj.sts.model.StsCredentials;
import java.net.URI;
import java.util.ServiceLoader;

public class StsClient {
  protected AbstractSts sts;
  
  protected StsClient(AbstractSts sts) {
    this.sts = sts;
  }
  
  public static StsBuilder builder(String providerId) {
    return new StsBuilder(providerId);
  }
  
  private static Iterable<AbstractSts> all() {
    ServiceLoader<AbstractSts> services = ServiceLoader.load(AbstractSts.class);
    ImmutableSet.Builder<AbstractSts> builder = ImmutableSet.builder();
    for (AbstractSts service : services)
      builder.add(service); 
    return (Iterable<AbstractSts>)builder.build();
  }
  
  private static AbstractSts.Builder<?, ?> findProviderBuilder(String providerId) {
    for (AbstractSts provider : all()) {
      if (provider.getProviderId().equals(providerId))
        return createBuilderInstance(provider); 
    } 
    throw new IllegalArgumentException("No cloud storage provider found for providerId: " + providerId);
  }
  
  private static AbstractSts.Builder<?, ?> createBuilderInstance(AbstractSts provider) {
    try {
      return (AbstractSts.Builder<?, ?>)provider.getClass().getMethod("builder", new Class[0]).invoke(provider, new Object[0]);
    } catch (Exception e) {
      throw new RuntimeException("Failed to create builder for provider: " + provider.getClass().getName(), e);
    } 
  }
  
  public StsCredentials getAssumeRoleCredentials(AssumedRoleRequest request) {
    try {
      return this.sts.assumeRole(request);
    } catch (Throwable t) {
      Class<? extends SubstrateSdkException> exception = this.sts.getException(t);
      ExceptionHandler.handleAndPropagate(exception, t);
      return null;
    } 
  }
  
  public CallerIdentity getCallerIdentity() {
    try {
      return getCallerIdentity(GetCallerIdentityRequest.builder().build());
    } catch (Throwable t) {
      Class<? extends SubstrateSdkException> exception = this.sts.getException(t);
      ExceptionHandler.handleAndPropagate(exception, t);
      return null;
    } 
  }
  
  public CallerIdentity getCallerIdentity(GetCallerIdentityRequest request) {
    try {
      return this.sts.getCallerIdentity(request);
    } catch (Throwable t) {
      Class<? extends SubstrateSdkException> exception = this.sts.getException(t);
      ExceptionHandler.handleAndPropagate(exception, t);
      return null;
    } 
  }
  
  public StsCredentials getAccessToken(GetAccessTokenRequest request) {
    try {
      return this.sts.getAccessToken(request);
    } catch (Throwable t) {
      Class<? extends SubstrateSdkException> exception = this.sts.getException(t);
      ExceptionHandler.handleAndPropagate(exception, t);
      return null;
    } 
  }
  
  public StsCredentials getAssumeRoleWithWebIdentityCredentials(AssumeRoleWebIdentityRequest request) {
    try {
      return this.sts.assumeRoleWithWebIdentity(request);
    } catch (Throwable t) {
      Class<? extends SubstrateSdkException> exception = this.sts.getException(t);
      ExceptionHandler.handleAndPropagate(exception, t);
      return null;
    } 
  }
  
  public static class StsBuilder {
    protected String region;
    
    protected URI endpoint;
    
    protected AbstractSts sts;
    
    protected AbstractSts.Builder<?, ?> stsBuilder;
    
    public StsBuilder(String providerId) {
      this.stsBuilder = StsClient.findProviderBuilder(providerId);
    }
    
    public StsBuilder withRegion(String region) {
      this.region = region;
      this.stsBuilder.withRegion(region);
      return this;
    }
    
    public StsBuilder withEndpoint(URI endpoint) {
      this.endpoint = endpoint;
      this.stsBuilder.withEndpoint(endpoint);
      return this;
    }
    
    public StsClient build() {
      this.sts = this.stsBuilder.build();
      return new StsClient(this.sts);
    }
  }
}
