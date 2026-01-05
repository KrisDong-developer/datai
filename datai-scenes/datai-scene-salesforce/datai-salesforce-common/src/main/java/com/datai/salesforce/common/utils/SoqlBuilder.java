package com.datai.salesforce.common.utils;

import com.datai.salesforce.common.constant.SalesforceConstants;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * salesforce SOQL查询构建器，支持复杂查询条件
 * 
 * SOQL语法限制：
 * 1. 不支持SELECT *，必须明确指定字段或使用 FIELDS()
 * 2. 不支持BETWEEN运算符，请使用 >= 和 <= 组合代替
 * 3. 不支持JOIN，使用关系查询（Relationship Queries）如 Account.Name
 * 4. 不支持UNION，无法合并两个不相关的结果集
 * 5. 查询最多返回50000条记录
 * 6. OFFSET上限为2000，且必须与LIMIT一起使用
 * 7. 支持的运算符：=, !=, >, >=, <, <=, LIKE, IN, NOT IN, = NULL, != NULL
 * 8. 支持的子句：WHERE, GROUP BY, HAVING, ORDER BY, LIMIT, OFFSET
 */
public class SoqlBuilder {
    private List<String> selectFields = new ArrayList<>();
    private String fromObject;
    private List<String> whereClauses = new ArrayList<>();
    private List<String> andClauses = new ArrayList<>();
    private List<String> orClauses = new ArrayList<>();
    private List<String> groupByFields = new ArrayList<>();
    private List<String> havingClauses = new ArrayList<>();
    private List<OrderByClause> orderByClauses = new ArrayList<>();
    private Integer limit;
    private Integer offset;

    /**
     * 设置SELECT子句
     * 注意：SOQL不支持SELECT *，必须明确指定查询字段
     *
     * @param fields 要查询的字段
     * @return 当前构建器实例
     * @throws IllegalArgumentException 如果fields为null或空
     */
    public SoqlBuilder select(String... fields) {
        if (fields == null || fields.length == 0) {
            throw new IllegalArgumentException("SOQL不支持SELECT *，必须明确指定查询字段");
        }
        
        for (String field : fields) {
            if (field != null && !field.trim().isEmpty()) {
                selectFields.add(field.trim());
            }
        }
        
        if (selectFields.isEmpty()) {
            throw new IllegalArgumentException("SELECT子句不能为空，必须指定至少一个字段");
        }
        
        return this;
    }

    /**
     * 设置FROM子句
     *
     * @param object 对象名称
     * @return 当前构建器实例
     */
    public SoqlBuilder from(String object) {
        this.fromObject = object;
        return this;
    }

    /**
     * 添加WHERE子句
     *
     * @param clause WHERE条件
     * @return 当前构建器实例
     * @throws IllegalArgumentException 如果条件为空
     */
    public SoqlBuilder where(String clause) {
        if (clause != null && !clause.trim().isEmpty()) {
            whereClauses.add(clause.trim());
        } else {
            throw new IllegalArgumentException("WHERE条件不能为空");
        }
        return this;
    }

    /**
     * 添加AND子句
     *
     * @param clause AND条件
     * @return 当前构建器实例
     * @throws IllegalArgumentException 如果条件为空
     */
    public SoqlBuilder and(String clause) {
        if (clause != null && !clause.trim().isEmpty()) {
            andClauses.add(clause.trim());
        } else {
            throw new IllegalArgumentException("AND条件不能为空");
        }
        return this;
    }

    /**
     * 添加OR子句
     *
     * @param clause OR条件
     * @return 当前构建器实例
     * @throws IllegalArgumentException 如果条件为空
     */
    public SoqlBuilder or(String clause) {
        if (clause != null && !clause.trim().isEmpty()) {
            orClauses.add(clause.trim());
        } else {
            throw new IllegalArgumentException("OR条件不能为空");
        }
        return this;
    }
    
    /**
     * 添加等于条件 (=)
     * 注意：当value为null时，会生成"= NULL"条件
     *
     * @param field 字段名
     * @param value 值
     * @return 当前构建器实例
     * @throws IllegalArgumentException 如果参数无效
     */
    public SoqlBuilder whereEq(String field, Object value) {
        if (field == null || field.trim().isEmpty()) {
            throw new IllegalArgumentException("字段名不能为空");
        }
        
        if (value == null) {
            return where(field.trim() + " = NULL");
        }
        
        String formattedValue = formatValue(value);
        return where(field.trim() + " = " + formattedValue);
    }
    
    /**
     * 添加不等于条件 (!=)
     * 注意：当value为null时，会生成"!= NULL"条件
     *
     * @param field 字段名
     * @param value 值
     * @return 当前构建器实例
     * @throws IllegalArgumentException 如果参数无效
     */
    public SoqlBuilder whereNe(String field, Object value) {
        if (field == null || field.trim().isEmpty()) {
            throw new IllegalArgumentException("字段名不能为空");
        }
        
        if (value == null) {
            return where(field.trim() + " != NULL");
        }
        
        String formattedValue = formatValue(value);
        return where(field.trim() + " != " + formattedValue);
    }
    
    /**
     * 添加大于条件 (>)
     *
     * @param field 字段名
     * @param value 值
     * @return 当前构建器实例
     * @throws IllegalArgumentException 如果参数无效
     */
    public SoqlBuilder whereGt(String field, Object value) {
        if (field == null || field.trim().isEmpty()) {
            throw new IllegalArgumentException("字段名不能为空");
        }
        
        if (value == null) {
            throw new IllegalArgumentException("大于条件的值不能为null");
        }
        
        String formattedValue = formatValue(value);
        return where(field.trim() + " > " + formattedValue);
    }
    
    /**
     * 添加大于等于条件 (>=)
     *
     * @param field 字段名
     * @param value 值
     * @return 当前构建器实例
     * @throws IllegalArgumentException 如果参数无效
     */
    public SoqlBuilder whereGe(String field, Object value) {
        if (field == null || field.trim().isEmpty()) {
            throw new IllegalArgumentException("字段名不能为空");
        }
        
        if (value == null) {
            throw new IllegalArgumentException("大于等于条件的值不能为null");
        }
        
        String formattedValue = formatValue(value);
        return where(field.trim() + " >= " + formattedValue);
    }
    
    /**
     * 添加小于条件 (<)
     *
     * @param field 字段名
     * @param value 值
     * @return 当前构建器实例
     * @throws IllegalArgumentException 如果参数无效
     */
    public SoqlBuilder whereLt(String field, Object value) {
        if (field == null || field.trim().isEmpty()) {
            throw new IllegalArgumentException("字段名不能为空");
        }
        
        if (value == null) {
            throw new IllegalArgumentException("小于条件的值不能为null");
        }
        
        String formattedValue = formatValue(value);
        return where(field.trim() + " < " + formattedValue);
    }
    
    /**
     * 添加小于等于条件 (<=)
     *
     * @param field 字段名
     * @param value 值
     * @return 当前构建器实例
     * @throws IllegalArgumentException 如果参数无效
     */
    public SoqlBuilder whereLe(String field, Object value) {
        if (field == null || field.trim().isEmpty()) {
            throw new IllegalArgumentException("字段名不能为空");
        }
        
        if (value == null) {
            throw new IllegalArgumentException("小于等于条件的值不能为null");
        }
        
        String formattedValue = formatValue(value);
        return where(field.trim() + " <= " + formattedValue);
    }
    
    /**
     * 格式化值，根据类型进行相应的处理
     * 支持的数据类型：
     * - null: 返回"NULL"字符串
     * - String: 用单引号包裹，转义单引号
     * - Date/LocalDateTime: 格式化为ISO 8601格式，用单引号包裹
     * - Boolean: 转换为TRUE或FALSE（不用引号）
     * - Integer/Long/Short/Byte: 数字类型，直接转换
     * - Float/Double/BigDecimal: 浮点数类型，直接转换
     * - BigInteger: 大整数，直接转换
     * - Enum: 使用枚举的name()方法
     * - Character: 用单引号包裹
     * - 其他类型: 调用toString()
     *
     * @param value 值对象
     * @return 格式化后的字符串
     */
    private String formatValue(Object value) {
        if (value == null) {
            return "NULL";
        }
        
        if (value instanceof String) {
            String strValue = ((String) value).trim();
            if (strValue.isEmpty()) {
                throw new IllegalArgumentException("字符串值不能为空");
            }
            return "'" + escapeString(strValue) + "'";
        } else if (value instanceof Date) {
            return DateFormatUtils.formatUTC((Date) value, SalesforceConstants.SF_DATE_FORMAT) ;
        } else if (value instanceof LocalDateTime) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(SalesforceConstants.SF_DATE_FORMAT);
            return  ((LocalDateTime) value).format(formatter) ;
        } else if (value instanceof Boolean) {
            return (Boolean) value ? "TRUE" : "FALSE";
        } else if (value instanceof Integer || value instanceof Long || value instanceof Short || value instanceof Byte) {
            return value.toString();
        } else if (value instanceof Float || value instanceof Double || value instanceof BigDecimal) {
            return value.toString();
        } else if (value instanceof BigInteger) {
            return value.toString();
        } else if (value instanceof Enum) {
            return "'" + ((Enum<?>) value).name() + "'";
        } else if (value instanceof Character) {
            return "'" + escapeString(value.toString()) + "'";
        } else {
            return value.toString();
        }
    }

    /**
     * 添加IN条件
     *
     * @param field 字段名
     * @param values 值列表
     * @return 当前构建器实例
     * @throws IllegalArgumentException 如果参数无效
     */
    public SoqlBuilder whereIn(String field, List<Object> values) {
        if (field == null || field.trim().isEmpty()) {
            throw new IllegalArgumentException("字段名不能为空");
        }
        
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("IN条件的值列表不能为空");
        }
        
        StringBuilder clause = new StringBuilder(field.trim()).append(" IN (");
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                clause.append(", ");
            }
            Object value = values.get(i);
            clause.append(formatValue(value));
        }
        clause.append(")");
        
        return where(clause.toString());
    }

    /**
     * 添加IN条件（可变参数）
     *
     * @param field 字段名
     * @param values 值列表
     * @return 当前构建器实例
     */
    public SoqlBuilder whereIn(String field, Object... values) {
        return whereIn(field, Arrays.asList(values));
    }

    /**
     * 添加NOT IN条件
     *
     * @param field 字段名
     * @param values 值列表
     * @return 当前构建器实例
     * @throws IllegalArgumentException 如果参数无效
     */
    public SoqlBuilder whereNotIn(String field, List<Object> values) {
        if (field == null || field.trim().isEmpty()) {
            throw new IllegalArgumentException("字段名不能为空");
        }
        
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("NOT IN条件的值列表不能为空");
        }
        
        StringBuilder clause = new StringBuilder(field.trim()).append(" NOT IN (");
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                clause.append(", ");
            }
            Object value = values.get(i);
            clause.append(formatValue(value));
        }
        clause.append(")");
        
        return where(clause.toString());
    }

    /**
     * 添加LIKE条件
     * 注意：LIKE模式中的通配符（%, _）需要用户自行处理
     *
     * @param field 字段名
     * @param value 匹配值
     * @return 当前构建器实例
     * @throws IllegalArgumentException 如果参数无效
     */
    public SoqlBuilder whereLike(String field, String value) {
        if (field == null || field.trim().isEmpty()) {
            throw new IllegalArgumentException("字段名不能为空");
        }
        
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("LIKE条件的值不能为空");
        }
        
        return where(field.trim() + " LIKE '%" + escapeString(value.trim()) + "%'");
    }

    /**
     * 添加NOT LIKE条件
     * 注意：LIKE模式中的通配符（%, _）需要用户自行处理
     *
     * @param field 字段名
     * @param value 匹配值
     * @return 当前构建器实例
     * @throws IllegalArgumentException 如果参数无效
     */
    public SoqlBuilder whereNotLike(String field, String value) {
        if (field == null || field.trim().isEmpty()) {
            throw new IllegalArgumentException("字段名不能为空");
        }
        
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("NOT LIKE条件的值不能为空");
        }
        
        return where(field.trim() + " NOT LIKE '%" + escapeString(value.trim()) + "%'");
    }

    /**
     * 添加= NULL条件
     * 注意：SOQL使用= NULL而不是IS NULL
     *
     * @param field 字段名
     * @return 当前构建器实例
     * @throws IllegalArgumentException 如果字段名为空
     */
    public SoqlBuilder whereIsNull(String field) {
        if (field == null || field.trim().isEmpty()) {
            throw new IllegalArgumentException("字段名不能为空");
        }
        return where(field.trim() + " = NULL");
    }

    /**
     * 添加!= NULL条件
     * 注意：SOQL使用!= NULL而不是IS NOT NULL
     *
     * @param field 字段名
     * @return 当前构建器实例
     * @throws IllegalArgumentException 如果字段名为空
     */
    public SoqlBuilder whereIsNotNull(String field) {
        if (field == null || field.trim().isEmpty()) {
            throw new IllegalArgumentException("字段名不能为空");
        }
        return where(field.trim() + " != NULL");
    }

    
    /**
     * 转义字符串中的单引号，防止SQL注入
     * SOQL中使用两个单引号来转义一个单引号
     *
     * @param value 要转义的字符串
     * @return 转义后的字符串
     */
    private String escapeString(String value) {
        return value.replace("'", "''");
    }

    /**
     * 添加ORDER BY子句
     * SOQL支持ORDER BY，可以指定排序方向
     *
     * @param field     排序字段
     * @param direction 排序方向
     * @return 当前构建器实例
     * @throws IllegalArgumentException 如果参数无效
     */
    public SoqlBuilder orderBy(String field, SortOrder direction) {
        if (field == null || field.trim().isEmpty()) {
            throw new IllegalArgumentException("排序字段不能为空");
        }
        
        if (direction == null) {
            throw new IllegalArgumentException("排序方向不能为null");
        }
        
        this.orderByClauses.add(new OrderByClause(field.trim(), direction));
        return this;
    }

    /**
     * 添加GROUP BY子句
     * SOQL支持GROUP BY，用于分组统计
     *
     * @param fields 分组字段
     * @return 当前构建器实例
     * @throws IllegalArgumentException 如果参数无效
     */
    public SoqlBuilder groupBy(String... fields) {
        if (fields == null || fields.length == 0) {
            throw new IllegalArgumentException("GROUP BY字段不能为空");
        }
        
        for (String field : fields) {
            if (field != null && !field.trim().isEmpty()) {
                groupByFields.add(field.trim());
            }
        }
        
        if (groupByFields.isEmpty()) {
            throw new IllegalArgumentException("GROUP BY字段不能为空");
        }
        
        return this;
    }

    /**
     * 添加HAVING子句
     * SOQL支持HAVING，用于过滤分组结果
     *
     * @param clause HAVING条件
     * @return 当前构建器实例
     * @throws IllegalArgumentException 如果条件为空
     */
    public SoqlBuilder having(String clause) {
        if (clause == null || clause.trim().isEmpty()) {
            throw new IllegalArgumentException("HAVING条件不能为空");
        }
        havingClauses.add(clause.trim());
        return this;
    }

    /**
     * 设置LIMIT子句
     * SOQL支持LIMIT，用于限制返回的记录数
     * 注意：SOQL查询最多返回50000条记录
     *
     * @param limit 限制数量
     * @return 当前构建器实例
     * @throws IllegalArgumentException 如果limit无效
     */
    public SoqlBuilder limit(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("LIMIT必须大于0");
        }
        
        if (limit > 50000) {
            throw new IllegalArgumentException("SOQL查询最多返回50000条记录");
        }
        
        this.limit = limit;
        return this;
    }

    /**
     * 设置OFFSET子句
     * SOQL支持OFFSET，用于跳过指定数量的记录
     * 注意：OFFSET上限为2000，且必须与LIMIT一起使用
     *
     * @param offset 偏移量
     * @return 当前构建器实例
     * @throws IllegalArgumentException 如果offset无效
     */
    public SoqlBuilder offset(int offset) {
        if (offset < 0) {
            throw new IllegalArgumentException("OFFSET不能为负数");
        }
        
        if (offset > 2000) {
            throw new IllegalArgumentException("SOQL的OFFSET上限为2000");
        }
        
        this.offset = offset;
        return this;
    }

    /**
     * 构建SOQL查询语句
     *
     * @return SOQL查询语句
     * @throws IllegalStateException 如果构建器配置不完整
     */
    public String build() {
        validate();

        StringBuilder soql = new StringBuilder();

        // 构建SELECT子句 - SOQL不支持SELECT *
        soql.append("SELECT ");
        for (int i = 0; i < selectFields.size(); i++) {
            if (i > 0) {
                soql.append(", ");
            }
            soql.append(selectFields.get(i));
        }

        // 构建FROM子句
        soql.append(" FROM ").append(fromObject);

        // 构建WHERE子句
        buildWhereClause(soql);

        // 构建GROUP BY子句
        buildGroupByClause(soql);

        // 构建HAVING子句
        buildHavingClause(soql);

        // 构建ORDER BY子句
        buildOrderByClause(soql);

        // 构建LIMIT子句
        if (limit != null) {
            soql.append(" LIMIT ").append(limit);
        }

        // 构建OFFSET子句
        if (offset != null) {
            soql.append(" OFFSET ").append(offset);
        }

        return soql.toString();
    }

    /**
     * 验证构建器配置
     *
     * @throws IllegalStateException 如果配置不完整或无效
     */
    private void validate() {
        if (fromObject == null || fromObject.trim().isEmpty()) {
            throw new IllegalStateException("FROM子句不能为空，必须指定查询对象");
        }
        
        if (selectFields.isEmpty()) {
            throw new IllegalStateException("SELECT子句不能为空，SOQL不支持SELECT *");
        }
        
        if (offset != null && limit == null) {
            throw new IllegalStateException("OFFSET必须与LIMIT一起使用");
        }
        
        if (!groupByFields.isEmpty() && havingClauses.isEmpty()) {
            logWarning("GROUP BY通常需要配合HAVING使用");
        }
        
        if (groupByFields.isEmpty() && !havingClauses.isEmpty()) {
            logWarning("HAVING子句需要与GROUP BY一起使用");
        }
    }
    
    /**
     * 记录警告信息
     * 注意：实际使用时应该使用日志框架
     *
     * @param message 警告信息
     */
    private void logWarning(String message) {
        System.err.println("WARNING: " + message);
    }

    /**
     * 构建WHERE子句
     *
     * @param soql StringBuilder实例
     */
    private void buildWhereClause(StringBuilder soql) {
        boolean hasWhere = false;

        // 处理WHERE子句
        for (String clause : whereClauses) {
            if (!hasWhere) {
                soql.append(" WHERE ").append(clause);
                hasWhere = true;
            } else {
                soql.append(" AND ").append(clause);
            }
        }

        // 处理AND子句
        for (String clause : andClauses) {
            if (!hasWhere) {
                soql.append(" WHERE ").append(clause);
                hasWhere = true;
            } else {
                soql.append(" AND ").append(clause);
            }
        }

        // 处理OR子句
        for (String clause : orClauses) {
            if (!hasWhere) {
                soql.append(" WHERE ").append(clause);
                hasWhere = true;
            } else {
                soql.append(" OR ").append(clause);
            }
        }
    }

    /**
     * 构建GROUP BY子句
     *
     * @param soql StringBuilder实例
     */
    private void buildGroupByClause(StringBuilder soql) {
        if (!groupByFields.isEmpty()) {
            soql.append(" GROUP BY ");
            for (int i = 0; i < groupByFields.size(); i++) {
                if (i > 0) {
                    soql.append(", ");
                }
                soql.append(groupByFields.get(i));
            }
        }
    }

    /**
     * 构建HAVING子句
     *
     * @param soql StringBuilder实例
     */
    private void buildHavingClause(StringBuilder soql) {
        if (!havingClauses.isEmpty()) {
            boolean hasHaving = false;
            for (String clause : havingClauses) {
                if (!hasHaving) {
                    soql.append(" HAVING ").append(clause);
                    hasHaving = true;
                } else {
                    soql.append(" AND ").append(clause);
                }
            }
        }
    }

    /**
     * 构建ORDER BY子句
     *
     * @param soql StringBuilder实例
     */
    private void buildOrderByClause(StringBuilder soql) {
        if (!orderByClauses.isEmpty()) {
            soql.append(" ORDER BY ");
            for (int i = 0; i < orderByClauses.size(); i++) {
                if (i > 0) {
                    soql.append(", ");
                }
                OrderByClause clause = orderByClauses.get(i);
                soql.append(clause.field).append(" ").append(clause.direction);
            }
        }
    }

    /**
     * 排序方向枚举
     */
    public enum SortOrder {
        ASC, DESC
    }

    /**
     * ORDER BY子句内部类
     */
    private static class OrderByClause {
        private final String field;
        private final SortOrder direction;

        private OrderByClause(String field, SortOrder direction) {
            this.field = field;
            this.direction = direction;
        }
    }
}