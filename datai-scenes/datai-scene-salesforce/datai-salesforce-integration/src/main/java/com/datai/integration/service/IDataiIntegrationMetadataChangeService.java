package com.datai.integration.service;

import java.util.List;
import java.util.Map;
import com.datai.integration.model.domain.DataiIntegrationMetadataChange;

/**
 * 对象元数据变更Service接口
 * 
 * @author datai
 * @date 2025-12-27
 */
public interface IDataiIntegrationMetadataChangeService 
{
    /**
     * 查询对象元数据变更
     * 
     * @param id 对象元数据变更主键
     * @return 对象元数据变更
     */
    public DataiIntegrationMetadataChange selectDataiIntegrationMetadataChangeById(Long id);

    /**
     * 查询对象元数据变更列表
     * 
     * @param dataiIntegrationMetadataChange 对象元数据变更
     * @return 对象元数据变更集合
     */
    public List<DataiIntegrationMetadataChange> selectDataiIntegrationMetadataChangeList(DataiIntegrationMetadataChange dataiIntegrationMetadataChange);

    /**
     * 新增对象元数据变更
     * 
     * @param dataiIntegrationMetadataChange 对象元数据变更
     * @return 结果
     */
    public int insertDataiIntegrationMetadataChange(DataiIntegrationMetadataChange dataiIntegrationMetadataChange);

    /**
     * 修改对象元数据变更
     * 
     * @param dataiIntegrationMetadataChange 对象元数据变更
     * @return 结果
     */
    public int updateDataiIntegrationMetadataChange(DataiIntegrationMetadataChange dataiIntegrationMetadataChange);

    /**
     * 批量删除对象元数据变更
     * 
     * @param ids 需要删除的对象元数据变更主键集合
     * @return 结果
     */
    public int deleteDataiIntegrationMetadataChangeByIds(Long[] ids);

    /**
     * 删除对象元数据变更信息
     * 
     * @param id 对象元数据变更主键
     * @return 结果
     */
    public int deleteDataiIntegrationMetadataChangeById(Long id);

    /**
     * 查询未同步的元数据变更列表
     * 
     * @param dataiIntegrationMetadataChange 对象元数据变更
     * @return 未同步的元数据变更集合
     */
    public List<DataiIntegrationMetadataChange> selectUnsyncedMetadataChangeList(DataiIntegrationMetadataChange dataiIntegrationMetadataChange);

    /**
     * 批量更新元数据变更同步状态
     * 
     * @param ids 需要更新的元数据变更ID集合
     * @param syncStatus 同步状态 (0-未同步, 1-已同步, 2-同步失败)
     * @param syncErrorMessage 同步错误信息
     * @return 结果
     */
    public int batchUpdateSyncStatus(Long[] ids, Integer syncStatus, String syncErrorMessage);

    /**
     * 获取变更统计信息
     * 
     * @param params 查询参数
     * @return 统计信息
     */
    public Map<String, Object> getChangeStatistics(Map<String, Object> params);

    /**
     * 同步元数据变更到本地数据库
     * 
     * @param id 元数据变更ID
     * @return 同步结果
     */
    public Map<String, Object> syncToLocalDatabase(Long id);

    /**
     * 批量同步元数据变更到本地数据库
     * 
     * @param ids 元数据变更ID集合
     * @return 同步结果
     */
    public Map<String, Object> syncBatchToLocalDatabase(Long[] ids);

    /**
     * 全对象元数据变更拉取
     * 从Salesforce拉取所有对象的元数据变更信息并记录到元数据变更表中
     * 表的变更新增需要满足以下任一条件：
     * - isQueryable (可查询)
     * - isCreateable (可创建)
     * - isUpdateable (可更新)
     * - isDeletable (可删除)
     * 字段的变更新增无限制
     * 
     * @return 拉取结果，包含对象变更数量和字段变更数量
     */
    public Map<String, Object> pullAllMetadataChanges();
}
