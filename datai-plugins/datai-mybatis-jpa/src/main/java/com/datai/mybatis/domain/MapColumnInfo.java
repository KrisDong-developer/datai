package com.datai.mybatis.domain;

import java.lang.reflect.Field;

import org.springframework.core.annotation.AnnotationUtils;

import com.datai.mybatis.annotation.ColumnMap;
import com.datai.mybatis.annotation.Query;
import com.datai.mybatis.utils.QueryUtil;

/**
 * 数据库关联字段信息
 *
 * @author Dftre
 */
public class MapColumnInfo extends BaseColumnInfo {
    private ColumnMap columnMap;

    public MapColumnInfo(Field field) {
        this.field = field;
        this.columnMap = AnnotationUtils.findAnnotation(this.field, ColumnMap.class);
        this.columnName = this.columnMap.name();
        this.query = AnnotationUtils.findAnnotation(this.field, Query.class);
        this.fieldName = this.field.getName();
        this.field.setAccessible(true);
        this.querySql = this.getQuerySql(this.query);
    }

    public ColumnMap getJoin() {
        return this.columnMap;
    }

    public String getQuerySql(Query query) {
        return QueryUtil.getQuerySql(this.getColumnName(), getTemplate(), query);
    }

    public String getQuerySql() {
        return this.columnMap.target() + "." + this.querySql;
    }

    public String getFullyQualifiedColumnName() {
        return this.columnMap.target() + "." + this.getColumnName();
    }

}
