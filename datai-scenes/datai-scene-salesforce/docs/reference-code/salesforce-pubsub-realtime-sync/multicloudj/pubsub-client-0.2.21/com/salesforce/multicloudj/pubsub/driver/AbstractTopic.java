package com.salesforce.multicloudj.pubsub.driver;

import com.salesforce.multicloudj.common.exceptions.FailedPreconditionException;
import com.salesforce.multicloudj.common.exceptions.SubstrateSdkException;
import com.salesforce.multicloudj.common.provider.Provider;
import com.salesforce.multicloudj.pubsub.batcher.Batcher;
import com.salesforce.multicloudj.pubsub.driver.utils.MessageUtils;
import com.salesforce.multicloudj.sts.model.CredentialsOverrider;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractTopic<T extends AbstractTopic<T>> implements AutoCloseable, Provider {
  protected final String providerId;
  
  protected final String topicName;
  
  protected final String region;
  
  protected final URI endpoint;
  
  protected final URI proxyEndpoint;
  
  protected final CredentialsOverrider credentialsOverrider;
  
  protected final Batcher<Message> batcher;
  
  protected final AtomicBoolean isShutdown = new AtomicBoolean(false);
  
  protected AbstractTopic(String providerId, String topicName, String region, URI endpoint, URI proxyEndpoint, CredentialsOverrider credentialsOverrider) {
    this.providerId = providerId;
    this.topicName = topicName;
    this.region = region;
    this.endpoint = endpoint;
    this.proxyEndpoint = proxyEndpoint;
    this.credentialsOverrider = credentialsOverrider;
    this.batcher = new Batcher(createBatcherOptions(), this::handleBatch);
  }
  
  protected AbstractTopic(Builder<T> builder) {
    this.providerId = builder.providerId;
    this.topicName = builder.topicName;
    this.region = builder.region;
    this.endpoint = builder.endpoint;
    this.proxyEndpoint = builder.proxyEndpoint;
    this.credentialsOverrider = builder.credentialsOverrider;
    this.batcher = new Batcher(createBatcherOptions(), this::handleBatch);
  }
  
  public String getProviderId() {
    return this.providerId;
  }
  
  protected Batcher.Options createBatcherOptions() {
    return (new Batcher.Options())
      .setMaxHandlers(1)
      .setMinBatchSize(1)
      .setMaxBatchSize(0)
      .setMaxBatchByteSize(0);
  }
  
  private Void handleBatch(List<Message> messages) {
    if (this.isShutdown.get())
      throw new FailedPreconditionException("Topic has been shut down"); 
    executeBeforeSendBatchHooks(messages);
    try {
      doSendBatch(messages);
    } finally {
      executeAfterSendBatchHooks(messages);
    } 
    return null;
  }
  
  public final void send(Message message) {
    if (this.isShutdown.get())
      throw new FailedPreconditionException("Topic has been shut down"); 
    MessageUtils.validateMessage(message);
    sendBatch(List.of(message));
  }
  
  private final void sendBatch(List<Message> messages) {
    if (this.isShutdown.get())
      throw new FailedPreconditionException("Topic has been shut down"); 
    MessageUtils.validateMessageBatch(messages);
    if (messages.isEmpty())
      return; 
    try {
      List<CompletableFuture<Void>> futures = new ArrayList<>();
      for (Message message : messages)
        futures.add(this.batcher.addNoWait(message)); 
      CompletableFuture.allOf((CompletableFuture<?>[])futures.<CompletableFuture>toArray(new CompletableFuture[0])).get();
    } catch (ExecutionException e) {
      if (e.getCause() instanceof RuntimeException)
        throw (RuntimeException)e.getCause(); 
      if (e.getCause() != null)
        throw new SubstrateSdkException("Error sending batch of messages", e.getCause()); 
      throw new SubstrateSdkException("Error sending batch of messages", e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new SubstrateSdkException("Interrupted while waiting for pubsub batch send to complete", e);
    } 
  }
  
  protected abstract void doSendBatch(List<Message> paramList);
  
  public abstract Class<? extends SubstrateSdkException> getException(Throwable paramThrowable);
  
  protected void executeBeforeSendBatchHooks(List<Message> messages) {}
  
  protected void executeAfterSendBatchHooks(List<Message> messages) {}
  
  public void close() throws Exception {
    this.isShutdown.set(true);
    if (this.batcher != null)
      this.batcher.shutdownAndDrain(); 
  }
  
  public static abstract class Builder<T extends AbstractTopic<T>> implements Provider.Builder {
    protected String providerId;
    
    protected String topicName;
    
    protected String region;
    
    protected URI endpoint;
    
    protected URI proxyEndpoint;
    
    protected CredentialsOverrider credentialsOverrider;
    
    public Builder<T> providerId(String providerId) {
      this.providerId = providerId;
      return this;
    }
    
    public Builder<T> withTopicName(String topicName) {
      this.topicName = topicName;
      return this;
    }
    
    public Builder<T> withRegion(String region) {
      this.region = region;
      return this;
    }
    
    public Builder<T> withEndpoint(URI endpoint) {
      this.endpoint = endpoint;
      return this;
    }
    
    public Builder<T> withProxyEndpoint(URI proxyEndpoint) {
      this.proxyEndpoint = proxyEndpoint;
      return this;
    }
    
    public Builder<T> withCredentialsOverrider(CredentialsOverrider credentialsOverrider) {
      this.credentialsOverrider = credentialsOverrider;
      return this;
    }
    
    public abstract T build();
  }
}
