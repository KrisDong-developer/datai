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
    Map<String, Boolean> syncMultipleObjectStructures(List<String> objectApis);

    /**
     * 同步多个Salesforce对象的数据
     * 
     * @param objectApis Salesforce对象API列表
     * @return 同步结果，键为对象API，值为同步结果
     */
    Map<String, Boolean> syncObjectsData(List<String> objectApis);

    /**
     * 同步单个Salesforce对象的数据
     * 
     * @param objectApi Salesforce对象API
     * @return 同步结果
     */
    boolean syncObjectsData(String objectApi);

    /**
     * 同步Salesforce对象的指定批次数据
     * 
     * @param objectApi Salesforce对象API
     * @param batchId 批次ID
     * @return 同步结果
     */
    boolean syncObjectDataByBatch(String objectApi, String batchId);

}
