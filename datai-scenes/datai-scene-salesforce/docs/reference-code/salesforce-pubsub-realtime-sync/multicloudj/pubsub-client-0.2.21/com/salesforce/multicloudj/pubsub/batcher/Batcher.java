package com.salesforce.multicloudj.pubsub.batcher;

import com.salesforce.multicloudj.common.exceptions.FailedPreconditionException;
import com.salesforce.multicloudj.common.exceptions.InvalidArgumentException;
import com.salesforce.multicloudj.common.exceptions.SubstrateSdkException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import lombok.Generated;

public class Batcher<T> {
  private final Options options;
  
  private final Function<List<T>, Void> handler;
  
  private final ExecutorService executorService;
  
  public static class Options {
    @Generated
    public Options setMaxHandlers(int maxHandlers) {
      this.maxHandlers = maxHandlers;
      return this;
    }
    
    @Generated
    public Options setMinBatchSize(int minBatchSize) {
      this.minBatchSize = minBatchSize;
      return this;
    }
    
    @Generated
    public Options setMaxBatchSize(int maxBatchSize) {
      this.maxBatchSize = maxBatchSize;
      return this;
    }
    
    @Generated
    public Options setMaxBatchByteSize(int maxBatchByteSize) {
      this.maxBatchByteSize = maxBatchByteSize;
      return this;
    }
    
    private int maxHandlers = 1;
    
    @Generated
    public int getMaxHandlers() {
      return this.maxHandlers;
    }
    
    private int minBatchSize = 1;
    
    @Generated
    public int getMinBatchSize() {
      return this.minBatchSize;
    }
    
    private int maxBatchSize = 0;
    
    @Generated
    public int getMaxBatchSize() {
      return this.maxBatchSize;
    }
    
    private int maxBatchByteSize = 0;
    
    @Generated
    public int getMaxBatchByteSize() {
      return this.maxBatchByteSize;
    }
  }
  
  private int getItemByteSize(T item) {
    if (item instanceof SizableItem)
      return ((SizableItem)item).getByteSize(); 
    return 0;
  }
  
  public static interface SizableItem {
    int getByteSize();
  }
  
  private static class Item<T> {
    final T batchItem;
    
    final CompletableFuture<Void> future;
    
    Item(T batchItem, CompletableFuture<Void> future) {
      this.batchItem = batchItem;
      this.future = future;
    }
  }
  
  private final ReentrantLock lock = new ReentrantLock();
  
  private final Condition workCompleted = this.lock.newCondition();
  
  private final List<Item<T>> pending = new ArrayList<>();
  
  private final AtomicInteger activeHandlers = new AtomicInteger(0);
  
  private volatile boolean shutdown = false;
  
  public Batcher(Function<List<T>, Void> handler) {
    this(new Options(), handler);
  }
  
  public Batcher(Options options, Function<List<T>, Void> handler) {
    if (options.getMaxHandlers() <= 0)
      throw new InvalidArgumentException("maxHandlers must be greater than 0"); 
    this.options = options;
    this.handler = handler;
    this.executorService = Executors.newFixedThreadPool(options.getMaxHandlers(), r -> new Thread(r, "Batcher-Handler"));
  }
  
  public void add(T item) {
    try {
      addNoWait(item).get();
    } catch (ExecutionException e) {
      if (e.getCause() instanceof RuntimeException)
        throw (RuntimeException)e.getCause(); 
      if (e.getCause() != null)
        throw new SubstrateSdkException("Error executing pubsub batch handler", e.getCause()); 
      throw new SubstrateSdkException("Error executing pubsub batch handler", e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new SubstrateSdkException("Interrupted while waiting for pubsub batch to complete", e);
    } 
  }
  
  public CompletableFuture<Void> addNoWait(T item) {
    this.lock.lock();
    try {
      if (item == null)
        throw new InvalidArgumentException("Item cannot be null"); 
      CompletableFuture<Void> future = new CompletableFuture<>();
      if (isShutdown()) {
        future.completeExceptionally((Throwable)new FailedPreconditionException("Batcher is shut down"));
        return future;
      } 
      int itemSize = getItemByteSize(item);
      if (this.options.getMaxBatchByteSize() > 0 && itemSize > this.options.getMaxBatchByteSize()) {
        future.completeExceptionally((Throwable)new InvalidArgumentException("Item size " + itemSize + " exceeds maximum batch byte size = " + this.options
              .getMaxBatchByteSize()));
        return future;
      } 
      this.pending.add(new Item<>(item, future));
      tryStartNewBatchHandler(false);
      return future;
    } finally {
      this.lock.unlock();
    } 
  }
  
  private List<Item<T>> getNextBatch(boolean ignoreMinBatchSize) {
    if (!ignoreMinBatchSize && this.pending.size() < this.options.getMinBatchSize())
      return null; 
    if (this.pending.isEmpty())
      return null; 
    if (this.options.getMaxBatchByteSize() == 0 && (this.options
      .getMaxBatchSize() == 0 || this.pending.size() <= this.options.getMaxBatchSize())) {
      List<Item<T>> list = new ArrayList<>(this.pending);
      this.pending.clear();
      return list;
    } 
    List<Item<T>> batch = new ArrayList<>();
    int batchByteSize = 0;
    Iterator<Item<T>> iterator = this.pending.iterator();
    while (iterator.hasNext()) {
      Item<T> item = iterator.next();
      int itemByteSize = getItemByteSize(item.batchItem);
      boolean reachedMaxSize = (this.options.getMaxBatchSize() > 0 && batch.size() + 1 > this.options.getMaxBatchSize());
      boolean reachedMaxByteSize = (this.options.getMaxBatchByteSize() > 0 && batchByteSize + itemByteSize > this.options.getMaxBatchByteSize());
      if (reachedMaxSize || reachedMaxByteSize)
        break; 
      batch.add(item);
      batchByteSize += itemByteSize;
      iterator.remove();
    } 
    return batch.isEmpty() ? null : batch;
  }
  
  private void processHandler(List<Item<T>> initialBatch) {
    List<Item<T>> batch = initialBatch;
    try {
      while (batch != null) {
        List<T> items = new ArrayList<>();
        for (Item<T> item : batch)
          items.add(item.batchItem); 
        RuntimeException processingError = null;
        try {
          this.handler.apply(items);
        } catch (RuntimeException e) {
          processingError = e;
        } 
        for (Item<T> item : batch) {
          if (processingError != null) {
            item.future.completeExceptionally(processingError);
            continue;
          } 
          item.future.complete(null);
        } 
        this.lock.lock();
        try {
          batch = getNextBatch(false);
          if (batch == null)
            handlerFinished(); 
        } finally {
          this.lock.unlock();
        } 
      } 
    } catch (RuntimeException e) {
      this.lock.lock();
      try {
        for (Item<T> item : this.pending)
          item.future.completeExceptionally(e); 
        this.pending.clear();
        handlerFinished();
      } finally {
        this.lock.unlock();
      } 
    } 
  }
  
  private boolean tryStartNewBatchHandler(boolean ignoreMinBatchSize) {
    if (this.activeHandlers.get() < this.options.getMaxHandlers()) {
      List<Item<T>> batch = getNextBatch(ignoreMinBatchSize);
      if (batch != null) {
        this.activeHandlers.incrementAndGet();
        this.executorService.submit(() -> processHandler(batch));
        return true;
      } 
    } 
    return false;
  }
  
  private void handlerFinished() {
    this.activeHandlers.decrementAndGet();
    this.workCompleted.signalAll();
  }
  
  public void shutdownAndDrain() {
    this.lock.lock();
    try {
      this.shutdown = true;
    } finally {
      this.lock.unlock();
    } 
    this.lock.lock();
    try {
      while (!this.pending.isEmpty() || this.activeHandlers.get() > 0) {
        while (tryStartNewBatchHandler(true))
          Thread.yield(); 
        if (!this.pending.isEmpty() || this.activeHandlers.get() > 0)
          try {
            this.workCompleted.await();
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            break;
          }  
      } 
    } finally {
      this.lock.unlock();
    } 
    this.executorService.shutdown();
    try {
      this.executorService.awaitTermination(10L, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } 
  }
  
  public boolean isShutdown() {
    return this.shutdown;
  }
  
  public static List<Integer> split(int n, Options opts) {
    if (opts == null)
      opts = new Options(); 
    if (n < opts.getMinBatchSize())
      return Collections.emptyList(); 
    if (opts.getMaxBatchSize() == 0)
      return List.of(Integer.valueOf(n)); 
    List<Integer> batches = new ArrayList<>();
    while (n >= opts.getMinBatchSize() && batches.size() < opts.getMaxHandlers()) {
      int b = opts.getMaxBatchSize();
      if (b > n)
        b = n; 
      batches.add(Integer.valueOf(b));
      n -= b;
    } 
    return batches;
  }
}
