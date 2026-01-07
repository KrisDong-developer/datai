package com.datai.integration.proxy;

import com.datai.integration.model.domain.DataiIntegrationApiCallLog;
import com.datai.integration.model.domain.DataiIntegrationRateLimit;
import com.datai.integration.service.IDataiIntegrationApiCallLogService;
import com.datai.integration.service.IDataiIntegrationRateLimitService;
import com.datai.salesforce.common.exception.RateLimitExceededException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
public class ConnectionProxy {

    @Autowired
    private IDataiIntegrationApiCallLogService apiCallLogService;

    @Autowired
    private IDataiIntegrationRateLimitService rateLimitService;

    private final ConcurrentHashMap<String, AtomicInteger> methodCallCountMap = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, Long> methodCallTimeMap = new ConcurrentHashMap<>();

    private final Map<String, Integer> defaultApiLimits = new ConcurrentHashMap<>();

    private final ScheduledExecutorService cleanupExecutor;

    private static final long CLEANUP_INTERVAL_MINUTES = 60;
    private static final long MAX_ENTRY_AGE_MINUTES = 1440;
    private static final int MAX_RETRY_TIMES = 3;

    private final ConcurrentHashMap<String, ReentrantLock> rateLimitLocks = new ConcurrentHashMap<>();

    public ConnectionProxy() {
        defaultApiLimits.put("BULK_V1", 5000);
        defaultApiLimits.put("BULK_V2", 10000);
        defaultApiLimits.put("SOAP", 15000);
        defaultApiLimits.put("REST", 15000);

        cleanupExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "ConnectionProxy-Cleanup");
            thread.setDaemon(true);
            return thread;
        });

        cleanupExecutor.scheduleWithFixedDelay(
            this::cleanupOldEntries,
            CLEANUP_INTERVAL_MINUTES,
            CLEANUP_INTERVAL_MINUTES,
            TimeUnit.MINUTES
        );
    }

    @SuppressWarnings("unchecked")
    public <T> T createProxy(T target, String apiType) {
        Class<?> targetClass = target.getClass();
        
        if (targetClass.getInterfaces().length > 0) {
            return (T) Proxy.newProxyInstance(
                targetClass.getClassLoader(),
                targetClass.getInterfaces(),
                new ConnectionInvocationHandler(target, apiType)
            );
        } else {
            try {
                Enhancer enhancer = new Enhancer();
                enhancer.setSuperclass(targetClass);
                enhancer.setInterfaces(targetClass.getInterfaces());
                enhancer.setCallback(new CglibMethodInterceptor(target, apiType));
                enhancer.setUseCache(false);
                
                return (T) enhancer.create();
            } catch (Exception e) {
                log.error("创建CGLIB代理失败", e);
                throw new RuntimeException("创建CGLIB代理失败: " + e.getMessage(), e);
            }
        }
    }

    private class ConnectionInvocationHandler implements InvocationHandler {
        private final Object target;
        private final String apiType;

        public ConnectionInvocationHandler(Object target, String apiType) {
            this.target = target;
            this.apiType = apiType;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return handleMethodInvocation(target, method, args, apiType);
        }
    }

    private class CglibMethodInterceptor implements MethodInterceptor {
        private final Object target;
        private final String apiType;

        public CglibMethodInterceptor(Object target, String apiType) {
            this.target = target;
            this.apiType = apiType;
        }

        @Override
        public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            return handleMethodInvocation(target, method, args, apiType);
        }
    }

    private Object handleMethodInvocation(Object target, Method method, Object[] args, String apiType) throws Throwable {
        String className = target.getClass().getSimpleName();
        String methodName = method.getName();
        String key = className + "." + methodName;

        long startTime = System.currentTimeMillis();
        Object result = null;
        Throwable exception = null;

        try {
            checkRateLimit(apiType);
            
            result = method.invoke(target, args);
            return result;
        } catch (Throwable e) {
            exception = e;
            throw e;
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;
            
            methodCallCountMap.computeIfAbsent(key, k -> new AtomicInteger(0)).incrementAndGet();
            methodCallTimeMap.put(key, System.currentTimeMillis());
            
            log.debug("方法调用统计 - 类: {}, 方法: {}, 调用次数: {}, 执行时间: {}ms", 
                     className, methodName, methodCallCountMap.get(key).get(), executionTime);

            recordApiCallLog(className, methodName, apiType, executionTime, exception);
            
            if (exception == null) {
                updateRateLimitUsage(apiType);
            }
        }
    }

    private void checkRateLimit(String apiType) {
        List<DataiIntegrationRateLimit> rateLimits = getRateLimits(apiType);

        for (DataiIntegrationRateLimit rateLimit : rateLimits) {
            if (rateLimit.getIsBlocked() != null && rateLimit.getIsBlocked()) {
                throwRateLimitException(rateLimit);
            }

            if (rateLimit.getRemainingVal() != null && rateLimit.getRemainingVal() <= 0) {
                rateLimit.setIsBlocked(true);
                rateLimitService.updateDataiIntegrationRateLimit(rateLimit);
                throwRateLimitException(rateLimit);
            }
        }
    }

    private List<DataiIntegrationRateLimit> getRateLimits(String apiType) {
        DataiIntegrationRateLimit query = new DataiIntegrationRateLimit();
        query.setApiType(apiType);
        query.setLimitType("Daily");
        List<DataiIntegrationRateLimit> rateLimits = rateLimitService.selectDataiIntegrationRateLimitList(query);

        if (rateLimits.isEmpty()) {
            createDefaultRateLimit(apiType);
            rateLimits = rateLimitService.selectDataiIntegrationRateLimitList(query);
        }

        return rateLimits;
    }

    private void throwRateLimitException(DataiIntegrationRateLimit rateLimit) {
        throw new RateLimitExceededException(
            rateLimit.getApiType(),
            rateLimit.getLimitType(),
            rateLimit.getCurrentUsage(),
            rateLimit.getMaxLimit()
        );
    }

    private void createDefaultRateLimit(String apiType) {
        try {
            Integer defaultLimit = defaultApiLimits.get(apiType);
            if (defaultLimit == null) {
                log.warn("未找到 API 类型 {} 的默认限流配置", apiType);
                return;
            }

            DataiIntegrationRateLimit rateLimit = new DataiIntegrationRateLimit();
            rateLimit.setApiType(apiType);
            rateLimit.setLimitType("Daily");
            rateLimit.setCurrentUsage(0);
            rateLimit.setMaxLimit(defaultLimit);
            rateLimit.setRemainingVal(defaultLimit);
            rateLimit.setIsBlocked(false);
            rateLimit.setResetTime(LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIDNIGHT));

            rateLimitService.insertDataiIntegrationRateLimit(rateLimit);
            log.info("自动创建默认限流配置 - API类型: {}, 总额度: {}", apiType, defaultLimit);
        } catch (Exception e) {
            log.error("创建默认限流配置失败 - API类型: {}", apiType, e);
        }
    }

    private void updateRateLimitUsage(String apiType) {
        ReentrantLock lock = rateLimitLocks.computeIfAbsent(apiType, k -> new ReentrantLock());
        lock.lock();
        try {
            List<DataiIntegrationRateLimit> rateLimits = getRateLimits(apiType);

            for (DataiIntegrationRateLimit rateLimit : rateLimits) {
                if (rateLimit.getIsBlocked() != null && rateLimit.getIsBlocked()) {
                    continue;
                }

                int currentUsage = rateLimit.getCurrentUsage() != null ? rateLimit.getCurrentUsage() : 0;
                int maxLimit = rateLimit.getMaxLimit() != null ? rateLimit.getMaxLimit() : 0;
                int remainingVal = maxLimit - currentUsage - 1;
                Integer oldVersion = rateLimit.getVersion();

                rateLimit.setCurrentUsage(currentUsage + 1);
                rateLimit.setRemainingVal(remainingVal);

                if (remainingVal <= 0) {
                    rateLimit.setIsBlocked(true);
                    log.warn("API限流触发 - 类型: {}, 限制维度: {}, 已用额度: {}, 总额度: {}", 
                            apiType, rateLimit.getLimitType(), currentUsage + 1, maxLimit);
                }

                boolean updateSuccess = false;
                int retryCount = 0;
                
                while (!updateSuccess && retryCount < MAX_RETRY_TIMES) {
                    try {
                        int affectedRows = rateLimitService.updateDataiIntegrationRateLimit(rateLimit);
                        if (affectedRows > 0) {
                            updateSuccess = true;
                        } else {
                            retryCount++;
                            if (retryCount < MAX_RETRY_TIMES) {
                                log.warn("更新限流配置失败，版本号可能已变更，正在重试 ({}/{}) - API类型: {}", 
                                        retryCount, MAX_RETRY_TIMES, apiType);
                                DataiIntegrationRateLimit latest = rateLimitService.selectDataiIntegrationRateLimitById(rateLimit.getId());
                                if (latest != null) {
                                    rateLimit.setVersion(latest.getVersion());
                                    rateLimit.setCurrentUsage(latest.getCurrentUsage() + 1);
                                    rateLimit.setRemainingVal(latest.getMaxLimit() - latest.getCurrentUsage() - 1);
                                }
                                Thread.sleep(10);
                            }
                        }
                    } catch (Exception e) {
                        retryCount++;
                        if (retryCount >= MAX_RETRY_TIMES) {
                            log.error("更新限流配置失败，已达最大重试次数 - API类型: {}", apiType, e);
                            throw new RuntimeException("更新限流配置失败: " + e.getMessage(), e);
                        }
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            throw new RuntimeException("线程被中断", ie);
                        }
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }

    private void recordApiCallLog(String className, String methodName, String apiType, 
                                  long executionTime, Throwable exception) {
        try {
            DataiIntegrationApiCallLog callLog = new DataiIntegrationApiCallLog();
            callLog.setApiType(apiType);
            callLog.setConnectionClass(className);
            callLog.setMethodName(methodName);
            callLog.setExecutionTime(executionTime);
            callLog.setCallTime(LocalDateTime.now());

            if (exception == null) {
                callLog.setStatus("SUCCESS");
            } else {
                callLog.setStatus("FAILED");
                String errorMessage = exception.getMessage();
                callLog.setErrorMessage(errorMessage != null ? errorMessage : exception.getClass().getName());
            }

            apiCallLogService.insertDataiIntegrationApiCallLog(callLog);
        } catch (Exception e) {
            log.error("记录API调用日志失败", e);
        }
    }

    public ConcurrentHashMap<String, AtomicInteger> getMethodCallCountMap() {
        return methodCallCountMap;
    }

    private void cleanupOldEntries() {
        try {
            long currentTime = System.currentTimeMillis();
            long maxAgeMillis = MAX_ENTRY_AGE_MINUTES * 60 * 1000;

            methodCallCountMap.entrySet().removeIf(entry -> {
                String key = entry.getKey();
                AtomicInteger counter = entry.getValue();
                Long lastCallTime = methodCallTimeMap.get(key);
                
                if (counter.get() == 0) {
                    methodCallTimeMap.remove(key);
                    return true;
                }
                
                if (lastCallTime != null && (currentTime - lastCallTime) > maxAgeMillis) {
                    methodCallTimeMap.remove(key);
                    return true;
                }
                
                return false;
            });

            if (!methodCallCountMap.isEmpty()) {
                log.debug("方法调用统计清理完成，当前统计项数: {}", methodCallCountMap.size());
            }
        } catch (Exception e) {
            log.error("清理方法调用统计时发生错误", e);
        }
    }

    public void shutdown() {
        if (cleanupExecutor != null && !cleanupExecutor.isShutdown()) {
            cleanupExecutor.shutdown();
            try {
                if (!cleanupExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    cleanupExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                cleanupExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}
