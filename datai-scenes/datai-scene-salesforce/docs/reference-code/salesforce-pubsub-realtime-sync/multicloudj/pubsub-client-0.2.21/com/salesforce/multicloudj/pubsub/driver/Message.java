package com.salesforce.multicloudj.pubsub.driver;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Message {
  private String loggableID;
  
  private byte[] body;
  
  private Map<String, String> metadata;
  
  private AckID ackID;
  
  private Function<Object, Boolean> asFunc;
  
  private Function<Function<Object, Boolean>, Exception> beforeSend;
  
  private Function<Function<Object, Boolean>, Exception> afterSend;
  
  public String getLoggableID() {
    return this.loggableID;
  }
  
  public byte[] getBody() {
    return this.body;
  }
  
  public Map<String, String> getMetadata() {
    return this.metadata;
  }
  
  public AckID getAckID() {
    return this.ackID;
  }
  
  public boolean as(Object target) {
    return (this.asFunc != null && ((Boolean)this.asFunc.apply(target)).booleanValue());
  }
  
  public static class Builder {
    private final Message message = new Message();
    
    public Builder withBody(byte[] body) {
      this.message.body = body;
      return this;
    }
    
    public Builder withBody(String body) {
      this.message.body = body.getBytes();
      return this;
    }
    
    public Builder withMetadata(Map<String, String> metadata) {
      this.message.metadata = metadata;
      return this;
    }
    
    public Builder withMetadata(String key, String value) {
      if (this.message.metadata == null)
        this.message.metadata = new HashMap<>(); 
      this.message.metadata.put(key, value);
      return this;
    }
    
    public Builder withLoggableID(String loggableID) {
      this.message.loggableID = loggableID;
      return this;
    }
    
    public Builder withAckID(AckID ackID) {
      this.message.ackID = ackID;
      return this;
    }
    
    public Builder withAsFunc(Function<Object, Boolean> asFunc) {
      this.message.asFunc = asFunc;
      return this;
    }
    
    public Builder withBeforeSend(Function<Function<Object, Boolean>, Exception> beforeSend) {
      this.message.beforeSend = beforeSend;
      return this;
    }
    
    public Builder withAfterSend(Function<Function<Object, Boolean>, Exception> afterSend) {
      this.message.afterSend = afterSend;
      return this;
    }
    
    public Message build() {
      return this.message;
    }
  }
  
  public static Builder builder() {
    return new Builder();
  }
}
