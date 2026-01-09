package com.datai.salesforce.common.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
    
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * 验证时间格式是否正确
     * 
     * @param timeStr 时间字符串
     * @return 验证结果
     */
    public static boolean validateTimeFormat(String timeStr) {
        if (timeStr == null || timeStr.isEmpty()) {
            return true;
        }
        
        try {
            // 尝试解析为完整的日期时间格式
            LocalDateTime.parse(timeStr, DATE_TIME_FORMATTER);
            return true;
        } catch (DateTimeParseException e1) {
            try {
                // 尝试解析为仅日期格式
                LocalDateTime.parse(timeStr + " 00:00:00", DATE_TIME_FORMATTER);
                return true;
            } catch (DateTimeParseException e2) {
                return false;
            }
        }
    }
    
    /**
     * 验证分组维度是否有效
     * 
     * @param groupBy 分组维度
     * @return 验证结果
     */
    public static boolean validateGroupBy(String groupBy) {
        Set<String> validGroupBy = new HashSet<>(Arrays.asList("changeType", "operationType", "objectApi", "syncStatus", "isCustom"));
        return validGroupBy.contains(groupBy);
    }
    
    /**
     * 验证时间维度是否有效
     * 
     * @param timeUnit 时间维度
     * @return 验证结果
     */
    public static boolean validateTimeUnit(String timeUnit) {
        Set<String> validTimeUnit = new HashSet<>(Arrays.asList("day", "week", "month", "quarter"));
        return validTimeUnit.contains(timeUnit);
    }
    
    /**
     * 验证日期范围是否有效
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 验证结果
     */
    public static boolean validateDateRange(String startTime, String endTime) {
        if (startTime == null || endTime == null) {
            return true;
        }
        
        if (!validateTimeFormat(startTime) || !validateTimeFormat(endTime)) {
            return false;
        }
        
        try {
            LocalDateTime startDateTime;
            LocalDateTime endDateTime;
            
            // 解析开始时间
            try {
                startDateTime = LocalDateTime.parse(startTime, DATE_TIME_FORMATTER);
            } catch (DateTimeParseException e) {
                startDateTime = LocalDateTime.parse(startTime + " 00:00:00", DATE_TIME_FORMATTER);
            }
            
            // 解析结束时间
            try {
                endDateTime = LocalDateTime.parse(endTime, DATE_TIME_FORMATTER);
            } catch (DateTimeParseException e) {
                endDateTime = LocalDateTime.parse(endTime + " 23:59:59", DATE_TIME_FORMATTER);
            }
            
            // 验证开始时间是否小于等于结束时间
            return startDateTime.isBefore(endDateTime) || startDateTime.isEqual(endDateTime);
        } catch (Exception e) {
            return false;
        }
    }
}