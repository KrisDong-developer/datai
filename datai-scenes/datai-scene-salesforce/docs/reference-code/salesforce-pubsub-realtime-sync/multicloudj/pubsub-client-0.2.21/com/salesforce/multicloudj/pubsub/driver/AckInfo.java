package com.salesforce.multicloudj.pubsub.driver;

public class AckInfo {
  private final AckID ackID;
  
  private final boolean isAck;
  
  public AckInfo(AckID ackID, boolean isAck) {
    this.ackID = ackID;
    this.isAck = isAck;
  }
  
  public AckID getAckID() {
    return this.ackID;
  }
  
  public boolean isAck() {
    return this.isAck;
  }
}
