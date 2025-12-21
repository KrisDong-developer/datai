package org.dromara.salesforce.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.dromara.salesforce.constant.SalesforceConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * SQL工具类
 * <p>
 * 用于生成和解析MyBatis执行的SQL语句，将带有占位符的SQL语句转换为可直接执行的完整SQL语句。
 * 主要用于Salesforce相关操作的SQL生成。
 * </p>
 *
 * @author Red
 * @version 1.0
 * @date 2022/04/13
 */
@Component
@Slf4j
public class SqlUtil {

    /**
     * MyBatis SQL会话工厂
     */
    private static SqlSessionFactory sqlSessionFactory = null;

    /**
     * 注入SQL会话工厂
     *
     * @param sqlSessionFactory SQL会话工厂实例
     */
    @Autowired
    private void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        SqlUtil.sqlSessionFactory = sqlSessionFactory;
    }


    /**
     * 根据方法名和参数生成可执行的SQL语句
     * <p>
     * 该方法通过MyBatis的MappedStatement获取BoundSql对象，
     * 然后解析其中的参数，将占位符替换为实际参数值，生成完整的SQL语句。
     * </p>
     *
     * @param method 方法全名称，格式如：com.celnet.xxxx.select
     * @param param  SQL执行参数，没有参数时传入null
     * @return 完整的可执行SQL语句字符串
     */
    public static String showSql(String method, Map<String, Object> param) {
        Configuration configuration = sqlSessionFactory.getConfiguration();
        BoundSql boundSql = configuration
                .getMappedStatement(method)
                .getBoundSql(param);
        return showSql(configuration, boundSql);
    }

    /**
     * 解析BoundSql对象，生成不含占位符的完整SQL语句
     * <p>
     * 该方法处理BoundSql中的参数映射，将SQL语句中的占位符(?)替换为实际的参数值，
     * 并对特殊类型的参数（如字符串、日期等）进行特殊处理。
     * </p>
     *
     * @param configuration MyBatis配置对象
     * @param boundSql      MyBatis绑定SQL对象
     * @return 完整的可执行SQL语句
     */
    private static String showSql(Configuration configuration, BoundSql boundSql) {
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        // 去除多余空格和换行符，规范化SQL语句
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
        if (parameterMappings.size() > 0 && parameterObject != null) {
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                sql = sql.replaceFirst("\\?", getParameterValue(parameterObject));
            } else {
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                for (ParameterMapping parameterMapping : parameterMappings) {
                    String propertyName = parameterMapping.getProperty();
                    if (metaObject.hasGetter(propertyName)) {
                        Object obj = metaObject.getValue(propertyName);
                        sql = sql.replaceFirst("\\?", getParameterValue(obj));
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        Object obj = boundSql.getAdditionalParameter(propertyName);
                        sql = sql.replaceFirst("\\?", getParameterValue(obj));
                    }
                }
            }
        }
        return sql;
    }

    /**
     * 根据参数类型获取参数值字符串表示
     * <p>
     * 对不同类型的参数进行特殊处理：
     * - 字符串类型：在值两侧添加单引号
     * - 日期类型：格式化为Salesforce标准日期格式
     * - 其他类型：直接转换为字符串
     * </p>
     *
     * @param obj 参数对象
     * @return 参数值的字符串表示
     */
    private static String getParameterValue(Object obj) {
        StringBuilder value = new StringBuilder();
        if (obj instanceof String) {
            // 字符串类型参数两侧添加单引号
            value.append("'").append(obj).append("'");
        } else if (obj instanceof Date) {
            // 日期转为Salesforce格式
            value.append(DateFormatUtils.formatUTC((Date) obj, SalesforceConstant.SF_DATE_FORMAT));
        } else if (obj instanceof Calendar) {
            // 日期转为Salesforce格式
            value.append(DateFormatUtils.formatUTC(((Calendar) obj).getTime(), SalesforceConstant.SF_DATE_FORMAT));
        } else {
            if (obj != null) {
                value.append(obj);
            } else {
                value.append(StringUtils.EMPTY);
            }
        }
        return value.toString();
    }

}
