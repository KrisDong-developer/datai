package com.datai.integration.service;

import java.util.List;
import java.util.Map;
import com.datai.integration.model.domain.DataiIntegrationBatchHistory;

/**
 * 数据批次历史Service接口
 * 
 * @author datai
 * @date 2025-12-26
 */
public interface IDataiIntegrationBatchHistoryService 
{
    /**
     * 查询数据批次历史
     * 
     * @param id 数据批次历史主键
     * @return 数据批次历史
     */
    public DataiIntegrationBatchHistory selectDataiIntegrationBatchHistoryById(Integer id);

    /**
     * 查询数据批次历史列表
     * 
     * @param dataiIntegrationBatchHistory 数据批次历史
     * @return 数据批次历史集合
     */
    public List<DataiIntegrationBatchHistory> selectDataiIntegrationBatchHistoryList(DataiIntegrationBatchHistory dataiIntegrationBatchHistory);

    /**
     * 新增数据批次历史
     * 
     * @param dataiIntegrationBatchHistory 数据批次历史
     * @return 结果
     */
    public int insertDataiIntegrationBatchHistory(DataiIntegrationBatchHistory dataiIntegrationBatchHistory);

    /**
     * 修改数据批次历史
     * 
     * @param dataiIntegrationBatchHistory 数据批次历史
     * @return 结果
     */
    public int updateDataiIntegrationBatchHistory(DataiIntegrationBatchHistory dataiIntegrationBatchHistory);

    /**
     * 批量删除数据批次历史
     * 
     * @param ids 需要删除的数据批次历史主键集合
     * @return 结果
     */
    public int deleteDataiIntegrationBatchHistoryByIds(Integer[] ids);

    /**
     * 删除数据批次历史信息
     * 
     * @param id 数据批次历史主键
     * @return 结果
     */
    public int deleteDataiIntegrationBatchHistoryById(Integer id);

    /**
     * 获取历史统计信息
     * 
     * @param params 查询参数
     * @return 统计信息
     */
    public Map<String, Object> getHistoryStatistics(Map<String, Object> params);
}
