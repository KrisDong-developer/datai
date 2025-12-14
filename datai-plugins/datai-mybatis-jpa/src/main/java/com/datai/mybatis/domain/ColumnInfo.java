package com.datai.mybatis.domain;

import java.lang.reflect.Field;

import org.springframework.core.annotation.AnnotationUtils;

import com.datai.mybatis.annotation.Column;
import com.datai.mybatis.annotation.Query;
import com.datai.mybatis.utils.QueryUtil;

public class ColumnInfo extends BaseColumnInfo {
    private Column column;

    public ColumnInfo(Field field) {
        this.field = field;
        this.column = AnnotationUtils.findAnnotation(this.field, Column.class);
        this.query = AnnotationUtils.findAnnotation(this.field, Query.class);
        this.columnName = this.column.name();
        this.fieldName = this.field.getName();
        this.field.setAccessible(true);
        this.querySql = this.getQuerySql(this.query);
    }

    public boolean isPrimaryKey() {
        return this.column.primaryKey();
    }

    public String getQuerySql(Query query) {
        return QueryUtil.getQuerySql(this.getColumnName(), getTemplate(), query);
    }

    public String getQuerySql() {
        return this.querySql;
    }

    public String getFullyQualifiedColumnName(String tableName) {
        return tableName + "." + this.getColumnName();
    }
}
