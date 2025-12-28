package com.datai.salesforce.common.exception;

/**
 * API限流异常
 * 当API调用超过配额限制时抛出此异常
 * 
 * @author datai
 * @date 2025-12-28
 */
public class RateLimitExceededException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    private String apiType;
    private String limitType;
    private Integer currentUsage;
    private Integer maxLimit;

    public RateLimitExceededException(String message)
    {
        super(message);
    }

    public RateLimitExceededException(String apiType, String limitType, Integer currentUsage, Integer maxLimit)
    {
        super(String.format("API限流异常 - 类型: %s, 限制维度: %s, 已用额度: %d, 总额度: %d", 
                           apiType, limitType, currentUsage, maxLimit));
        this.apiType = apiType;
        this.limitType = limitType;
        this.currentUsage = currentUsage;
        this.maxLimit = maxLimit;
    }

    public String getApiType()
    {
        return apiType;
    }

    public String getLimitType()
    {
        return limitType;
    }

    public Integer getCurrentUsage()
    {
        return currentUsage;
    }

    public Integer getMaxLimit()
    {
        return maxLimit;
    }
}
