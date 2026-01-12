package com.salesforce.multicloudj.pubsub.client;

public class GetAttributeResult {
  private String name;
  
  private String topic;
  
  public static class Builder {
    private final GetAttributeResult result = new GetAttributeResult();
    
    public Builder name(String name) {
      this.result.name = name;
      return this;
    }
    
    public Builder topic(String topic) {
      this.result.topic = topic;
      return this;
    }
    
    public GetAttributeResult build() {
      return this.result;
    }
  }
  
  public String getName() {
    return this.name;
  }
  
  public String getTopic() {
    return this.topic;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public void setTopic(String topic) {
    this.topic = topic;
  }
}
