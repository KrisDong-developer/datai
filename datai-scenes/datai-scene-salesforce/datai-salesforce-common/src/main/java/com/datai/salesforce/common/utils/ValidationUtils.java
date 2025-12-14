package com.datai.salesforce.common.utils;

/**
 * 验证工具类
 */
public class ValidationUtils {

    /**
     * 验证对象不为null
     *
     * @param obj     要验证的对象
     * @param message 验证失败时的错误信息
     * @throws IllegalArgumentException 验证失败时抛出
     */
    public static void validateNotNull(Object obj, String message) {
        if (obj == null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 验证字符串不为null且不为空
     *
     * @param str     要验证的字符串
     * @param message 验证失败时的错误信息
     * @throws IllegalArgumentException 验证失败时抛出
     */
    public static void validateNotBlank(String str, String message) {
        if (str == null || str.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 验证Salesforce ID是否有效
     *
     * @param salesforceId 要验证的Salesforce ID
     * @param message      验证失败时的错误信息
     * @throws IllegalArgumentException 验证失败时抛出
     */
    public static void validateSalesforceId(String salesforceId, String message) {
        if (!IdUtils.isValid(salesforceId)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 验证数值大于0
     *
     * @param value   要验证的数值
     * @param message 验证失败时的错误信息
     * @throws IllegalArgumentException 验证失败时抛出
     */
    public static void validateGreaterThanZero(long value, String message) {
        if (value <= 0) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 验证数值大于等于0
     *
     * @param value   要验证的数值
     * @param message 验证失败时的错误信息
     * @throws IllegalArgumentException 验证失败时抛出
     */
    public static void validateGreaterThanOrEqualToZero(long value, String message) {
        if (value < 0) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 验证数值在指定范围内
     *
     * @param value   要验证的数值
     * @param min     最小值
     * @param max     最大值
     * @param message 验证失败时的错误信息
     * @throws IllegalArgumentException 验证失败时抛出
     */
    public static void validateInRange(long value, long min, long max, String message) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(message);
        }
    }
}