package com.datai.salesforce.common.param;

import lombok.Data;

import java.util.Date;


/**
 * Salesforce参数类（存量版本）
 * <p>
 * 该类是SalesforceParam的存量版本，剥离了beginCreateDate和endCreateDate字段，
 * 用于兼容历史代码和特定场景下的参数传递。
 * </p>
 *
 * @author lingma
 * @version 1.0
 */
@Data
public class SalesforceStockParam {

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
    private String idField="Id";
    /**
     * sql
     */
    private String sql;
    /**
     * LastModifiedDate
     */
    private Date timestamp;
    /**
     * 时间字段
     */
    private String dateField = "CreatedDate";
    /**
     * 批次
     */
    private Integer batch = 1;
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
