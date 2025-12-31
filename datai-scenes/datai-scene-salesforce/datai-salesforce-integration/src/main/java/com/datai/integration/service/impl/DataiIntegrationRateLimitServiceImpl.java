package com.datai.integration.service.impl;

import java.util.List;
        import com.datai.common.utils.DateUtils;
        import com.datai.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datai.integration.mapper.DataiIntegrationRateLimitMapper;
import com.datai.integration.model.domain.DataiIntegrationRateLimit;
import com.datai.integration.service.IDataiIntegrationRateLimitService;
import com.datai.common.core.domain.model.LoginUser;


/**
 * API限流监控Service业务层处理
 *
 * @author datai
 * @date 2025-12-24
 */
@Service
public class DataiIntegrationRateLimitServiceImpl implements IDataiIntegrationRateLimitService {
    @Autowired
    private DataiIntegrationRateLimitMapper dataiIntegrationRateLimitMapper;

    /**
     * 查询API限流监控
     *
     * @param id API限流监控主键
     * @return API限流监控
     */
    @Override
    public DataiIntegrationRateLimit selectDataiIntegrationRateLimitById(Long id)
    {
        return dataiIntegrationRateLimitMapper.selectDataiIntegrationRateLimitById(id);
    }

    /**
     * 查询API限流监控列表
     *
     * @param dataiIntegrationRateLimit API限流监控
     * @return API限流监控
     */
    @Override
    public List<DataiIntegrationRateLimit> selectDataiIntegrationRateLimitList(DataiIntegrationRateLimit dataiIntegrationRateLimit)
    {
        return dataiIntegrationRateLimitMapper.selectDataiIntegrationRateLimitList(dataiIntegrationRateLimit);
    }

    /**
     * 新增API限流监控
     *
     * @param dataiIntegrationRateLimit API限流监控
     * @return 结果
     */
    @Override
    public int insertDataiIntegrationRateLimit(DataiIntegrationRateLimit dataiIntegrationRateLimit)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiIntegrationRateLimit.setCreateTime(DateUtils.getNowDate());
                dataiIntegrationRateLimit.setUpdateTime(DateUtils.getNowDate());
                dataiIntegrationRateLimit.setCreateBy(username);
                dataiIntegrationRateLimit.setUpdateBy(username);
            return dataiIntegrationRateLimitMapper.insertDataiIntegrationRateLimit(dataiIntegrationRateLimit);
    }

    /**
     * 修改API限流监控
     *
     * @param dataiIntegrationRateLimit API限流监控
     * @return 结果
     */
    @Override
    public int updateDataiIntegrationRateLimit(DataiIntegrationRateLimit dataiIntegrationRateLimit)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiIntegrationRateLimit.setUpdateTime(DateUtils.getNowDate());
                dataiIntegrationRateLimit.setUpdateBy(username);
        return dataiIntegrationRateLimitMapper.updateDataiIntegrationRateLimit(dataiIntegrationRateLimit);
    }

    /**
     * 批量删除API限流监控
     *
     * @param ids 需要删除的API限流监控主键
     * @return 结果
     */
    @Override
    public int deleteDataiIntegrationRateLimitByIds(Long[] ids)
    {
        return dataiIntegrationRateLimitMapper.deleteDataiIntegrationRateLimitByIds(ids);
    }

    /**
     * 删除API限流监控信息
     *
     * @param id API限流监控主键
     * @return 结果
     */
    @Override
    public int deleteDataiIntegrationRateLimitById(Long id)
    {
        return dataiIntegrationRateLimitMapper.deleteDataiIntegrationRateLimitById(id);
    }
}
