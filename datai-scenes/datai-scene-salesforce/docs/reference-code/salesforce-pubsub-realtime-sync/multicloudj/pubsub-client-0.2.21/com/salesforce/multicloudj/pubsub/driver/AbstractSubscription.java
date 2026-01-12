package com.salesforce.multicloudj.pubsub.driver;

import com.salesforce.multicloudj.common.exceptions.FailedPreconditionException;
import com.salesforce.multicloudj.common.exceptions.InvalidArgumentException;
import com.salesforce.multicloudj.common.exceptions.SubstrateSdkException;
import com.salesforce.multicloudj.common.provider.Provider;
import com.salesforce.multicloudj.pubsub.batcher.Batcher;
import com.salesforce.multicloudj.pubsub.client.GetAttributeResult;
import com.salesforce.multicloudj.sts.model.CredentialsOverrider;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractSubscription<T extends AbstractSubscription<T>> implements AutoCloseable, Provider {
  protected final String providerId;
  
  protected final String subscriptionName;
  
  protected final String region;
  
  protected final URI endpoint;
  
  protected final URI proxyEndpoint;
  
  private final ExecutorService backgroundPool;
  
  private final Batcher.Options receiveBatcherOptions;
  
  protected final CredentialsOverrider credentialsOverrider;
  
  private static final class QueueConfig {
    static final Duration DESIRED_QUEUE_DURATION = Duration.ofSeconds(2L);
    
    static final double PREFETCH_RATIO = 0.5D;
    
    static final double DECAY = 0.5D;
    
    static final double MAX_GROWTH_FACTOR = 2.0D;
    
    static final double MAX_SHRINK_FACTOR = 0.75D;
    
    static final int MAX_BATCH_SIZE = 3000;
  }
  
  private final ReentrantLock lock = new ReentrantLock();
  
  private final Condition batchArrived = this.lock.newCondition();
  
  private final Queue<Message> queue = new ArrayDeque<>();
  
  private final AtomicBoolean prefetchInFlight = new AtomicBoolean(false);
  
  protected final AtomicBoolean isShutdown = new AtomicBoolean(false);
  
  private double runningBatchSize = 1.0D;
  
  private long throughputStart = 0L;
  
  private int throughputCount = 0;
  
  private final Batcher<AckInfo> ackBatcher;
  
  protected final AtomicReference<Throwable> permanentError = new AtomicReference<>(null);
  
  protected final AtomicReference<Throwable> unreportedAckErr = new AtomicReference<>(null);
  
  protected AbstractSubscription(String providerId, String subscriptionName, String region, CredentialsOverrider credentialsOverrider) {
    this.providerId = providerId;
    this.subscriptionName = subscriptionName;
    this.region = region;
    this.endpoint = null;
    this.proxyEndpoint = null;
    this.receiveBatcherOptions = createReceiveBatcherOptions();
    this.credentialsOverrider = credentialsOverrider;
    this.ackBatcher = new Batcher(createAckBatcherOptions(), this::handleAckAndNackBatch);
    int poolSize = Math.max(1, this.receiveBatcherOptions.getMaxHandlers());
    this.backgroundPool = Executors.newFixedThreadPool(poolSize, r -> {
          Thread t = new Thread(r);
          t.setName("subscription-" + this.subscriptionName + "-prefetch");
          return t;
        });
  }
  
  protected AbstractSubscription(Builder<T> builder) {
    this.providerId = builder.providerId;
    this.subscriptionName = builder.subscriptionName;
    this.region = builder.region;
    this.endpoint = builder.endpoint;
    this.proxyEndpoint = builder.proxyEndpoint;
    this.receiveBatcherOptions = createReceiveBatcherOptions();
    this.credentialsOverrider = builder.credentialsOverrider;
    this.ackBatcher = new Batcher(createAckBatcherOptions(), this::handleAckAndNackBatch);
    int poolSize = Math.max(1, this.receiveBatcherOptions.getMaxHandlers());
    this.backgroundPool = Executors.newFixedThreadPool(poolSize, r -> {
          Thread t = new Thread(r);
          t.setName("subscription-" + this.subscriptionName + "-prefetch");
          return t;
        });
  }
  
  public String getProviderId() {
    return this.providerId;
  }
  
  public Message receive() {
    this.lock.lock();
    try {
      while (true) {
        if (this.isShutdown.get())
          throw new FailedPreconditionException("Subscription has been shut down"); 
        if (this.permanentError.get() != null) {
          this.unreportedAckErr.set(null);
          throw new SubstrateSdkException("Subscription in permanent error state", (Throwable)this.permanentError.get());
        } 
        maybePrefetch();
        if (!this.queue.isEmpty()) {
          Message m = this.queue.poll();
          this.throughputCount++;
          return m;
        } 
        if (this.prefetchInFlight.get()) {
          try {
            this.batchArrived.await();
          } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new SubstrateSdkException("Interrupted while waiting for messages", ie);
          } 
          continue;
        } 
        try {
          if (!this.batchArrived.await(100L, TimeUnit.MILLISECONDS));
        } catch (InterruptedException ie) {
          Thread.currentThread().interrupt();
          throw new SubstrateSdkException("Interrupted while waiting for messages", ie);
        } 
      } 
    } finally {
      this.lock.unlock();
    } 
  }
  
  protected Batcher.Options createReceiveBatcherOptions() {
    return (new Batcher.Options())
      .setMaxHandlers(1)
      .setMinBatchSize(1)
      .setMaxBatchSize(3000)
      .setMaxBatchByteSize(0);
  }
  
  public void sendAck(AckID ackID) {
    if (this.isShutdown.get())
      throw new FailedPreconditionException("Subscription has been shut down"); 
    if (ackID == null) {
      InvalidArgumentException invalidArgumentException = new InvalidArgumentException("AckID cannot be null");
      this.permanentError.set(invalidArgumentException);
      throw invalidArgumentException;
    } 
    validateAckIDType(ackID);
    this.ackBatcher.addNoWait(new AckInfo(ackID, true));
  }
  
  public CompletableFuture<Void> sendAcks(List<AckID> ackIDs) {
    if (this.isShutdown.get())
      throw new FailedPreconditionException("Subscription has been shut down"); 
    if (ackIDs == null || ackIDs.isEmpty())
      return CompletableFuture.completedFuture(null); 
    for (AckID ackID : ackIDs) {
      if (ackID == null) {
        InvalidArgumentException invalidArgumentException = new InvalidArgumentException("AckID cannot be null in batch acknowledgment");
        this.permanentError.set(invalidArgumentException);
        throw invalidArgumentException;
      } 
      validateAckIDType(ackID);
    } 
    for (AckID ackID : ackIDs)
      this.ackBatcher.addNoWait(new AckInfo(ackID, true)); 
    return CompletableFuture.completedFuture(null);
  }
  
  public void sendNack(AckID ackID) {
    if (this.isShutdown.get())
      throw new FailedPreconditionException("Subscription has been shut down"); 
    if (ackID == null) {
      InvalidArgumentException invalidArgumentException = new InvalidArgumentException("AckID cannot be null");
      this.permanentError.set(invalidArgumentException);
      throw invalidArgumentException;
    } 
    validateAckIDType(ackID);
    this.ackBatcher.addNoWait(new AckInfo(ackID, false));
  }
  
  public CompletableFuture<Void> sendNacks(List<AckID> ackIDs) {
    if (this.isShutdown.get())
      throw new FailedPreconditionException("Subscription has been shut down"); 
    if (ackIDs == null) {
      InvalidArgumentException invalidArgumentException = new InvalidArgumentException("AckIDs list cannot be null");
      this.permanentError.set(invalidArgumentException);
      throw invalidArgumentException;
    } 
    if (ackIDs.isEmpty())
      return CompletableFuture.completedFuture(null); 
    for (AckID ackID : ackIDs) {
      if (ackID == null) {
        InvalidArgumentException invalidArgumentException = new InvalidArgumentException("AckID cannot be null in batch negative acknowledgment");
        this.permanentError.set(invalidArgumentException);
        throw invalidArgumentException;
      } 
      validateAckIDType(ackID);
    } 
    for (AckID ackID : ackIDs)
      this.ackBatcher.addNoWait(new AckInfo(ackID, false)); 
    return CompletableFuture.completedFuture(null);
  }
  
  protected void validateAckIDType(AckID ackID) {}
  
  private Void handleAckAndNackBatch(List<AckInfo> ackInfos) {
    if (ackInfos.isEmpty())
      return null; 
    List<AckID> acks = new ArrayList<>();
    List<AckID> nacks = new ArrayList<>();
    for (AckInfo info : ackInfos) {
      if (info.isAck()) {
        acks.add(info.getAckID());
        continue;
      } 
      nacks.add(info.getAckID());
    } 
    if (!acks.isEmpty())
      try {
        doSendAcks(acks);
      } catch (Exception e) {
        boolean permanent = !isRetryable(e);
        if (permanent && 
          this.permanentError.compareAndSet(null, e))
          this.unreportedAckErr.compareAndSet(null, e); 
        throw new SubstrateSdkException("Batch acknowledge failed", e);
      }  
    if (!nacks.isEmpty())
      try {
        doSendNacks(nacks);
      } catch (Exception e) {
        boolean permanent = !isRetryable(e);
        if (permanent && 
          this.permanentError.compareAndSet(null, e))
          this.unreportedAckErr.compareAndSet(null, e); 
        throw new SubstrateSdkException("Batch negative acknowledge failed", e);
      }  
    return null;
  }
  
  private void maybePrefetch() {
    if (this.isShutdown.get() || this.prefetchInFlight.get())
      return; 
    int qSize = this.queue.size();
    if (qSize <= (int)(this.runningBatchSize * 0.5D)) {
      int batchSize = updateBatchSize();
      if (this.prefetchInFlight.compareAndSet(false, true))
        this.backgroundPool.submit(() -> doPrefetch(batchSize)); 
    } 
  }
  
  private int updateBatchSize() {
    if (this.receiveBatcherOptions.getMaxHandlers() == 1 && this.receiveBatcherOptions.getMaxBatchSize() == 1)
      return 1; 
    long now = System.nanoTime();
    if (this.throughputStart != 0L) {
      long elapsedNanos = now - this.throughputStart;
      long minElapsedNanos = Duration.ofMillis(100L).toNanos();
      if (elapsedNanos < minElapsedNanos)
        elapsedNanos = minElapsedNanos; 
      double elapsedSeconds = elapsedNanos / 1.0E9D;
      double msgsPerSec = this.throughputCount / elapsedSeconds;
      double idealBatchSize = QueueConfig.DESIRED_QUEUE_DURATION.getSeconds() * msgsPerSec;
      double newBatchSize = this.runningBatchSize * 0.5D + idealBatchSize * 0.5D;
      double maxSize = this.runningBatchSize * 2.0D;
      double minSize = this.runningBatchSize * 0.75D;
      if (newBatchSize > maxSize) {
        this.runningBatchSize = maxSize;
      } else if (newBatchSize < minSize) {
        this.runningBatchSize = minSize;
      } else {
        this.runningBatchSize = newBatchSize;
      } 
    } 
    this.throughputStart = now;
    this.throughputCount = 0;
    return (int)Math.ceil(Math.min(this.runningBatchSize, 3000.0D));
  }
  
  private List<Message> getNextBatch(int nMessages) {
    List<Integer> batchSizes = Batcher.split(nMessages, this.receiveBatcherOptions);
    if (batchSizes.isEmpty())
      return Collections.emptyList(); 
    if (batchSizes.size() == 1)
      return doReceiveBatch(((Integer)batchSizes.get(0)).intValue()); 
    List<CompletableFuture<List<Message>>> futures = new ArrayList<>();
    for (Iterator<Integer> iterator = batchSizes.iterator(); iterator.hasNext(); ) {
      int size = ((Integer)iterator.next()).intValue();
      futures.add(CompletableFuture.supplyAsync(() -> doReceiveBatch(size), this.backgroundPool));
    } 
    List<Message> combined = new ArrayList<>();
    for (CompletableFuture<List<Message>> f : futures) {
      try {
        combined.addAll(f.get());
      } catch (InterruptedException ie) {
        Thread.currentThread().interrupt();
        throw new SubstrateSdkException("Interrupted while waiting for batch futures", ie);
      } catch (ExecutionException ee) {
        Throwable cause = ee.getCause();
        if (cause instanceof RuntimeException)
          throw (RuntimeException)cause; 
        if (cause != null)
          throw new SubstrateSdkException("Error receiving batch subscription", cause); 
        throw new SubstrateSdkException("Error receiving batch subscription", ee);
      } 
    } 
    return combined;
  }
  
  private void doPrefetch(int batchSize) {
    try {
      List<Message> msgs = getNextBatch(batchSize);
      this.lock.lock();
      try {
        this.queue.addAll(msgs);
        this.batchArrived.signalAll();
      } finally {
        this.lock.unlock();
      } 
    } catch (Throwable t) {
      this.lock.lock();
      try {
        if (!isRetryable(t))
          this.permanentError.compareAndSet(null, t); 
        this.batchArrived.signalAll();
      } finally {
        this.lock.unlock();
      } 
    } finally {
      this.prefetchInFlight.set(false);
    } 
  }
  
  public void close() throws Exception {
    this.isShutdown.set(true);
    this.lock.lock();
    try {
      this.batchArrived.signalAll();
    } finally {
      this.lock.unlock();
    } 
    if (this.ackBatcher != null)
      this.ackBatcher.shutdownAndDrain(); 
    if (this.backgroundPool != null && !this.backgroundPool.isShutdown()) {
      this.backgroundPool.shutdown();
      try {
        this.backgroundPool.awaitTermination(10L, TimeUnit.SECONDS);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      } 
    } 
    Throwable ackError = this.unreportedAckErr.getAndSet(null);
    if (ackError != null)
      throw new SubstrateSdkException("Unreported ack error during shutdown", ackError); 
  }
  
  protected abstract List<Message> doReceiveBatch(int paramInt);
  
  public abstract boolean canNack();
  
  public abstract boolean isRetryable(Throwable paramThrowable);
  
  public abstract GetAttributeResult getAttributes();
  
  protected abstract void doSendAcks(List<AckID> paramList);
  
  protected abstract void doSendNacks(List<AckID> paramList);
  
  protected abstract Batcher.Options createAckBatcherOptions();
  
  public abstract Class<? extends SubstrateSdkException> getException(Throwable paramThrowable);
  
  public static abstract class Builder<T extends AbstractSubscription<T>> implements Provider.Builder {
    protected String providerId;
    
    protected String subscriptionName;
    
    protected String region;
    
    protected URI endpoint;
    
    protected URI proxyEndpoint;
    
    protected CredentialsOverrider credentialsOverrider;
    
    public Builder<T> providerId(String providerId) {
      this.providerId = providerId;
      return this;
    }
    
    public Builder<T> withSubscriptionName(String subscriptionName) {
      this.subscriptionName = subscriptionName;
      return this;
    }
    
    public Builder<T> withRegion(String region) {
      this.region = region;
      return this;
    }
    
    public Builder<T> withEndpoint(URI endpoint) {
      this.endpoint = endpoint;
      return this;
    }
    
    public Builder<T> withProxyEndpoint(URI proxyEndpoint) {
      this.proxyEndpoint = proxyEndpoint;
      return this;
    }
    
    public Builder<T> withCredentialsOverrider(CredentialsOverrider credentialsOverrider) {
      this.credentialsOverrider = credentialsOverrider;
      return this;
    }
    
    public abstract T build();
  }
}
