package com.datai.integration.util;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.datai.salesforce.common.constant.SalesforceConstants;
import com.sforce.soap.partner.Field;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.bind.XmlObject;
import com.sforce.ws.types.Time;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.commons.lang3.time.FastTimeZone;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Salesforce到MySQL数据转换工具类
 * 
 * 主要功能：
 * 1. 将Salesforce的SObject对象转换为Map
 * 2. 处理各种数据类型的转换，确保与MySQL数据库兼容
 * 3. 过滤和转换复杂类型字段
 * 4. 字段类型映射（Salesforce -> MySQL）
 * 5. 日期时间处理和转换
 * 6. 时间范围计算
 * 7. 任务分割
 * 
 * 支持的类型转换：
 * - Calendar -> Date: 日期类型转换
 * - Boolean -> Integer(0/1): 布尔值转换为整数，兼容MySQL的TINYINT
 * - XmlObjectWrapper -> String: 复杂字段转换为字符串
 * - 其他类型 -> 保持原样
 */
@Component
@Slf4j
public class ConvertUtil {

    private static final String XML_OBJECT_WRAPPER_CLASS = "com.sforce.ws.bind.XmlObjectWrapper";

    private static final FastDateFormat SF_DATE_FORMAT = FastDateFormat.getInstance(SalesforceConstants.SF_DATE_FORMAT, FastTimeZone.getGmtTimeZone());
    private static final FastDateFormat SF_TIME_FORMAT = FastDateFormat.getInstance(SalesforceConstants.SF_TIME_FORMAT, FastTimeZone.getGmtTimeZone());
    private static final FastDateFormat SF_DATE_FORMAT_BULK = FastDateFormat.getInstance(SalesforceConstants.SF_DATE_FORMAT_BULK, FastTimeZone.getGmtTimeZone());

    private static final String MYSQL_DATE_FORMAT = "yyyy-MM-dd";
    private static final String MYSQL_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    private static final int MINUTE = 60;
    private static final int HOUR = 24;

    private ConvertUtil() {
    }




    /**
     * 添加ID字段到Map
     *
     * @param record    SObject对象
     * @param recordMap 目标Map
     */
    private static void addIdField(SObject record, Map<String, Object> recordMap) {
        if (record.getId() != null) {
            recordMap.put("Id", record.getId());
        }
    }


    /**
     * sObject 数组转 JSONArray
     *
     * @param records sObject 数组
     * @return JSONArray
     */
    public static JSONArray toJsonArray(SObject[] records, Field[] fields) throws ParseException {
        JSONArray jsonArray = new JSONArray();
        for (SObject sObject : records) {
            jsonArray.add(toJsonObject(sObject, fields));
        }
        return jsonArray;
    }


    /**
     * sObject 数组转 JSONObject
     *
     * @param sObject sObject
     * @param fields  字段
     * @return JSONObject
     */
    public static JSONObject toJsonObject(SObject sObject, Field[] fields) throws ParseException {
        Iterator<XmlObject> children = sObject.getChildren();
        JSONObject jsonObject = new JSONObject();
        CaseInsensitiveMap map = new CaseInsensitiveMap();
        for (Field field : fields) {
            map.put(field.getName(), field.getType().toString());
        }
        while (children.hasNext()) {
            XmlObject next = children.next();
            String name = next.getName().getLocalPart();
            Object value = next.getValue();
            // 全部当string处理
            if (next instanceof SObject) {
                SObject sObjectChild = (SObject) next;
                Iterator<XmlObject> children1 = sObjectChild.getChildren();
                while (children1.hasNext()) {
                    XmlObject childNext = children1.next();
                    String childName = childNext.getName().getLocalPart();
                    Object childValue = childNext.getValue();
                    // partner会多一个type 值为对象名称 这里过滤掉
                    if ("type".equals(childName)) {
                        continue;
                    }
                    if (childValue == null) {
                        continue;
                    }
                    jsonObject.put(name+"_"+childName, childValue);
                }
            }
            // partner会多一个type 值为对象名称 这里过滤掉
            if ("type".equals(name)) {
                continue;
            }
            if (value == null) {
                continue;
            }
            String type = (String) map.get(name);
            if (type == null) {
                continue;
            }
            switch (type) {
                case "date":
                    jsonObject.put(name, DateUtils.parseDate((String) value, "yyyy-MM-dd"));
                    break;
                case "datetime":
                    jsonObject.put(name, SF_DATE_FORMAT.parse((String) value));
                    break;
                case "time":
                    jsonObject.put(name, SF_TIME_FORMAT.parse((String) value));
                    break;
                case "boolean":
                    jsonObject.put(name, BooleanUtils.toBoolean((String) value));
                    break;
                case "long":
                case "int":
                    jsonObject.put(name, Integer.parseInt((String) value));
                    break;
                case "double":
                case "currency":
                case "percent":
                    jsonObject.put(name, Double.parseDouble((String) value));
                    break;
                case "base64":
                    // 文件不处理 统一存oss或者server
                    break;
                default:
                    if (value instanceof GregorianCalendar) {
                        jsonObject.put(name, DateFormatUtils.format(((GregorianCalendar) value), "yyyy-MM-dd HH:mm:ss"));
                    } else if (value instanceof Time){
                        jsonObject.put(name, DateFormatUtils.format((((Time) value).getTimeInMillis()), "HH:mm:ss"));
                    }else {
                        jsonObject.put(name,  String.valueOf(value));
                    }
                    break;
            }
        }
        return jsonObject;
    }

    /**
     * 转换Calendar为Date
     *
     * @param calendar Calendar对象
     * @return Date对象
     */
    private static Date convertCalendarToDate(Calendar calendar) {
        return calendar.getTime();
    }



    /**
     * 转换Boolean为Integer
     *
     * @param bool 布尔值
     * @return 整数（true=1, false=0）
     */
    private static Integer convertBooleanToInteger(Boolean bool) {
        return bool ? 1 : 0;
    }

    /**
     * 判断是否为XmlObjectWrapper类型
     *
     * @param value 对象
     * @return true如果是XmlObjectWrapper类型
     */
    private static boolean isXmlObjectWrapper(Object value) {
        return XML_OBJECT_WRAPPER_CLASS.equals(value.getClass().getName());
    }

    /**
     * 转换XmlObjectWrapper为String
     *
     * @param field 字段名
     * @param value XmlObjectWrapper对象
     * @return 字符串表示
     */
    private static String convertXmlObjectWrapperToString(String field, Object value) {
        log.warn("跳过复杂字段 {}，类型为 XmlObjectWrapper，转换为字符串", field);
        return String.valueOf(value);
    }



    /**
     * 检查字段是否为复杂类型
     *
     * @param value 字段值
     * @return true如果是复杂类型
     */
    public static boolean isComplexType(Object value) {
        if (value == null) {
            return false;
        }
        return isXmlObjectWrapper(value);
    }

    /**
     * 检查字段是否需要特殊转换
     *
     * @param value 字段值
     * @return true如果需要转换
     */
    public static boolean needsConversion(Object value) {
        if (value == null) {
            return false;
        }
        return value instanceof java.util.Calendar 
            || value instanceof Boolean 
            || value instanceof Time
            || isComplexType(value);
    }

    /**
     * 将Salesforce字段类型转换为MySQL字段类型
     *
     * @param field Salesforce字段对象
     * @return MySQL字段类型
     */
    public static String fieldTypeToMysql(Field field) {
        String type = field.getType().toString();
        int length = field.getLength();
        String result = type;

        switch (type) {
            case "id":
            case "reference":
                result = "varchar(" + length + ")";
                break;
            case "string":
                if ("Name".equalsIgnoreCase(field.getName())) {
                    result = "varchar(" + length + ")";
                } else {
                    result = "text";
                }
                break;
            case "url":
            case "email":
            case "phone":
            case "picklist":
            case "textarea":
            case "multipicklist":
            case "address":
            case "combobox":
            case "location":
            case "encryptedstring":
            case "anyType":
                result = "text";
                break;
            case "date":
                result = "date";
                break;
            case "datetime":
                result = "datetime";
                break;
            case "boolean":
                result = "tinyint(1)";
                break;
            case "time":
                result = "time(3)";
                break;
            case "long":
            case "int":
                result = "int(" + length + ")";
                break;
            case "double":
            case "currency":
            case "percent":
                if (length > 4) {
                    result = "double(" + length + ")";
                } else {
                    result = "decimal(40,20)";
                }
                break;
            case "base64":
                result = "longblob";
                break;
            default:
                result = "text";
                break;
        }
        return result;
    }

    /**
     * 计算两个时间差
     *
     * @param date1 日期1
     * @param date2 日期2
     * @return 返回日期格式 xxdxxhxxmin
     */
    public static String calTime(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return null;
        }
        long min = Math.abs(date2.getTime() - date1.getTime()) / 1000 / 60;
        StringBuilder stringBuilder = new StringBuilder();
        if (min >= MINUTE) {
            long h = min / MINUTE;
            long realMin = min % MINUTE;
            if (h >= HOUR) {
                stringBuilder.append(h / HOUR).append("d");
                long realH = h % HOUR;
                if (realH > 0) {
                    stringBuilder.append(realH).append("h");
                }
            } else {
                stringBuilder.append(h).append("h");
            }
            if (realMin > 0) {
                stringBuilder.append(realMin).append("min");
            }
        } else {
            stringBuilder.append(min).append("min");
        }
        return stringBuilder.toString();
    }

    /**
     * 返回本周最后一天 如果大于结束日期 返回结束日期
     *
     * @param endDate   结束日期
     * @param startDate 开始日期
     * @return 本周最后一天
     */
    public static Date getWeekLastDay(Date endDate, Date startDate) {
        Date weekLastDay;
        Calendar cal = DateUtils.toCalendar(startDate);
        cal.set(Calendar.DAY_OF_WEEK, cal.getActualMaximum(Calendar.DAY_OF_WEEK));
        cal.add(Calendar.DAY_OF_WEEK, 1);
        weekLastDay = cal.getTime();
        if (weekLastDay.compareTo(endDate) > 0) {
            weekLastDay = endDate;
        }
        return weekLastDay;
    }

    /**
     * 返回本月最后一天 如果大于结束日期 返回结束日期
     *
     * @param endDate   结束日期
     * @param startDate 开始日期
     * @return 本月最后一天
     */
    public static Date getMonthLastDay(Date endDate, Date startDate) {
        Date monthLastDay;
        Calendar cal = DateUtils.toCalendar(startDate);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.add(Calendar.DAY_OF_MONTH, 1);
        monthLastDay = cal.getTime();
        if (monthLastDay.compareTo(endDate) > 0) {
            monthLastDay = endDate;
        }
        return monthLastDay;
    }

    /**
     * 返回本年最后一天 如果大于结束日期 返回结束日期
     *
     * @param endDate   结束日期
     * @param startDate 开始日期
     * @return 本年最后一天
     */
    public static Date getYearLastDay(Date endDate, Date startDate) {
        Date yearLastDay;
        startDate = DateUtils.addYears(startDate, 1);
        Calendar cal = DateUtils.toCalendar(startDate);
        cal.set(Calendar.YEAR, cal.get(Calendar.YEAR));
        yearLastDay = cal.getTime();
        if (yearLastDay.compareTo(endDate) > 0) {
            yearLastDay = endDate;
        }
        return yearLastDay;
    }

    /**
     * 根据字段类型将值转换为MySQL类型并返回
     *
     * @param type  字段类型
     * @param value 字段值
     * @return 转换后的值
     * @throws ParseException 日期解析异常
     */
    public static Object convertMysqlValue(String type, Object value) throws ParseException {
        return convertMysqlValueInternal(type, value, false);
    }

    /**
     * 根据字段类型将值转换为MySQL类型并返回（Bulk API专用）
     *
     * @param type  字段类型
     * @param value 字段值
     * @return 转换后的值
     * @throws ParseException 日期解析异常
     */
    public static Object convertMysqlBulkValue(String type, Object value) throws ParseException {
        return convertMysqlValueInternal(type, value, true);
    }

    private static Object convertMysqlValueInternal(String type, Object value, boolean isBulk) throws ParseException {
        if (value == null) {
            return null;
        }

        String strValue = value instanceof String ? (String) value : null;

        switch (type) {
            case "date":
                if (StringUtils.isEmpty(strValue)) {
                    return null;
                }
                return DateUtils.parseDate(strValue, MYSQL_DATE_FORMAT);
            case "datetime":
                if (StringUtils.isEmpty(strValue)) {
                    return null;
                }
                if (isBulk) {
                    return SF_DATE_FORMAT_BULK.parse(strValue);
                } else {
                    return SF_DATE_FORMAT.parse(strValue);
                }
            case "time":
                if (StringUtils.isEmpty(strValue)) {
                    return null;
                }
                return SF_TIME_FORMAT.parse(strValue);
            case "boolean":
                if (StringUtils.isEmpty(strValue)) {
                    return false;
                }
                return Boolean.parseBoolean(strValue);
            case "long":
            case "int":
                if (StringUtils.isEmpty(strValue)) {
                    return 0;
                }
                return Integer.parseInt(strValue);
            case "double":
            case "currency":
            case "percent":
                if (StringUtils.isEmpty(strValue)) {
                    return 0.0;
                }
                return Double.parseDouble(strValue);
            case "base64":
                return null;
            default:
                if (value instanceof GregorianCalendar) {
                    return DateFormatUtils.format(((GregorianCalendar) value), "yyyy-MM-dd HH:mm:ss");
                } else if (value instanceof Time) {
                    return DateFormatUtils.format((((Time) value).getTimeInMillis()), "HH:mm:ss");
                } else {
                    return String.valueOf(value);
                }
        }
    }

    /**
     * 将本地数据转换为Salesforce数据（Partner API）
     *
     * @param fieldType 字段类型
     * @param data      数据值
     * @return 转换后的值
     * @throws ParseException 解析异常
     */
    public static Object localDataToSfData(String fieldType, String data) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date;
        Calendar calendar = Calendar.getInstance();

        switch (fieldType) {
            case "int":
                return Integer.parseInt(data);
            case "double":
            case "currency":
            case "percent":
                return new BigDecimal(data);
            case "boolean":
                return Boolean.parseBoolean(data);
            case "date":
                try {
                    date = sd.parse(data);
                    calendar.setTime(date);
                    return calendar;
                } catch (ParseException e) {
                    log.error("日期解析失败: {}", data, e);
                    return null;
                }
            case "datetime":
                try {
                    date = sd.parse(data);
                    calendar.setTime(date);
                    return calendar;
                } catch (ParseException e) {
                    log.error("日期时间解析失败: {}", data, e);
                    return null;
                }
            case "time":
                return data;
            default:
                return data;
        }
    }

    /**
     * 将本地数据转换为Salesforce数据（Bulk API）
     *
     * @param fieldType 字段类型
     * @param data      数据值
     * @return 转换后的值
     * @throws ParseException 解析异常
     */
    public static Object localBulkDataToSfData(String fieldType, String data) throws ParseException {
        switch (fieldType) {
            case "int":
                return Integer.parseInt(data);
            case "double":
            case "currency":
            case "percent":
                return new BigDecimal(data);
            case "boolean":
                return Boolean.parseBoolean(data);
            case "date":
                return data + "T08:00:00Z";
            case "datetime":
                LocalDateTime localDateTime = LocalDateTime.parse(data);
                LocalDateTime plusEightHours = localDateTime.plusHours(-8);
                OffsetDateTime offsetDateTime = plusEightHours.atOffset(ZoneOffset.ofHours(0));
                return offsetDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            case "time":
                return adjustHour(data);
            default:
                return data;
        }
    }

    /**
     * 调整时间格式
     *
     * @param time 时间字符串
     * @return 调整后的时间字符串
     */
    private static String adjustHour(String time) {
        if (StringUtils.isBlank(time)) {
            return time;
        }
        return time;
    }



    /**
     * 格式化日期时间为MySQL格式
     *
     * @param date 日期对象
     * @return MySQL格式日期时间字符串
     */
    public static String formatDateToMysql(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(MYSQL_DATETIME_FORMAT);
        return sdf.format(date);
    }


    /**
     * 解析MySQL日期字符串
     *
     * @param dateStr MySQL日期字符串
     * @return Date对象
     * @throws ParseException 解析异常
     */
    public static Date parseMysqlDate(String dateStr) throws ParseException {
        if (StringUtils.isBlank(dateStr)) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(MYSQL_DATETIME_FORMAT);
        return sdf.parse(dateStr);
    }

    /**
     * 获取当天的开始时间（00:00:00）
     *
     * @param date 日期对象
     * @return 当天开始时间
     */
    public static Date getStartOfDay(Date date) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取当天的结束时间（23:59:59）
     *
     * @param date 日期对象
     * @return 当天结束时间
     */
    public static Date getEndOfDay(Date date) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    /**
     * 判断日期是否在指定范围内
     *
     * @param date   待判断的日期
     * @param start  开始日期
     * @param end    结束日期
     * @return true如果在范围内
     */
    public static boolean isDateInRange(Date date, Date start, Date end) {
        if (date == null) {
            return false;
        }
        if (start != null && date.before(start)) {
            return false;
        }
        if (end != null && date.after(end)) {
            return false;
        }
        return true;
    }

    /**
     * 添加天数到日期
     *
     * @param date 日期对象
     * @param days 天数
     * @return 新日期
     */
    public static Date addDays(Date date, int days) {
        if (date == null) {
            return null;
        }
        return DateUtils.addDays(date, days);
    }

    /**
     * 添加小时到日期
     *
     * @param date  日期对象
     * @param hours 小时数
     * @return 新日期
     */
    public static Date addHours(Date date, int hours) {
        if (date == null) {
            return null;
        }
        return DateUtils.addHours(date, hours);
    }

    /**
     * 添加分钟到日期
     *
     * @param date    日期对象
     * @param minutes 分钟数
     * @return 新日期
     */
    public static Date addMinutes(Date date, int minutes) {
        if (date == null) {
            return null;
        }
        return DateUtils.addMinutes(date, minutes);
    }

    /**
     * 比较两个日期是否是同一天
     *
     * @param date1 日期1
     * @param date2 日期2
     * @return true如果是同一天
     */
    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return DateUtils.isSameDay(date1, date2);
    }

    /**
     * 截断日期到指定精度
     *
     * @param date   日期对象
     * @param field  精度字段（Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY等）
     * @return 截断后的日期
     */
    public static Date truncate(Date date, int field) {
        if (date == null) {
            return null;
        }
        return DateUtils.truncate(date, field);
    }
}
