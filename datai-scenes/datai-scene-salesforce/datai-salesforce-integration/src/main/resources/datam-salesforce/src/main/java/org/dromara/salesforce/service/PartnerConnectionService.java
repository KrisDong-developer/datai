package org.dromara.salesforce.service;

import com.sforce.soap.partner.*;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import org.apache.commons.beanutils.DynaBean;

import java.util.List;

/**
 * Partner连接服务接口 - 提供Partner API操作的相关方法
 * 
 * PartnerConnectionService是Data Loader中负责处理Partner API操作的服务接口。
 * 它定义了与Salesforce SOAP API交互所需的所有方法，包括CRUD操作、查询操作等。
 * 
 * 主要功能：
 * 1. 提供标准的CRUD操作(create, update, delete等)
 * 2. 提供查询操作(query, queryAll等)
 * 3. 提供特殊操作(upsert, undelete等)
 * 4. 提供源ORG和目标ORG的独立操作方法
 * 
 * 设计特点：
 * - 遵循Spring Boot服务接口设计规范
 * - 提供源ORG和目标ORG的独立操作方法
 * - 统一异常处理，抛出ConnectionException
 * - 向后兼容，提供默认操作方法
 * 
 * 使用场景：
 * - 需要使用SOAP API进行数据操作的场景
 * - 需要复杂查询和操作的场景
 * - 数据在两个ORG之间迁移的场景
 * 
 * @author Salesforce
 * @since 64.0.0
 */
public interface PartnerConnectionService {

    // ==================== 向后兼容方法 - 默认使用源ORG ====================
    
    /**
     * 创建记录
     * 
     * @param sObjects SObject数组
     * @return SaveResult[] 保存结果数组
     * @throws ConnectionException 连接异常
     */
    SaveResult[] create(SObject[] sObjects) throws ConnectionException;
    
    /**
     * 更新记录
     * 
     * @param sObjects SObject数组
     * @return SaveResult[] 保存结果数组
     * @throws ConnectionException 连接异常
     */
    SaveResult[] update(SObject[] sObjects) throws ConnectionException;
    
    /**
     * 删除记录
     * 
     * @param ids ID数组
     * @return DeleteResult[] 删除结果数组
     * @throws ConnectionException 连接异常
     */
    DeleteResult[] delete(String[] ids) throws ConnectionException;
    
    /**
     * Upsert记录
     * 
     * @param externalIDFieldName 外部ID字段名
     * @param sObjects SObject数组
     * @return UpsertResult[] Upsert结果数组
     * @throws ConnectionException 连接异常
     */
    UpsertResult[] upsert(String externalIDFieldName, SObject[] sObjects) throws ConnectionException;
    
    /**
     * 恢复删除的记录
     * 
     * @param ids ID数组
     * @return UndeleteResult[] 恢复结果数组
     * @throws ConnectionException 连接异常
     */
    UndeleteResult[] undelete(String[] ids) throws ConnectionException;
    
    /**
     * 查询记录
     * 
     * @param queryString 查询语句
     * @return QueryResult 查询结果
     * @throws ConnectionException 连接异常
     */
    QueryResult query(String queryString) throws ConnectionException;
    
    /**
     * 查询所有记录（包括回收站）
     * 
     * @param queryString 查询语句
     * @return QueryResult 查询结果
     * @throws ConnectionException 连接异常
     */
    QueryResult queryAll(String queryString) throws ConnectionException;
    
    /**
     * 获取更多查询结果
     * 
     * @param queryLocator 查询定位器
     * @return QueryResult 查询结果
     * @throws ConnectionException 连接异常
     */
    QueryResult queryMore(String queryLocator) throws ConnectionException;
    
    /**
     * 获取对象描述信息
     * 
     * @param sObjectType 对象类型
     * @return DescribeSObjectResult 对象描述结果
     * @throws ConnectionException 连接异常
     */
    DescribeSObjectResult describeSObject(String sObjectType) throws ConnectionException;
    
    /**
     * 获取多个对象描述信息
     * 
     * @param sObjectTypes 对象类型数组
     * @return DescribeSObjectResult[] 对象描述结果数组
     * @throws ConnectionException 连接异常
     */
    DescribeSObjectResult[] describeSObjects(String[] sObjectTypes) throws ConnectionException;
    
    /**
     * 获取全局描述信息
     * 
     * @return DescribeGlobalResult 全局描述结果
     * @throws ConnectionException 连接异常
     */
    DescribeGlobalResult describeGlobal() throws ConnectionException;
    
    /**
     * 获取布局信息
     * 
     * @param sObjectType 对象类型
     * @param recordTypeIds 记录类型ID数组
     * @return DescribeLayoutResult 布局描述结果
     * @throws ConnectionException 连接异常
     */
    DescribeLayoutResult describeLayout(String sObjectType, String[] recordTypeIds) throws ConnectionException;
    
    /**
     * 获取布局信息（带记录类型ID）
     * 
     * @param sObjectType 对象类型
     * @param recordTypeId 记录类型ID
     * @param recordTypeIds 记录类型ID数组
     * @return DescribeLayoutResult 布局描述结果
     * @throws ConnectionException 连接异常
     */
    DescribeLayoutResult describeLayout(String sObjectType, String recordTypeId, String[] recordTypeIds) throws ConnectionException;

    // ==================== 源ORG专用方法 ====================
    
    /**
     * 在源ORG中创建记录
     * 
     * @param sObjects SObject数组
     * @return SaveResult[] 保存结果数组
     * @throws ConnectionException 连接异常
     */
    SaveResult[] createInSource(SObject[] sObjects) throws ConnectionException;
    
    /**
     * 在源ORG中更新记录
     * 
     * @param sObjects SObject数组
     * @return SaveResult[] 保存结果数组
     * @throws ConnectionException 连接异常
     */
    SaveResult[] updateInSource(SObject[] sObjects) throws ConnectionException;
    
    /**
     * 在源ORG中删除记录
     * 
     * @param ids ID数组
     * @return DeleteResult[] 删除结果数组
     * @throws ConnectionException 连接异常
     */
    DeleteResult[] deleteInSource(String[] ids) throws ConnectionException;
    
    /**
     * 在源ORG中Upsert记录
     * 
     * @param externalIDFieldName 外部ID字段名
     * @param sObjects SObject数组
     * @return UpsertResult[] Upsert结果数组
     * @throws ConnectionException 连接异常
     */
    UpsertResult[] upsertInSource(String externalIDFieldName, SObject[] sObjects) throws ConnectionException;
    
    /**
     * 在源ORG中恢复删除的记录
     * 
     * @param ids ID数组
     * @return UndeleteResult[] 恢复结果数组
     * @throws ConnectionException 连接异常
     */
    UndeleteResult[] undeleteInSource(String[] ids) throws ConnectionException;
    
    /**
     * 在源ORG中查询记录
     * 
     * @param queryString 查询语句
     * @return QueryResult 查询结果
     * @throws ConnectionException 连接异常
     */
    QueryResult queryInSource(String queryString) throws ConnectionException;
    
    /**
     * 在源ORG中查询所有记录（包括回收站）
     * 
     * @param queryString 查询语句
     * @return QueryResult 查询结果
     * @throws ConnectionException 连接异常
     */
    QueryResult queryAllInSource(String queryString) throws ConnectionException;
    
    /**
     * 在源ORG中获取更多查询结果
     * 
     * @param queryLocator 查询定位器
     * @return QueryResult 查询结果
     * @throws ConnectionException 连接异常
     */
    QueryResult queryMoreInSource(String queryLocator) throws ConnectionException;
    
    /**
     * 获取源ORG对象描述信息
     * 
     * @param sObjectType 对象类型
     * @return DescribeSObjectResult 对象描述结果
     * @throws ConnectionException 连接异常
     */
    DescribeSObjectResult describeSObjectInSource(String sObjectType) throws ConnectionException;
    
    /**
     * 获取源ORG多个对象描述信息
     * 
     * @param sObjectTypes 对象类型数组
     * @return DescribeSObjectResult[] 对象描述结果数组
     * @throws ConnectionException 连接异常
     */
    DescribeSObjectResult[] describeSObjectsInSource(String[] sObjectTypes) throws ConnectionException;
    
    /**
     * 获取源ORG全局描述信息
     * 
     * @return DescribeGlobalResult 全局描述结果
     * @throws ConnectionException 连接异常
     */
    DescribeGlobalResult describeGlobalInSource() throws ConnectionException;
    
    /**
     * 获取源ORG布局信息
     * 
     * @param sObjectType 对象类型
     * @param recordTypeIds 记录类型ID数组
     * @return DescribeLayoutResult 布局描述结果
     * @throws ConnectionException 连接异常
     */
    DescribeLayoutResult describeLayoutInSource(String sObjectType, String[] recordTypeIds) throws ConnectionException;
    
    /**
     * 获取源ORG布局信息（带记录类型ID）
     * 
     * @param sObjectType 对象类型
     * @param recordTypeId 记录类型ID
     * @param recordTypeIds 记录类型ID数组
     * @return DescribeLayoutResult 布局描述结果
     * @throws ConnectionException 连接异常
     */
    DescribeLayoutResult describeLayoutInSource(String sObjectType, String recordTypeId, String[] recordTypeIds) throws ConnectionException;

    // ==================== 目标ORG专用方法 ====================
    
    /**
     * 在目标ORG中创建记录
     * 
     * @param sObjects SObject数组
     * @return SaveResult[] 保存结果数组
     * @throws ConnectionException 连接异常
     */
    SaveResult[] createInTarget(SObject[] sObjects) throws ConnectionException;
    
    /**
     * 在目标ORG中更新记录
     * 
     * @param sObjects SObject数组
     * @return SaveResult[] 保存结果数组
     * @throws ConnectionException 连接异常
     */
    SaveResult[] updateInTarget(SObject[] sObjects) throws ConnectionException;
    
    /**
     * 在目标ORG中删除记录
     * 
     * @param ids ID数组
     * @return DeleteResult[] 删除结果数组
     * @throws ConnectionException 连接异常
     */
    DeleteResult[] deleteInTarget(String[] ids) throws ConnectionException;
    
    /**
     * 在目标ORG中Upsert记录
     * 
     * @param externalIDFieldName 外部ID字段名
     * @param sObjects SObject数组
     * @return UpsertResult[] Upsert结果数组
     * @throws ConnectionException 连接异常
     */
    UpsertResult[] upsertInTarget(String externalIDFieldName, SObject[] sObjects) throws ConnectionException;
    
    /**
     * 在目标ORG中恢复删除的记录
     * 
     * @param ids ID数组
     * @return UndeleteResult[] 恢复结果数组
     * @throws ConnectionException 连接异常
     */
    UndeleteResult[] undeleteInTarget(String[] ids) throws ConnectionException;
    
    /**
     * 在目标ORG中查询记录
     * 
     * @param queryString 查询语句
     * @return QueryResult 查询结果
     * @throws ConnectionException 连接异常
     */
    QueryResult queryInTarget(String queryString) throws ConnectionException;
    
    /**
     * 在目标ORG中查询所有记录（包括回收站）
     * 
     * @param queryString 查询语句
     * @return QueryResult 查询结果
     * @throws ConnectionException 连接异常
     */
    QueryResult queryAllInTarget(String queryString) throws ConnectionException;
    
    /**
     * 在目标ORG中获取更多查询结果
     * 
     * @param queryLocator 查询定位器
     * @return QueryResult 查询结果
     * @throws ConnectionException 连接异常
     */
    QueryResult queryMoreInTarget(String queryLocator) throws ConnectionException;
    
    /**
     * 获取目标ORG对象描述信息
     * 
     * @param sObjectType 对象类型
     * @return DescribeSObjectResult 对象描述结果
     * @throws ConnectionException 连接异常
     */
    DescribeSObjectResult describeSObjectInTarget(String sObjectType) throws ConnectionException;
    
    /**
     * 获取目标ORG多个对象描述信息
     * 
     * @param sObjectTypes 对象类型数组
     * @return DescribeSObjectResult[] 对象描述结果数组
     * @throws ConnectionException 连接异常
     */
    DescribeSObjectResult[] describeSObjectsInTarget(String[] sObjectTypes) throws ConnectionException;
    
    /**
     * 获取目标ORG全局描述信息
     * 
     * @return DescribeGlobalResult 全局描述结果
     * @throws ConnectionException 连接异常
     */
    DescribeGlobalResult describeGlobalInTarget() throws ConnectionException;
    
    /**
     * 获取目标ORG布局信息
     * 
     * @param sObjectType 对象类型
     * @param recordTypeIds 记录类型ID数组
     * @return DescribeLayoutResult 布局描述结果
     * @throws ConnectionException 连接异常
     */
    DescribeLayoutResult describeLayoutInTarget(String sObjectType, String[] recordTypeIds) throws ConnectionException;
    
    /**
     * 获取目标ORG布局信息（带记录类型ID）
     * 
     * @param sObjectType 对象类型
     * @param recordTypeId 记录类型ID
     * @param recordTypeIds 记录类型ID数组
     * @return DescribeLayoutResult 布局描述结果
     * @throws ConnectionException 连接异常
     */
    DescribeLayoutResult describeLayoutInTarget(String sObjectType, String recordTypeId, String[] recordTypeIds) throws ConnectionException;
}