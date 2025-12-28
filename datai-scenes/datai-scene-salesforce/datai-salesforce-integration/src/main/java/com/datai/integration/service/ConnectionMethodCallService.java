package com.datai.integration.service;

import com.datai.integration.domain.DataiIntegrationRateLimit;

import java.util.Map;

public interface ConnectionMethodCallService {

    void recordMethodCall(String className, String methodName, long executionTime, Throwable exception);

    DataiIntegrationRateLimit getMethodCallStatistics(String apiType, String limitType);

    Map<String, Integer> getAllMethodCallCounts();
}
