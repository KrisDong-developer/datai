package com.datai.salesforce.common.utils;

import com.datai.salesforce.common.constant.SalesforceConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Salesforce SOQL语句生成工具类
 * 
 * 主要功能：
 * 1. 根据MyBatis的Mapper方法生成完整的SQL语句
 * 2. 将SQL中的占位符替换为实际的参数值
 * 3. 根据参数类型自动格式化参数值
 * 4. 提供SQL格式化和美化功能
 * 
 * 支持的参数类型：
 * - 基本类型及其包装类
 * - 字符串（自动转义）
 * - 日期类型（Date, Calendar, LocalDateTime, LocalDate, LocalTime）
 * - 集合类型（数组, List, Set）
 * - 自定义对象
 */
@Component
@Slf4j
public class SoqlUtil {

    private static SqlSessionFactory sqlSessionFactory = null;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(SalesforceConstants.SF_DATE_FORMAT);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(SalesforceConstants.SF_DATE_ONLY_FORMAT);

    private static final Pattern SQL_KEYWORD_PATTERN = Pattern.compile("\\b(SELECT|FROM|WHERE|AND|OR|ORDER BY|GROUP BY|HAVING|LIMIT|OFFSET|IN|NOT IN|LIKE|IS NULL|IS NOT NULL|BETWEEN)\\b", Pattern.CASE_INSENSITIVE);

    @org.springframework.beans.factory.annotation.Autowired
    private void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        SoqlUtil.sqlSessionFactory = sqlSessionFactory;
        log.debug("SoqlUtil初始化成功，SqlSessionFactory已注入");
    }

    /**
     * 生成Salesforce执行SQL语句
     *
     * @param method 方法全名称，例如：com.celnet.xxxx.select
     * @param param  参数对象，没有则传null
     * @return 完整的SQL语句
     * @throws IllegalArgumentException 如果method为空或无效
     */
    public static String showSql(String method, Map<String, Object> param) {
        if (StringUtils.isBlank(method)) {
            throw new IllegalArgumentException("方法名称不能为空");
        }

        try {
            Configuration configuration = sqlSessionFactory.getConfiguration();
            if (configuration == null) {
                throw new IllegalStateException("SqlSessionFactory未正确初始化");
            }

            BoundSql boundSql = configuration.getMappedStatement(method).getBoundSql(param);
            return showSql(configuration, boundSql);
        } catch (Exception e) {
            log.error("生成SQL语句失败，方法: {}, 参数: {}", method, param, e);
            throw new RuntimeException("生成SQL语句失败: " + e.getMessage(), e);
        }
    }

    /**
     * 解析BoundSql，生成不含占位符的SQL语句
     *
     * @param configuration MyBatis配置对象
     * @param boundSql      MyBatis BoundSql对象
     * @return 完整的SQL语句
     */
    private static String showSql(Configuration configuration, BoundSql boundSql) {
        if (configuration == null || boundSql == null) {
            throw new IllegalArgumentException("Configuration和BoundSql不能为空");
        }

        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ").trim();
        
        if (parameterMappings.isEmpty() || parameterObject == null) {
            log.debug("SQL语句无参数或参数对象为空");
            return formatSql(sql);
        }

        try {
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                sql = replaceFirstPlaceholder(sql, getParameterValue(parameterObject));
            } else {
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                for (ParameterMapping parameterMapping : parameterMappings) {
                    String propertyName = parameterMapping.getProperty();
                    Object obj = null;
                    
                    if (metaObject.hasGetter(propertyName)) {
                        obj = metaObject.getValue(propertyName);
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        obj = boundSql.getAdditionalParameter(propertyName);
                    }
                    
                    sql = replaceFirstPlaceholder(sql, getParameterValue(obj));
                }
            }
            
            return formatSql(sql);
        } catch (Exception e) {
            log.error("解析BoundSql失败", e);
            throw new RuntimeException("解析BoundSql失败: " + e.getMessage(), e);
        }
    }

    /**
     * 替换SQL中的第一个占位符
     *
     * @param sql      SQL语句
     * @param value    替换值
     * @return 替换后的SQL语句
     */
    private static String replaceFirstPlaceholder(String sql, String value) {
        if (sql == null) {
            return null;
        }
        int index = sql.indexOf('?');
        if (index == -1) {
            return sql;
        }
        return sql.substring(0, index) + value + sql.substring(index + 1);
    }

    /**
     * 格式化参数值，根据参数类型进行相应的处理
     *
     * @param obj 参数对象
     * @return 格式化后的字符串
     */
    private static String getParameterValue(Object obj) {
        if (obj == null) {
            return "NULL";
        }

        try {
            if (obj instanceof String) {
                return escapeSqlString((String) obj);
            } else if (obj instanceof Date) {
                return formatDateTime((Date) obj);
            } else if (obj instanceof Calendar) {
                return formatDateTime(((Calendar) obj).getTime());
            } else if (obj instanceof LocalDateTime) {
                return formatLocalDateTime((LocalDateTime) obj);
            } else if (obj instanceof LocalDate) {
                return formatLocalDate((LocalDate) obj);
            } else if (obj instanceof LocalTime) {
                return formatLocalTime((LocalTime) obj);
            } else if (obj instanceof Boolean) {
                return (Boolean) obj ? "TRUE" : "FALSE";
            } else if (obj.getClass().isArray()) {
                return formatArray(obj);
            } else if (obj instanceof Collection) {
                return formatCollection((Collection<?>) obj);
            } else {
                return obj.toString();
            }
        } catch (Exception e) {
            log.error("格式化参数值失败，参数类型: {}, 值: {}", obj.getClass().getName(), obj, e);
            return "NULL";
        }
    }

    /**
     * 转义SQL字符串，防止SQL注入
     *
     * @param str 原始字符串
     * @return 转义后的字符串
     */
    private static String escapeSqlString(String str) {
        if (str == null) {
            return "NULL";
        }
        return "'" + str.replace("'", "\\'") + "'";
    }

    /**
     * 格式化Date类型为Salesforce日期时间格式
     *
     * @param date 日期对象
     * @return 格式化后的字符串
     */
    private static String formatDateTime(Date date) {
        return "'" + DateFormatUtils.formatUTC(date, SalesforceConstants.SF_DATE_FORMAT) + "'";
    }

    /**
     * 格式化LocalDateTime为Salesforce日期时间格式
     *
     * @param dateTime 日期时间对象
     * @return 格式化后的字符串
     */
    private static String formatLocalDateTime(LocalDateTime dateTime) {
        return "'" + dateTime.format(DATE_TIME_FORMATTER) + "'";
    }

    /**
     * 格式化LocalDate为Salesforce日期格式
     *
     * @param date 日期对象
     * @return 格式化后的字符串
     */
    private static String formatLocalDate(LocalDate date) {
        return "'" + date.format(DATE_FORMATTER) + "'";
    }

    /**
     * 格式化LocalTime
     *
     * @param time 时间对象
     * @return 格式化后的字符串
     */
    private static String formatLocalTime(LocalTime time) {
        return "'" + time.toString() + "'";
    }

    /**
     * 格式化数组为IN子句格式
     *
     * @param array 数组对象
     * @return 格式化后的字符串
     */
    private static String formatArray(Object array) {
        if (array == null) {
            return "NULL";
        }
        
        int length = java.lang.reflect.Array.getLength(array);
        if (length == 0) {
            return "()";
        }
        
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            Object element = java.lang.reflect.Array.get(array, i);
            sb.append(getParameterValue(element));
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * 格式化集合为IN子句格式
     *
     * @param collection 集合对象
     * @return 格式化后的字符串
     */
    private static String formatCollection(Collection<?> collection) {
        if (collection == null) {
            return "NULL";
        }
        
        if (collection.isEmpty()) {
            return "()";
        }
        
        StringBuilder sb = new StringBuilder("(");
        boolean first = true;
        for (Object element : collection) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(getParameterValue(element));
            first = false;
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * 格式化SQL语句，使其更易读
     *
     * @param sql 原始SQL语句
     * @return 格式化后的SQL语句
     */
    private static String formatSql(String sql) {
        if (StringUtils.isBlank(sql)) {
            return sql;
        }

        Matcher matcher = SQL_KEYWORD_PATTERN.matcher(sql);
        StringBuffer sb = new StringBuffer();
        
        while (matcher.find()) {
            matcher.appendReplacement(sb, "\n" + matcher.group().toUpperCase());
        }
        matcher.appendTail(sb);
        
        return sb.toString().trim();
    }

    /**
     * 获取SqlSessionFactory实例
     *
     * @return SqlSessionFactory实例
     */
    public static SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }

    /**
     * 检查工具类是否已初始化
     *
     * @return true如果已初始化，false否则
     */
    public static boolean isInitialized() {
        return sqlSessionFactory != null;
    }
}
