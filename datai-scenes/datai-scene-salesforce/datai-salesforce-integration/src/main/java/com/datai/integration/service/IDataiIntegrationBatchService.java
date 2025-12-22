package com.datai.integration.service;

import java.util.List;
import com.datai.integration.domain.DataiIntegrationBatch;

/**
 * 数据批次Service接口
 * 
 * @author datai
 * @date 2025-12-22
 */
public interface IDataiIntegrationBatchService 
{
    /**
     * 查询数据批次
     * 
     * @param id 数据批次主键
     * @return 数据批次
     */
    public DataiIntegrationBatch selectDataiIntegrationBatchById(Long id);

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
    public int deleteDataiIntegrationBatchByIds(Long[] ids);

    /**
     * 删除数据批次信息
     * 
     * @param id 数据批次主键
     * @return 结果
     */
    public int deleteDataiIntegrationBatchById(Long id);
}
