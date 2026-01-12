package com.salesforce.multicloudj.common.provider;

import java.util.Properties;

public interface SdkProvider<T extends com.salesforce.multicloudj.common.service.SdkService> {
  String getProviderId();
  
  Builder<T> builder();
  
  public static interface Builder<T extends com.salesforce.multicloudj.common.service.SdkService> {
    Builder<T> providerId(String param1String);
    
    String getProviderId();
    
    Builder<T> withProperties(Properties param1Properties);
    
    Properties getProperties();
    
    T build();
  }
}
