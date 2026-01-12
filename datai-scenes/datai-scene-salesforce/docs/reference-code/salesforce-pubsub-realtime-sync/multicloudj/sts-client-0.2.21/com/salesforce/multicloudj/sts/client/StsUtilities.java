package com.salesforce.multicloudj.sts.client;

import com.google.common.collect.ImmutableSet;
import com.salesforce.multicloudj.common.exceptions.ExceptionHandler;
import com.salesforce.multicloudj.common.exceptions.SubstrateSdkException;
import com.salesforce.multicloudj.sts.driver.AbstractStsUtilities;
import com.salesforce.multicloudj.sts.model.CredentialsOverrider;
import com.salesforce.multicloudj.sts.model.SignedAuthRequest;
import java.net.http.HttpRequest;
import java.util.ServiceLoader;

public class StsUtilities {
  protected AbstractStsUtilities<?> stsUtility;
  
  protected StsUtilities(StsUtilityBuilder builder) {
    this.stsUtility = builder.stsUtility;
  }
  
  public static StsUtilityBuilder builder(String providerId) {
    return new StsUtilityBuilder(providerId);
  }
  
  private static Iterable<AbstractStsUtilities<?>> all() {
    ServiceLoader<AbstractStsUtilities> services = ServiceLoader.load(AbstractStsUtilities.class);
    ImmutableSet.Builder<AbstractStsUtilities<?>> builder = ImmutableSet.builder();
    for (AbstractStsUtilities<?> service : services)
      builder.add(service); 
    return (Iterable<AbstractStsUtilities<?>>)builder.build();
  }
  
  private static AbstractStsUtilities.Builder<?> findProviderBuilder(String providerId) {
    for (AbstractStsUtilities<?> provider : all()) {
      if (provider.getProviderId().equals(providerId))
        return createBuilderInstance(provider); 
    } 
    throw new IllegalArgumentException("No cloud storage provider found for providerId: " + providerId);
  }
  
  private static AbstractStsUtilities.Builder<?> createBuilderInstance(AbstractStsUtilities<?> provider) {
    try {
      return (AbstractStsUtilities.Builder)provider.getClass().getMethod("builder", new Class[0]).invoke(provider, new Object[0]);
    } catch (Exception e) {
      throw new RuntimeException("Failed to create builder for provider: " + provider.getClass().getName(), e);
    } 
  }
  
  public SignedAuthRequest newCloudNativeAuthSignedRequest(HttpRequest request) {
    try {
      return this.stsUtility.cloudNativeAuthSignedRequest(request);
    } catch (Throwable t) {
      Class<? extends SubstrateSdkException> exception = this.stsUtility.getException(t);
      ExceptionHandler.handleAndPropagate(exception, t);
      return null;
    } 
  }
  
  public static class StsUtilityBuilder {
    protected AbstractStsUtilities<?> stsUtility;
    
    protected AbstractStsUtilities.Builder<?> builder;
    
    public StsUtilityBuilder(String providerId) {
      this.builder = StsUtilities.findProviderBuilder(providerId);
    }
    
    public StsUtilityBuilder withRegion(String region) {
      this.builder.withRegion(region);
      return this;
    }
    
    public StsUtilityBuilder withCredentialsOverrider(CredentialsOverrider credentialsOverrider) {
      this.builder.withCredentialsOverrider(credentialsOverrider);
      return this;
    }
    
    public StsUtilities build() {
      this.stsUtility = this.builder.build();
      return new StsUtilities(this);
    }
  }
}
