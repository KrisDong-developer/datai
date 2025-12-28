package com.datai.integration.aspect;

import com.datai.integration.domain.DataiIntegrationApiCallLog;
import com.datai.integration.domain.DataiIntegrationRateLimit;
import com.datai.salesforce.common.exception.RateLimitExceededException;
import com.datai.integration.service.IDataiIntegrationApiCallLogService;
import com.datai.integration.service.IDataiIntegrationRateLimitService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Aspect
@Component
public class ConnectionMethodCallAspect {

    @Autowired
    private IDataiIntegrationApiCallLogService apiCallLogService;

    @Autowired
    private IDataiIntegrationRateLimitService rateLimitService;

    private final ConcurrentHashMap<String, AtomicInteger> methodCallCountMap = new ConcurrentHashMap<>();

    private final Map<String, Integer> defaultApiLimits = new ConcurrentHashMap<>();

    public ConnectionMethodCallAspect() {
        defaultApiLimits.put("BulkV1", 5000);
        defaultApiLimits.put("BulkV2", 10000);
        defaultApiLimits.put("SOAP", 15000);
        defaultApiLimits.put("REST", 15000);
    }

    @Pointcut("execution(* com.datai.integration.core.BulkV1Connection.*(..)) || " +
              "execution(* com.datai.integration.core.BulkV2Connection.*(..)) || " +
              "execution(* com.datai.integration.core.PartnerV1Connection.*(..)) || " +
              "execution(* com.datai.integration.core.RESTConnection.*(..))")
    public void connectionMethods() {}

    @Around("connectionMethods()")
    public Object countMethodCalls(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String key = className + "." + methodName;
        String apiType = extractApiType(className);

        long startTime = System.currentTimeMillis();
        Object result = null;
        Throwable exception = null;

        try {
            checkRateLimit(apiType);
            
            result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            exception = e;
            throw e;
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;
            
            methodCallCountMap.computeIfAbsent(key, k -> new AtomicInteger(0)).incrementAndGet();
            
            log.debug("方法调用统计 - 类: {}, 方法: {}, 调用次数: {}, 执行时间: {}ms", 
                     className, methodName, methodCallCountMap.get(key).get(), executionTime);

            recordApiCallLog(className, methodName, apiType, executionTime, exception);
            
            if (exception == null) {
                updateRateLimitUsage(apiType);
            }
        }
    }

    private String extractApiType(String className) {
        if (className.contains("BulkV1")) {
            return "BulkV1";
        } else if (className.contains("BulkV2")) {
            return "BulkV2";
        } else if (className.contains("PartnerV1")) {
            return "SOAP";
        } else if (className.contains("REST")) {
            return "REST";
        }
        return "Unknown";
    }

    private void checkRateLimit(String apiType) {
        DataiIntegrationRateLimit query = new DataiIntegrationRateLimit();
        query.setApiType(apiType);
        query.setLimitType("Daily");
        List<DataiIntegrationRateLimit> rateLimits = rateLimitService.selectDataiIntegrationRateLimitList(query);

        if (rateLimits.isEmpty()) {
            createDefaultRateLimit(apiType);
            rateLimits = rateLimitService.selectDataiIntegrationRateLimitList(query);
        }

        for (DataiIntegrationRateLimit rateLimit : rateLimits) {
            if (rateLimit.getIsBlocked() != null && rateLimit.getIsBlocked()) {
                throw new RateLimitExceededException(
                    rateLimit.getApiType(),
                    rateLimit.getLimitType(),
                    rateLimit.getCurrentUsage(),
                    rateLimit.getMaxLimit()
                );
            }

            if (rateLimit.getRemainingVal() != null && rateLimit.getRemainingVal() <= 0) {
                rateLimit.setIsBlocked(true);
                rateLimitService.updateDataiIntegrationRateLimit(rateLimit);
                throw new RateLimitExceededException(
                    rateLimit.getApiType(),
                    rateLimit.getLimitType(),
                    rateLimit.getCurrentUsage(),
                    rateLimit.getMaxLimit()
                );
            }
        }
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
        DataiIntegrationRateLimit query = new DataiIntegrationRateLimit();
        query.setApiType(apiType);
        query.setLimitType("Daily");
        List<DataiIntegrationRateLimit> rateLimits = rateLimitService.selectDataiIntegrationRateLimitList(query);

        if (rateLimits.isEmpty()) {
            createDefaultRateLimit(apiType);
            rateLimits = rateLimitService.selectDataiIntegrationRateLimitList(query);
        }

        for (DataiIntegrationRateLimit rateLimit : rateLimits) {
            if (rateLimit.getIsBlocked() != null && rateLimit.getIsBlocked()) {
                continue;
            }

            int currentUsage = rateLimit.getCurrentUsage() != null ? rateLimit.getCurrentUsage() : 0;
            int maxLimit = rateLimit.getMaxLimit() != null ? rateLimit.getMaxLimit() : 0;
            int remainingVal = maxLimit - currentUsage - 1;

            rateLimit.setCurrentUsage(currentUsage + 1);
            rateLimit.setRemainingVal(remainingVal);

            if (remainingVal <= 0) {
                rateLimit.setIsBlocked(true);
                log.warn("API限流触发 - 类型: {}, 限制维度: {}, 已用额度: {}, 总额度: {}", 
                        apiType, rateLimit.getLimitType(), currentUsage + 1, maxLimit);
            }

            rateLimitService.updateDataiIntegrationRateLimit(rateLimit);
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
                callLog.setErrorMessage(exception.getMessage());
            }

            apiCallLogService.insertDataiIntegrationApiCallLog(callLog);
        } catch (Exception e) {
            log.error("记录API调用日志失败", e);
        }
    }

    public ConcurrentHashMap<String, AtomicInteger> getMethodCallCountMap() {
        return methodCallCountMap;
    }
}
