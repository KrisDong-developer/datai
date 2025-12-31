package com.datai.integration.service;

import com.datai.integration.domain.DataiIntegrationObject;

import java.util.List;
import java.util.Map;

/**
 * Salesforce数据拉取服务接口
 * <p>
 * 该接口提供了Salesforce数据拉取的所有方法，包括单个对象拉取、批量拉取、增量拉取等。
 * </p>
 */
public interface ISalesforceDataPullService {


    /**
     * 同步Salesforce对象的表结构
     * 
     * @param objectApis Salesforce对象API列表
     * @return 同步结果，键为对象API，值为同步结果
     */
    boolean syncObjectStructures(List<String> objectApis);

    /**
     * 同步Salesforce对象的数据
     * 
     * @param objectApis Salesforce对象API列表
     * @return 同步结果，键为对象API，值为同步结果
     */
    boolean syncObjectsData(List<String> objectApis);

    /**
     * 同步Salesforce对象的指定批次数据
     * 
     * @param objectApi Salesforce对象API
     * @param batchId 批次ID
     * @return 同步结果
     */
    boolean syncObjectDataByBatch(String objectApi, String batchId);

    /**
     * 自动同步Salesforce对象信息到对象信息表
     * 同步满足以下任一条件的对象：
     * - isQueryable (可查询)
     * - isCreateable (可创建)
     * - isUpdateable (可更新)
     * - isDeletable (可删除)
     * 
     * @return 同步操作是否成功
     */
    boolean autoSyncObjects();

    /**
     * 同步元数据变更到元数据变更信息表
     * 表的变更新增需要满足以下任一条件：
     * - isQueryable (可查询)
     * - isCreateable (可创建)
     * - isUpdateable (可更新)
     * - isDeletable (可删除)
     * 字段的变更新增无限制
     * 
     * @return 同步操作是否成功
     */
    boolean syncMetadataChanges();

}
