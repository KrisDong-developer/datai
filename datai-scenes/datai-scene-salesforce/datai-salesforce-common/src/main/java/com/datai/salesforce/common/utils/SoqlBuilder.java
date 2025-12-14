package com.datai.salesforce.common.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * SOQL查询构建器，支持复杂查询条件和子查询
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
     *
     * @param fields 要查询的字段
     * @return 当前构建器实例
     */
    public SoqlBuilder select(String... fields) {
        if (fields != null) {
            for (String field : fields) {
                if (field != null && !field.trim().isEmpty()) {
                    selectFields.add(field.trim());
                }
            }
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
     */
    public SoqlBuilder where(String clause) {
        if (clause != null && !clause.trim().isEmpty()) {
            whereClauses.add(clause.trim());
        }
        return this;
    }

    /**
     * 添加AND子句
     *
     * @param clause AND条件
     * @return 当前构建器实例
     */
    public SoqlBuilder and(String clause) {
        if (clause != null && !clause.trim().isEmpty()) {
            andClauses.add(clause.trim());
        }
        return this;
    }

    /**
     * 添加OR子句
     *
     * @param clause OR条件
     * @return 当前构建器实例
     */
    public SoqlBuilder or(String clause) {
        if (clause != null && !clause.trim().isEmpty()) {
            orClauses.add(clause.trim());
        }
        return this;
    }

    /**
     * 添加IN条件
     *
     * @param field 字段名
     * @param values 值列表
     * @return 当前构建器实例
     */
    public SoqlBuilder whereIn(String field, List<Object> values) {
        if (values == null || values.isEmpty()) {
            return this;
        }
        
        StringBuilder clause = new StringBuilder(field).append(" IN (");
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                clause.append(", ");
            }
            Object value = values.get(i);
            if (value instanceof String) {
                clause.append("'").append(value).append("'");
            } else {
                clause.append(value);
            }
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
     */
    public SoqlBuilder whereNotIn(String field, List<Object> values) {
        if (values == null || values.isEmpty()) {
            return this;
        }
        
        StringBuilder clause = new StringBuilder(field).append(" NOT IN (");
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                clause.append(", ");
            }
            Object value = values.get(i);
            if (value instanceof String) {
                clause.append("'").append(value).append("'");
            } else {
                clause.append(value);
            }
        }
        clause.append(")");
        
        return where(clause.toString());
    }

    /**
     * 添加LIKE条件
     *
     * @param field 字段名
     * @param value 匹配值
     * @return 当前构建器实例
     */
    public SoqlBuilder whereLike(String field, String value) {
        return where(field + " LIKE '%" + value + "%'");
    }

    /**
     * 添加NOT LIKE条件
     *
     * @param field 字段名
     * @param value 匹配值
     * @return 当前构建器实例
     */
    public SoqlBuilder whereNotLike(String field, String value) {
        return where(field + " NOT LIKE '%" + value + "%'");
    }

    /**
     * 添加IS NULL条件
     *
     * @param field 字段名
     * @return 当前构建器实例
     */
    public SoqlBuilder whereIsNull(String field) {
        return where(field + " IS NULL");
    }

    /**
     * 添加IS NOT NULL条件
     *
     * @param field 字段名
     * @return 当前构建器实例
     */
    public SoqlBuilder whereIsNotNull(String field) {
        return where(field + " IS NOT NULL");
    }

    /**
     * 添加BETWEEN条件
     *
     * @param field 字段名
     * @param start 起始值
     * @param end 结束值
     * @return 当前构建器实例
     */
    public SoqlBuilder whereBetween(String field, Object start, Object end) {
        StringBuilder clause = new StringBuilder(field).append(" BETWEEN ");
        
        if (start instanceof String) {
            clause.append("'").append(start).append("'");
        } else {
            clause.append(start);
        }
        
        clause.append(" AND ");
        
        if (end instanceof String) {
            clause.append("'").append(end).append("'");
        } else {
            clause.append(end);
        }
        
        return where(clause.toString());
    }

    /**
     * 添加ORDER BY子句
     *
     * @param field     排序字段
     * @param direction 排序方向
     * @return 当前构建器实例
     */
    public SoqlBuilder orderBy(String field, SortOrder direction) {
        this.orderByClauses.add(new OrderByClause(field, direction));
        return this;
    }

    /**
     * 添加GROUP BY子句
     *
     * @param fields 分组字段
     * @return 当前构建器实例
     */
    public SoqlBuilder groupBy(String... fields) {
        if (fields != null) {
            for (String field : fields) {
                if (field != null && !field.trim().isEmpty()) {
                    groupByFields.add(field.trim());
                }
            }
        }
        return this;
    }

    /**
     * 添加HAVING子句
     *
     * @param clause HAVING条件
     * @return 当前构建器实例
     */
    public SoqlBuilder having(String clause) {
        if (clause != null && !clause.trim().isEmpty()) {
            havingClauses.add(clause.trim());
        }
        return this;
    }

    /**
     * 设置LIMIT子句
     *
     * @param limit 限制数量
     * @return 当前构建器实例
     */
    public SoqlBuilder limit(int limit) {
        this.limit = limit;
        return this;
    }

    /**
     * 设置OFFSET子句
     *
     * @param offset 偏移量
     * @return 当前构建器实例
     */
    public SoqlBuilder offset(int offset) {
        this.offset = offset;
        return this;
    }

    /**
     * 构建SOQL查询语句
     *
     * @return SOQL查询语句
     */
    public String build() {
        validate();

        StringBuilder soql = new StringBuilder();

        // 构建SELECT子句
        soql.append("SELECT ");
        if (selectFields.isEmpty()) {
            soql.append("*");
        } else {
            for (int i = 0; i < selectFields.size(); i++) {
                if (i > 0) {
                    soql.append(", ");
                }
                soql.append(selectFields.get(i));
            }
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
     */
    private void validate() {
        if (fromObject == null || fromObject.trim().isEmpty()) {
            throw new IllegalArgumentException("FROM子句不能为空");
        }
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