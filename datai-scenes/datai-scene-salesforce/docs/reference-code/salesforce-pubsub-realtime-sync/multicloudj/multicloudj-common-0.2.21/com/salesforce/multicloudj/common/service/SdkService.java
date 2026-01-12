package com.salesforce.multicloudj.common.service;

import com.salesforce.multicloudj.common.exceptions.SubstrateSdkException;

public interface SdkService {
  String getProviderId();
  
  Class<? extends SubstrateSdkException> getException(Throwable paramThrowable);
}
