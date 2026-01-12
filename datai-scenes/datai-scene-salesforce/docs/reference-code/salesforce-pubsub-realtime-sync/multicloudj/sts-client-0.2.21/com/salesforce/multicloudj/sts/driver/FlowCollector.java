package com.salesforce.multicloudj.sts.driver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

public class FlowCollector<T> implements Flow.Subscriber<T> {
  private final List<T> buffer = new ArrayList<>();
  
  private final CompletableFuture<List<T>> future = new CompletableFuture<>();
  
  public CompletableFuture<List<T>> items() {
    return this.future;
  }
  
  public void onComplete() {
    this.future.complete(this.buffer);
  }
  
  public void onError(Throwable throwable) {
    this.future.completeExceptionally(throwable);
  }
  
  public void onNext(T item) {
    this.buffer.add(item);
  }
  
  public void onSubscribe(Flow.Subscription subscription) {
    subscription.request(Long.MAX_VALUE);
  }
}
