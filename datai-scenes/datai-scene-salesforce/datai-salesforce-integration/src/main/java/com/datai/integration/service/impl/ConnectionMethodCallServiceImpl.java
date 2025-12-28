package com.datai.integration.service.impl;

import com.datai.integration.aspect.ConnectionMethodCallAspect;
import com.datai.integration.domain.DataiIntegrationRateLimit;
import com.datai.integration.mapper.DataiIntegrationRateLimitMapper;
import com.datai.integration.service.ConnectionMethodCallService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ConnectionMethodCallServiceImpl implements ConnectionMethodCallService {

    @Autowired
    private DataiIntegrationRateLimitMapper rateLimitMapper;

    @Autowired
    private ConnectionMethodCallAspect connectionMethodCallAspect;

    @Override
    public void recordMethodCall(String className, String methodName, long executionTime, Throwable exception) {
        try {
            String apiType = determineApiType(className);
            String limitType = methodName;

            DataiIntegrationRateLimit existing = getMethodCallStatistics(apiType, limitType);

            if (existing != null) {
                existing.setCurrentUsage(existing.getCurrentUsage() + 1);
                rateLimitMapper.updateDataiIntegrationRateLimit(existing);
            } else {
                DataiIntegrationRateLimit newRecord = new DataiIntegrationRateLimit();
                newRecord.setApiType(apiType);
                newRecord.setLimitType(limitType);
                newRecord.setCurrentUsage(1);
                newRecord.setMaxLimit(10000);
                newRecord.setRemainingVal(9999);
                newRecord.setResetTime(LocalDateTime.now().plusDays(1));
                newRecord.setIsBlocked(false);
                rateLimitMapper.insertDataiIntegrationRateLimit(newRecord);
            }

            log.debug("方法调用统计已持久化 - 类: {}, 方法: {}, 执行时间: {}ms, 是否异常: {}",
                     className, methodName, executionTime, exception != null);

        } catch (Exception e) {
            log.error("持久化方法调用统计失败 - 类: {}, 方法: {}, error: {}", className, methodName, e.getMessage(), e);
        }
    }

    @Override
    public DataiIntegrationRateLimit getMethodCallStatistics(String apiType, String limitType) {
        DataiIntegrationRateLimit query = new DataiIntegrationRateLimit();
        query.setApiType(apiType);
        query.setLimitType(limitType);
        List<DataiIntegrationRateLimit> results = rateLimitMapper.selectDataiIntegrationRateLimitList(query);
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public Map<String, Integer> getAllMethodCallCounts() {
        Map<String, Integer> result = new HashMap<>();
        connectionMethodCallAspect.getMethodCallCountMap().forEach((key, value) -> {
            result.put(key, value.get());
        });
        return result;
    }

    private String determineApiType(String className) {
        if (className.contains("BulkV1")) {
            return "BULK_API_V1";
        } else if (className.contains("BulkV2")) {
            return "BULK_API_V2";
        } else if (className.contains("PartnerV1")) {
            return "SOAP_API";
        } else if (className.contains("REST")) {
            return "REST_API";
        }
        return "UNKNOWN";
    }
}
