package com.salesforce.multicloudj.pubsub.client;

import com.salesforce.multicloudj.pubsub.driver.AbstractSubscription;
import com.salesforce.multicloudj.pubsub.driver.AbstractTopic;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public class ProviderSupplier {
  static AbstractTopic.Builder<?> findTopicProviderBuilder(String providerId) {
    ServiceLoader<AbstractTopic> services = ServiceLoader.load(AbstractTopic.class);
    List<AbstractTopic> all = new ArrayList<>();
    for (AbstractTopic service : services)
      all.add(service); 
    for (AbstractTopic<?> provider : all) {
      if (provider.getProviderId().equals(providerId))
        return createTopicBuilderInstance(provider); 
    } 
    throw new IllegalArgumentException("No Topic provider found for providerId: " + providerId);
  }
  
  static AbstractSubscription.Builder<?> findSubscriptionProviderBuilder(String providerId) {
    ServiceLoader<AbstractSubscription> services = ServiceLoader.load(AbstractSubscription.class);
    List<AbstractSubscription> all = new ArrayList<>();
    for (AbstractSubscription service : services)
      all.add(service); 
    for (AbstractSubscription<?> provider : all) {
      if (provider.getProviderId().equals(providerId))
        return createSubscriptionBuilderInstance(provider); 
    } 
    throw new IllegalArgumentException("No Subscription provider found for providerId: " + providerId);
  }
  
  private static AbstractTopic.Builder<?> createTopicBuilderInstance(AbstractTopic<?> provider) {
    try {
      return (AbstractTopic.Builder)provider.getClass().getMethod("builder", new Class[0]).invoke(provider, new Object[0]);
    } catch (Exception e) {
      throw new RuntimeException("Failed to create topic builder for provider: " + provider.getClass().getName(), e);
    } 
  }
  
  private static AbstractSubscription.Builder<?> createSubscriptionBuilderInstance(AbstractSubscription<?> provider) {
    try {
      return (AbstractSubscription.Builder)provider.getClass().getMethod("builder", new Class[0]).invoke(provider, new Object[0]);
    } catch (Exception e) {
      throw new RuntimeException("Failed to create subscription builder for provider: " + provider.getClass().getName(), e);
    } 
  }
}
