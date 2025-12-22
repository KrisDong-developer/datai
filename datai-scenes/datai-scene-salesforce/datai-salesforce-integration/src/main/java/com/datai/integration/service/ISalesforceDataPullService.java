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
     * 拉取单个Salesforce对象的数据
     * 
     * @param objectName Salesforce对象名称
     * @param fields 需要拉取的字段列表
     * @param filter 过滤条件
     * @return 拉取的数据列表
     */
    List<Map<String, Object>> pullSingleObject(String objectName, List<String> fields, String filter);

    /**
     * 拉取多个Salesforce对象的数据
     * 
     * @param objectNames Salesforce对象名称列表
     * @return 拉取的数据，键为对象名称，值为该对象的数据列表
     */
    Map<String, List<Map<String, Object>>> pullMultipleObjects(List<String> objectNames);

    /**
     * 增量拉取Salesforce对象的数据
     * 
     * @param objectName Salesforce对象名称
     * @param fields 需要拉取的字段列表
     * @param lastModifiedField 最后修改时间字段
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 拉取的数据列表
     */
    List<Map<String, Object>> pullIncrementalData(String objectName, List<String> fields, String lastModifiedField, String startTime, String endTime);

    /**
     * 使用Bulk API拉取大量数据
     * 
     * @param objectName Salesforce对象名称
     * @param fields 需要拉取的字段列表
     * @param filter 过滤条件
     * @return 拉取的数据列表
     */
    List<Map<String, Object>> pullBulkData(String objectName, List<String> fields, String filter);

    /**
     * 拉取Salesforce对象的元数据
     * 
     * @param objectName Salesforce对象名称
     * @return 对象元数据信息
     */
    Map<String, Object> pullObjectMetadata(String objectName);

    /**
     * 拉取Salesforce对象的字段信息
     * 
     * @param objectName Salesforce对象名称
     * @return 字段信息列表
     */
    List<Map<String, Object>> pullObjectFields(String objectName);

    /**
     * 验证Salesforce连接是否可用
     * 
     * @return 连接是否可用
     */
    boolean validateConnection();

    /**
     * 获取Salesforce API版本信息
     * 
     * @return API版本信息
     */
    String getApiVersion();

    /**
     * 获取Salesforce组织信息
     * 
     * @return 组织信息
     */
    Map<String, Object> getOrganizationInfo();

    /**
     * 拉取Salesforce对象的记录总数
     * 
     * @param objectName Salesforce对象名称
     * @param filter 过滤条件
     * @return 记录总数
     */
    long getRecordCount(String objectName, String filter);
}
