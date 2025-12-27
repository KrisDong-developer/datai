package com.datai.integration.domain.param;

import com.alibaba.fastjson2.filter.SimplePropertyPreFilter;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;

/**
 * @author Radian
 * @description
 * @date 2022/11/30
 */
@Data
public class DataiSyncParam {

    /**
     * 表名 多个英文逗号分割
     */
    private String api;
    /**
     * ids
     */
    private Set<String> ids;
    /**
     * 开始时间
     */
    private Date beginDate;
    /**
     * 结束时间
     */
    private Date endDate;
    /**
     * 同步类型 1：存量 2：增量
     */
    private Integer type;

    /**
     * fastjson 过滤器 只打印当前类所有属性
     * @return SimplePropertyPreFilter
     */
    public static SimplePropertyPreFilter getFilter() {
        return new SimplePropertyPreFilter(DataiSyncParam.class, Arrays.stream(DataiSyncParam.class.getDeclaredFields()).map(Field::getName).toArray(String[]::new));
    }

}
