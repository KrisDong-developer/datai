package com.datai.integration.service;

import java.util.List;
import java.util.Map;
import com.datai.integration.domain.DataiIntegrationBatch;

/**
 * 数据批次Service接口
 * 
 * @author datai
 * @date 2025-12-24
 */
public interface IDataiIntegrationBatchService 
{
    /**
     * 查询数据批次
     * 
     * @param id 数据批次主键
     * @return 数据批次
     */
    public DataiIntegrationBatch selectDataiIntegrationBatchById(Integer id);

    /**
     * 查询数据批次列表
     * 
     * @param dataiIntegrationBatch 数据批次
     * @return 数据批次集合
     */
    public List<DataiIntegrationBatch> selectDataiIntegrationBatchList(DataiIntegrationBatch dataiIntegrationBatch);

    /**
     * 新增数据批次
     * 
     * @param dataiIntegrationBatch 数据批次
     * @return 结果
     */
    public int insertDataiIntegrationBatch(DataiIntegrationBatch dataiIntegrationBatch);

    /**
     * 修改数据批次
     * 
     * @param dataiIntegrationBatch 数据批次
     * @return 结果
     */
    public int updateDataiIntegrationBatch(DataiIntegrationBatch dataiIntegrationBatch);

    /**
     * 批量删除数据批次
     * 
     * @param ids 需要删除的数据批次主键集合
     * @return 结果
     */
    public int deleteDataiIntegrationBatchByIds(Integer[] ids);

    /**
     * 删除数据批次信息
     * 
     * @param id 数据批次主键
     * @return 结果
     */
    public int deleteDataiIntegrationBatchById(Integer id);

    /**
     * 重试失败的批次
     * 
     * @param id 批次ID
     * @return 结果
     */
    public boolean retryFailed(Integer id);

    /**
     * 获取批次同步统计信息
     * 
     * @param id 批次ID
     * @return 统计信息
     */
    public Map<String, Object> getSyncStatistics(Integer id);

    /**
     * 同步批次数据
     * 
     * @param id 批次ID
     * @return 同步结果
     */
    public Map<String, Object> syncBatchData(Integer id);
}
