package com.datai.integration.aspect;

import com.datai.integration.domain.DataiIntegrationRateLimit;
import com.datai.integration.service.ConnectionMethodCallService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Aspect
@Component
public class ConnectionMethodCallAspect {

    @Autowired
    private ConnectionMethodCallService connectionMethodCallService;

    private final ConcurrentHashMap<String, AtomicInteger> methodCallCountMap = new ConcurrentHashMap<>();

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

        long startTime = System.currentTimeMillis();
        Object result = null;
        Throwable exception = null;

        try {
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

            connectionMethodCallService.recordMethodCall(className, methodName, executionTime, exception);
        }
    }

    public ConcurrentHashMap<String, AtomicInteger> getMethodCallCountMap() {
        return methodCallCountMap;
    }
}
