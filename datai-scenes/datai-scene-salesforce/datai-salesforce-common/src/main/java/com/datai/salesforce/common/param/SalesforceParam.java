package com.datai.salesforce.common.param;


import lombok.Data;

import java.util.Date;


@Data
public class SalesforceParam {

    /**
     * 查询对象
     */
    private String api;
    /**
     * 查询参数
     */
    private String select;
    /**
     * 开始创建时间
     */
    private Date beginDate;
    /**
     * 结束创建时间
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
    private String dateField = "CreatedDate";
    /**
     * sql
     */
    private String sql;
    /**
     * 批次
     */
    private Integer batchId;
    /**
     * 逻辑删除
     */
    private Boolean isDeleted;

    /**
     * 查询限制数量
     */
    private Integer limit;

    /**
     * 是否单线程
     */
    private Boolean isSingleThread = false;

}
