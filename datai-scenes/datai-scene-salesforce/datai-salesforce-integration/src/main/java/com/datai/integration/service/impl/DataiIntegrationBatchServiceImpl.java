package com.datai.integration.service.impl;

import java.util.List;

import com.datai.common.core.domain.model.LoginUser;
import com.datai.common.utils.DateUtils;
        import com.datai.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datai.integration.mapper.DataiIntegrationBatchMapper;
import com.datai.integration.domain.DataiIntegrationBatch;
import com.datai.integration.service.IDataiIntegrationBatchService;


/**
 * 数据批次Service业务层处理
 *
 * @author datai
 * @date 2025-12-22
 */
@Service
public class DataiIntegrationBatchServiceImpl implements IDataiIntegrationBatchService {
    @Autowired
    private DataiIntegrationBatchMapper dataiIntegrationBatchMapper;

    /**
     * 查询数据批次
     *
     * @param id 数据批次主键
     * @return 数据批次
     */
    @Override
    public DataiIntegrationBatch selectDataiIntegrationBatchById(Long id)
    {
        return dataiIntegrationBatchMapper.selectDataiIntegrationBatchById(id);
    }

    /**
     * 查询数据批次列表
     *
     * @param dataiIntegrationBatch 数据批次
     * @return 数据批次
     */
    @Override
    public List<DataiIntegrationBatch> selectDataiIntegrationBatchList(DataiIntegrationBatch dataiIntegrationBatch)
    {
        return dataiIntegrationBatchMapper.selectDataiIntegrationBatchList(dataiIntegrationBatch);
    }

    /**
     * 新增数据批次
     *
     * @param dataiIntegrationBatch 数据批次
     * @return 结果
     */
    @Override
    public int insertDataiIntegrationBatch(DataiIntegrationBatch dataiIntegrationBatch)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiIntegrationBatch.setCreateTime(DateUtils.getNowDate());
                dataiIntegrationBatch.setUpdateTime(DateUtils.getNowDate());
                dataiIntegrationBatch.setCreateBy(username);
                dataiIntegrationBatch.setUpdateBy(username);
            return dataiIntegrationBatchMapper.insertDataiIntegrationBatch(dataiIntegrationBatch);
    }

    /**
     * 修改数据批次
     *
     * @param dataiIntegrationBatch 数据批次
     * @return 结果
     */
    @Override
    public int updateDataiIntegrationBatch(DataiIntegrationBatch dataiIntegrationBatch)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiIntegrationBatch.setUpdateTime(DateUtils.getNowDate());
                dataiIntegrationBatch.setUpdateBy(username);
        return dataiIntegrationBatchMapper.updateDataiIntegrationBatch(dataiIntegrationBatch);
    }

    /**
     * 批量删除数据批次
     *
     * @param ids 需要删除的数据批次主键
     * @return 结果
     */
    @Override
    public int deleteDataiIntegrationBatchByIds(Long[] ids)
    {
        return dataiIntegrationBatchMapper.deleteDataiIntegrationBatchByIds(ids);
    }

    /**
     * 删除数据批次信息
     *
     * @param id 数据批次主键
     * @return 结果
     */
    @Override
    public int deleteDataiIntegrationBatchById(Long id)
    {
        return dataiIntegrationBatchMapper.deleteDataiIntegrationBatchById(id);
    }
}
