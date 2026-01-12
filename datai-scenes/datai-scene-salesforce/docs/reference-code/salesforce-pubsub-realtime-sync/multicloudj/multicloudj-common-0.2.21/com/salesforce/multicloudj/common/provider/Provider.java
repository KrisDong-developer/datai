package com.salesforce.multicloudj.common.provider;

import com.salesforce.multicloudj.common.exceptions.SubstrateSdkException;

public interface Provider {
  String getProviderId();
  
  Builder builder();
  
  Class<? extends SubstrateSdkException> getException(Throwable paramThrowable);
  
  public static interface Builder {
    Builder providerId(String param1String);
  }
}
