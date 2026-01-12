package com.salesforce.multicloudj.pubsub.client;

import com.salesforce.multicloudj.common.exceptions.ExceptionHandler;
import com.salesforce.multicloudj.common.exceptions.SubstrateSdkException;
import com.salesforce.multicloudj.pubsub.driver.AbstractTopic;
import com.salesforce.multicloudj.pubsub.driver.Message;
import com.salesforce.multicloudj.sts.model.CredentialsOverrider;
import java.net.URI;

public class TopicClient implements AutoCloseable {
  protected AbstractTopic<?> topic;
  
  protected TopicClient(AbstractTopic<?> topic) {
    this.topic = topic;
  }
  
  public static TopicClientBuilder builder(String providerId) {
    return new TopicClientBuilder(providerId);
  }
  
  public void send(Message message) {
    try {
      this.topic.send(message);
    } catch (Throwable t) {
      Class<? extends SubstrateSdkException> exception = this.topic.getException(t);
      ExceptionHandler.handleAndPropagate(exception, t);
    } 
  }
  
  public void close() throws Exception {
    try {
      this.topic.close();
    } catch (Throwable t) {
      Class<? extends SubstrateSdkException> exception = this.topic.getException(t);
      ExceptionHandler.handleAndPropagate(exception, t);
    } 
  }
  
  public static class TopicClientBuilder {
    private final AbstractTopic.Builder<?> topicBuilder;
    
    public TopicClientBuilder(String providerId) {
      this.topicBuilder = ProviderSupplier.findTopicProviderBuilder(providerId);
    }
    
    public TopicClientBuilder withTopicName(String topicName) {
      this.topicBuilder.withTopicName(topicName);
      return this;
    }
    
    public TopicClientBuilder withRegion(String region) {
      this.topicBuilder.withRegion(region);
      return this;
    }
    
    public TopicClientBuilder withEndpoint(URI endpoint) {
      this.topicBuilder.withEndpoint(endpoint);
      return this;
    }
    
    public TopicClientBuilder withProxyEndpoint(URI proxyEndpoint) {
      this.topicBuilder.withProxyEndpoint(proxyEndpoint);
      return this;
    }
    
    public TopicClientBuilder withCredentialsOverrider(CredentialsOverrider credentialsOverrider) {
      this.topicBuilder.withCredentialsOverrider(credentialsOverrider);
      return this;
    }
    
    public TopicClient build() {
      return new TopicClient(this.topicBuilder.build());
    }
  }
}
