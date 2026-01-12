package com.salesforce.multicloudj.common.exceptions;

public class TransactionFailedException extends SubstrateSdkException {
  public TransactionFailedException() {}
  
  public TransactionFailedException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public TransactionFailedException(String message) {
    super(message);
  }
  
  public TransactionFailedException(Throwable cause) {
    super(cause);
  }
}
