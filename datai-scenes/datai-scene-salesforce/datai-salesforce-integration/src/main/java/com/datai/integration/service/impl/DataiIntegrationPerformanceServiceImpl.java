package com.datai.integration.service.impl;

import java.util.List;

import com.datai.common.core.domain.model.LoginUser;
import com.datai.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datai.integration.mapper.DataiIntegrationPerformanceMapper;
import com.datai.integration.domain.DataiIntegrationPerformance;
import com.datai.integration.service.IDataiIntegrationPerformanceService;


/**
 * 同步性能监控Service业务层处理
 *
 * @author datai
 * @date 2025-12-22
 */
@Service
public class DataiIntegrationPerformanceServiceImpl implements IDataiIntegrationPerformanceService {
    @Autowired
    private DataiIntegrationPerformanceMapper dataiIntegrationPerformanceMapper;

    /**
     * 查询同步性能监控
     *
     * @param id 同步性能监控主键
     * @return 同步性能监控
     */
    @Override
    public DataiIntegrationPerformance selectDataiIntegrationPerformanceById(Long id)
    {
        return dataiIntegrationPerformanceMapper.selectDataiIntegrationPerformanceById(id);
    }

    /**
     * 查询同步性能监控列表
     *
     * @param dataiIntegrationPerformance 同步性能监控
     * @return 同步性能监控
     */
    @Override
    public List<DataiIntegrationPerformance> selectDataiIntegrationPerformanceList(DataiIntegrationPerformance dataiIntegrationPerformance)
    {
        return dataiIntegrationPerformanceMapper.selectDataiIntegrationPerformanceList(dataiIntegrationPerformance);
    }

    /**
     * 新增同步性能监控
     *
     * @param dataiIntegrationPerformance 同步性能监控
     * @return 结果
     */
    @Override
    public int insertDataiIntegrationPerformance(DataiIntegrationPerformance dataiIntegrationPerformance)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

            return dataiIntegrationPerformanceMapper.insertDataiIntegrationPerformance(dataiIntegrationPerformance);
    }

    /**
     * 修改同步性能监控
     *
     * @param dataiIntegrationPerformance 同步性能监控
     * @return 结果
     */
    @Override
    public int updateDataiIntegrationPerformance(DataiIntegrationPerformance dataiIntegrationPerformance)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

        return dataiIntegrationPerformanceMapper.updateDataiIntegrationPerformance(dataiIntegrationPerformance);
    }

    /**
     * 批量删除同步性能监控
     *
     * @param ids 需要删除的同步性能监控主键
     * @return 结果
     */
    @Override
    public int deleteDataiIntegrationPerformanceByIds(Long[] ids)
    {
        return dataiIntegrationPerformanceMapper.deleteDataiIntegrationPerformanceByIds(ids);
    }

    /**
     * 删除同步性能监控信息
     *
     * @param id 同步性能监控主键
     * @return 结果
     */
    @Override
    public int deleteDataiIntegrationPerformanceById(Long id)
    {
        return dataiIntegrationPerformanceMapper.deleteDataiIntegrationPerformanceById(id);
    }
}
