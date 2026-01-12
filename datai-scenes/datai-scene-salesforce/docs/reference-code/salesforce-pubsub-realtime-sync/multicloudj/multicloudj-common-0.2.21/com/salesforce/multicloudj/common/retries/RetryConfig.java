package com.salesforce.multicloudj.common.retries;

import lombok.Generated;

public final class RetryConfig {
  private final Mode mode;
  
  private final Integer maxAttempts;
  
  private final long initialDelayMillis;
  
  private final double multiplier;
  
  private final long maxDelayMillis;
  
  private final long fixedDelayMillis;
  
  private final Long attemptTimeout;
  
  private final Long totalTimeout;
  
  @Generated
  RetryConfig(Mode mode, Integer maxAttempts, long initialDelayMillis, double multiplier, long maxDelayMillis, long fixedDelayMillis, Long attemptTimeout, Long totalTimeout) {
    this.mode = mode;
    this.maxAttempts = maxAttempts;
    this.initialDelayMillis = initialDelayMillis;
    this.multiplier = multiplier;
    this.maxDelayMillis = maxDelayMillis;
    this.fixedDelayMillis = fixedDelayMillis;
    this.attemptTimeout = attemptTimeout;
    this.totalTimeout = totalTimeout;
  }
  
  @Generated
  public static RetryConfigBuilder builder() {
    return new RetryConfigBuilder();
  }
  
  @Generated
  public static class RetryConfigBuilder {
    @Generated
    private RetryConfig.Mode mode;
    
    @Generated
    private Integer maxAttempts;
    
    @Generated
    private long initialDelayMillis;
    
    @Generated
    private double multiplier;
    
    @Generated
    private long maxDelayMillis;
    
    @Generated
    private long fixedDelayMillis;
    
    @Generated
    private Long attemptTimeout;
    
    @Generated
    private Long totalTimeout;
    
    @Generated
    public RetryConfigBuilder mode(RetryConfig.Mode mode) {
      this.mode = mode;
      return this;
    }
    
    @Generated
    public RetryConfigBuilder maxAttempts(Integer maxAttempts) {
      this.maxAttempts = maxAttempts;
      return this;
    }
    
    @Generated
    public RetryConfigBuilder initialDelayMillis(long initialDelayMillis) {
      this.initialDelayMillis = initialDelayMillis;
      return this;
    }
    
    @Generated
    public RetryConfigBuilder multiplier(double multiplier) {
      this.multiplier = multiplier;
      return this;
    }
    
    @Generated
    public RetryConfigBuilder maxDelayMillis(long maxDelayMillis) {
      this.maxDelayMillis = maxDelayMillis;
      return this;
    }
    
    @Generated
    public RetryConfigBuilder fixedDelayMillis(long fixedDelayMillis) {
      this.fixedDelayMillis = fixedDelayMillis;
      return this;
    }
    
    @Generated
    public RetryConfigBuilder attemptTimeout(Long attemptTimeout) {
      this.attemptTimeout = attemptTimeout;
      return this;
    }
    
    @Generated
    public RetryConfigBuilder totalTimeout(Long totalTimeout) {
      this.totalTimeout = totalTimeout;
      return this;
    }
    
    @Generated
    public RetryConfig build() {
      return new RetryConfig(this.mode, this.maxAttempts, this.initialDelayMillis, this.multiplier, this.maxDelayMillis, this.fixedDelayMillis, this.attemptTimeout, this.totalTimeout);
    }
    
    @Generated
    public String toString() {
      return "RetryConfig.RetryConfigBuilder(mode=" + String.valueOf(this.mode) + ", maxAttempts=" + this.maxAttempts + ", initialDelayMillis=" + this.initialDelayMillis + ", multiplier=" + this.multiplier + ", maxDelayMillis=" + this.maxDelayMillis + ", fixedDelayMillis=" + this.fixedDelayMillis + ", attemptTimeout=" + this.attemptTimeout + ", totalTimeout=" + this.totalTimeout + ")";
    }
  }
  
  public enum Mode {
    EXPONENTIAL, FIXED;
  }
  
  @Generated
  public Mode getMode() {
    return this.mode;
  }
  
  @Generated
  public Integer getMaxAttempts() {
    return this.maxAttempts;
  }
  
  @Generated
  public long getInitialDelayMillis() {
    return this.initialDelayMillis;
  }
  
  @Generated
  public double getMultiplier() {
    return this.multiplier;
  }
  
  @Generated
  public long getMaxDelayMillis() {
    return this.maxDelayMillis;
  }
  
  @Generated
  public long getFixedDelayMillis() {
    return this.fixedDelayMillis;
  }
  
  @Generated
  public Long getAttemptTimeout() {
    return this.attemptTimeout;
  }
  
  @Generated
  public Long getTotalTimeout() {
    return this.totalTimeout;
  }
}
