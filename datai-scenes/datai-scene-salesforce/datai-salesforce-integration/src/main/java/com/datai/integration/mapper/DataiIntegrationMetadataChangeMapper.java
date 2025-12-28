package com.datai.integration.mapper;

import java.util.List;
import java.util.Map;
import com.datai.integration.domain.DataiIntegrationMetadataChange;

/**
 * 对象元数据变更Mapper接口
 * 
 * @author datai
 * @date 2025-12-27
 */
public interface DataiIntegrationMetadataChangeMapper 
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
     * 删除对象元数据变更
     * 
     * @param id 对象元数据变更主键
     * @return 结果
     */
    public int deleteDataiIntegrationMetadataChangeById(Long id);

    /**
     * 批量删除对象元数据变更
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDataiIntegrationMetadataChangeByIds(Long[] ids);

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
     * @param updateBy 更新人
     * @return 结果
     */
    public int batchUpdateSyncStatus(Long[] ids, Integer syncStatus, String syncErrorMessage, String updateBy);

    /**
     * 查询变更统计信息
     * 
     * @param params 查询参数
     * @return 统计信息
     */
    public Map<String, Object> selectChangeStatistics(Map<String, Object> params);
}
