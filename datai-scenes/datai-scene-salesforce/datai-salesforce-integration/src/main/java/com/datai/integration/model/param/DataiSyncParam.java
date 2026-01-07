package com.datai.integration.model.param;

import com.alibaba.fastjson2.filter.SimplePropertyPreFilter;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;

/**
 * @author Kris
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
     * 开始创建时间
     */
    private Date beginCreateDate;
    /**
     * 结束创建时间
     */
    private Date endCreateDate;
    /**
     * 开始修改时间
     */
    private Date beginModifyDate;

    /**
     * 数据拉取截断时间
     */
    private Date lastDate;
    /**
     * 结束修改时间
     */
    private Date endModifyDate;
    /**
     * 最大Id
     */
    private String maxId;
    /**
     * id字段
     */
    private String idField = "Id";
    /**
     * 时间字段
     */
    private String batchField = "CreatedDate";
    /**
     * 查询限制数量
     */
    private Integer limit;
    /**
     * 查询字段 多个英文逗号分割
     */
    private String select;
    /**
     * 是否存在IsDeleted删除字段
     */
    private Boolean isDeleted;


}
