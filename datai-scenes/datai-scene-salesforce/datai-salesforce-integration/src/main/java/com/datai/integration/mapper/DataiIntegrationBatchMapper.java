package com.datai.integration.mapper;

import java.util.List;
import com.datai.integration.domain.DataiIntegrationBatch;

/**
 * 数据批次Mapper接口
 * 
 * @author datai
 * @date 2025-12-22
 */
public interface DataiIntegrationBatchMapper 
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
     * 删除数据批次
     * 
     * @param id 数据批次主键
     * @return 结果
     */
    public int deleteDataiIntegrationBatchById(Long id);

    /**
     * 批量删除数据批次
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDataiIntegrationBatchByIds(Long[] ids);
}
