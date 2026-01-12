package com.salesforce.multicloudj.pubsub.client;

import com.salesforce.multicloudj.common.exceptions.ExceptionHandler;
import com.salesforce.multicloudj.common.exceptions.SubstrateSdkException;
import com.salesforce.multicloudj.pubsub.driver.AbstractSubscription;
import com.salesforce.multicloudj.pubsub.driver.AckID;
import com.salesforce.multicloudj.pubsub.driver.Message;
import com.salesforce.multicloudj.sts.model.CredentialsOverrider;
import java.net.URI;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SubscriptionClient implements AutoCloseable {
  protected AbstractSubscription<?> subscription;
  
  protected SubscriptionClient(AbstractSubscription<?> subscription) {
    this.subscription = subscription;
  }
  
  public static SubscriptionClientBuilder builder(String providerId) {
    return new SubscriptionClientBuilder(providerId);
  }
  
  public Message receive() {
    try {
      return this.subscription.receive();
    } catch (Throwable t) {
      Class<? extends SubstrateSdkException> exception = this.subscription.getException(t);
      ExceptionHandler.handleAndPropagate(exception, t);
      return null;
    } 
  }
  
  public void sendAck(AckID ackID) {
    try {
      this.subscription.sendAck(ackID);
    } catch (Throwable t) {
      Class<? extends SubstrateSdkException> exception = this.subscription.getException(t);
      ExceptionHandler.handleAndPropagate(exception, t);
    } 
  }
  
  public CompletableFuture<Void> sendAcks(List<AckID> ackIDs) {
    try {
      return this.subscription.sendAcks(ackIDs);
    } catch (Throwable t) {
      Class<? extends SubstrateSdkException> exception = this.subscription.getException(t);
      ExceptionHandler.handleAndPropagate(exception, t);
      return CompletableFuture.failedFuture(t);
    } 
  }
  
  public void sendNack(AckID ackID) {
    try {
      this.subscription.sendNack(ackID);
    } catch (Throwable t) {
      Class<? extends SubstrateSdkException> exception = this.subscription.getException(t);
      ExceptionHandler.handleAndPropagate(exception, t);
    } 
  }
  
  public CompletableFuture<Void> sendNacks(List<AckID> ackIDs) {
    try {
      return this.subscription.sendNacks(ackIDs);
    } catch (Throwable t) {
      Class<? extends SubstrateSdkException> exception = this.subscription.getException(t);
      ExceptionHandler.handleAndPropagate(exception, t);
      return CompletableFuture.failedFuture(t);
    } 
  }
  
  public boolean canNack() {
    return this.subscription.canNack();
  }
  
  public GetAttributeResult getAttributes() {
    try {
      return this.subscription.getAttributes();
    } catch (Throwable t) {
      Class<? extends SubstrateSdkException> exception = this.subscription.getException(t);
      ExceptionHandler.handleAndPropagate(exception, t);
      return null;
    } 
  }
  
  public boolean isRetryable(Throwable error) {
    return this.subscription.isRetryable(error);
  }
  
  public void close() throws Exception {
    try {
      this.subscription.close();
    } catch (Throwable t) {
      Class<? extends SubstrateSdkException> exception = this.subscription.getException(t);
      ExceptionHandler.handleAndPropagate(exception, t);
    } 
  }
  
  public static class SubscriptionClientBuilder {
    private final AbstractSubscription.Builder<?> subscriptionBuilder;
    
    public SubscriptionClientBuilder(String providerId) {
      this.subscriptionBuilder = ProviderSupplier.findSubscriptionProviderBuilder(providerId);
    }
    
    public SubscriptionClientBuilder withSubscriptionName(String subscriptionName) {
      this.subscriptionBuilder.withSubscriptionName(subscriptionName);
      return this;
    }
    
    public SubscriptionClientBuilder withRegion(String region) {
      this.subscriptionBuilder.withRegion(region);
      return this;
    }
    
    public SubscriptionClientBuilder withEndpoint(URI endpoint) {
      this.subscriptionBuilder.withEndpoint(endpoint);
      return this;
    }
    
    public SubscriptionClientBuilder withProxyEndpoint(URI proxyEndpoint) {
      this.subscriptionBuilder.withProxyEndpoint(proxyEndpoint);
      return this;
    }
    
    public SubscriptionClientBuilder withCredentialsOverrider(CredentialsOverrider credentialsOverrider) {
      this.subscriptionBuilder.withCredentialsOverrider(credentialsOverrider);
      return this;
    }
    
    public SubscriptionClient build() {
      return new SubscriptionClient(this.subscriptionBuilder.build());
    }
  }
}
