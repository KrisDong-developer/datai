package com.datai.salesforce.common.converter;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

/**
 * Salesforce特定数据类型转换工具
 */
public class SalesforceDataTypeConverter {
    private static final DateTimeFormatter SALESFORCE_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneOffset.UTC);
    private static final DateTimeFormatter SALESFORCE_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneOffset.UTC);
    
    private final ZoneId localTimeZone;

    /**
     * 默认构造函数，使用UTC时区
     */
    public SalesforceDataTypeConverter() {
        this(ZoneOffset.UTC);
    }

    /**
     * 构造函数，支持自定义时区
     *
     * @param localTimeZone 本地时区
     */
    public SalesforceDataTypeConverter(ZoneId localTimeZone) {
        this.localTimeZone = localTimeZone;
    }

    /**
     * 将Salesforce日期时间字符串转换为Date对象
     *
     * @param dateTimeString Salesforce日期时间字符串，格式：yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
     * @return Date对象
     */
    public Date convertToDate(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.isEmpty()) {
            return null;
        }
        DateTimeFormatter formatter = dateTimeString.contains("T") ? SALESFORCE_DATETIME_FORMATTER : SALESFORCE_DATE_FORMATTER;
        TemporalAccessor temporal = formatter.parse(dateTimeString);
        Instant instant;
        
        if (dateTimeString.contains("T")) {
            // 日期时间格式，直接转换为Instant
            instant = Instant.from(temporal);
        } else {
            // 日期格式，转换为LocalDate后加上当天的开始时间
            LocalDate localDate = LocalDate.from(temporal);
            instant = localDate.atStartOfDay(ZoneOffset.UTC).toInstant();
        }
        
        // 转换为本地时区的ZonedDateTime，然后转换为Date
        ZonedDateTime zonedDateTime = instant.atZone(localTimeZone);
        return Date.from(zonedDateTime.toInstant());
    }

    /**
     * 将Date对象转换为Salesforce日期时间字符串
     *
     * @param date Date对象
     * @return Salesforce日期时间字符串，格式：yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
     */
    public String convertToString(Date date) {
        if (date == null) {
            return null;
        }
        // 将Date转换为Instant，然后转换为本地时区的ZonedDateTime
        Instant instant = date.toInstant();
        ZonedDateTime zonedDateTime = instant.atZone(localTimeZone);
        // 转换为UTC时区的ZonedDateTime，然后格式化为字符串
        ZonedDateTime utcZonedDateTime = zonedDateTime.withZoneSameInstant(ZoneOffset.UTC);
        return SALESFORCE_DATETIME_FORMATTER.format(utcZonedDateTime);
    }

    /**
     * 将Date对象转换为Salesforce日期字符串
     *
     * @param date Date对象
     * @return Salesforce日期字符串，格式：yyyy-MM-dd
     */
    public String convertToDateString(Date date) {
        if (date == null) {
            return null;
        }
        // 将Date转换为Instant，然后转换为本地时区的ZonedDateTime
        Instant instant = date.toInstant();
        ZonedDateTime zonedDateTime = instant.atZone(localTimeZone);
        // 转换为UTC时区的ZonedDateTime，然后格式化为字符串
        ZonedDateTime utcZonedDateTime = zonedDateTime.withZoneSameInstant(ZoneOffset.UTC);
        return SALESFORCE_DATE_FORMATTER.format(utcZonedDateTime);
    }

    /**
     * 将Salesforce ID转换为15位格式
     *
     * @param salesforceId Salesforce ID（15位或18位）
     * @return 15位Salesforce ID
     */
    public String to15CharId(String salesforceId) {
        if (salesforceId == null) {
            return null;
        }
        if (salesforceId.length() == 15) {
            return salesforceId;
        }
        if (salesforceId.length() == 18) {
            return salesforceId.substring(0, 15);
        }
        throw new IllegalArgumentException("Invalid Salesforce ID length: " + salesforceId.length());
    }

    /**
     * 将Salesforce ID转换为18位格式
     *
     * @param salesforceId Salesforce ID（15位或18位）
     * @return 18位Salesforce ID
     */
    public String to18CharId(String salesforceId) {
        if (salesforceId == null) {
            return null;
        }
        if (salesforceId.length() == 18) {
            return salesforceId;
        }
        if (salesforceId.length() == 15) {
            return addChecksum(salesforceId);
        }
        throw new IllegalArgumentException("Invalid Salesforce ID length: " + salesforceId.length());
    }

    /**
     * 为15位Salesforce ID添加校验位，生成18位ID
     *
     * @param id15 15位Salesforce ID
     * @return 18位Salesforce ID
     */
    private String addChecksum(String id15) {
        StringBuilder suffix = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            int flags = 0;
            for (int j = 0; j < 5; j++) {
                char c = id15.charAt(i * 5 + j);
                if (c >= 'A' && c <= 'Z') {
                    flags |= 1 << j;
                }
            }
            suffix.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ012345".charAt(flags));
        }
        return id15 + suffix;
    }
    
    /**
     * 获取当前本地时区
     *
     * @return 当前本地时区
     */
    public ZoneId getLocalTimeZone() {
        return localTimeZone;
    }
}