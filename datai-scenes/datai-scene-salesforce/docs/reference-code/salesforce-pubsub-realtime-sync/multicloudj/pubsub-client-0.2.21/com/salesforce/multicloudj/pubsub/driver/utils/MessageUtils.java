package com.salesforce.multicloudj.pubsub.driver.utils;

import com.salesforce.multicloudj.common.exceptions.InvalidArgumentException;
import com.salesforce.multicloudj.pubsub.driver.Message;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public final class MessageUtils {
  public static void validateMessage(Message message) {
    if (message == null)
      throw new InvalidArgumentException("Message cannot be null"); 
    if (message.getBody() == null)
      throw new InvalidArgumentException("Message body cannot be null"); 
    if (message.getLoggableID() != null)
      throw new InvalidArgumentException("LoggableID cannot be set on outgoing messages. This field is reserved for received messages and is set internally by drivers."); 
    if (message.getMetadata() != null)
      validateMetadata(message.getMetadata()); 
  }
  
  public static void validateMessageBatch(List<Message> messages) {
    if (messages == null)
      throw new InvalidArgumentException("Messages list cannot be null"); 
    for (int i = 0; i < messages.size(); i++) {
      Message message = messages.get(i);
      if (message == null)
        throw new InvalidArgumentException("Message at index " + i + " cannot be null"); 
      validateMessage(message);
    } 
  }
  
  public static int calculateByteSize(Message message) {
    if (message == null)
      return 0; 
    if (message.getBody() != null)
      return (message.getBody()).length; 
    return 0;
  }
  
  private static void validateMetadata(Map<String, String> metadata) {
    for (Map.Entry<String, String> entry : metadata.entrySet()) {
      String key = entry.getKey();
      String value = entry.getValue();
      if (key == null || key.trim().isEmpty())
        throw new InvalidArgumentException("Metadata keys cannot be null or empty"); 
      if (!isValidUtf8String(key))
        throw new InvalidArgumentException("Metadata key is not valid UTF-8: " + key); 
      if (value != null && !isValidUtf8String(value))
        throw new InvalidArgumentException("Metadata value is not valid UTF-8 for key: " + key); 
    } 
  }
  
  private static boolean isValidUtf8String(String str) {
    if (str == null)
      return true; 
    try {
      byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
      String decoded = new String(bytes, StandardCharsets.UTF_8);
      return str.equals(decoded);
    } catch (Exception e) {
      return false;
    } 
  }
}
