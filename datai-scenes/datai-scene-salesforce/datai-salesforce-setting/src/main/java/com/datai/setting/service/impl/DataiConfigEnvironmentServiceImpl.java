package com.datai.setting.service.impl;

import java.util.List;
import com.datai.common.utils.DateUtils;
import com.datai.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import com.datai.setting.mapper.DataiConfigEnvironmentMapper;
import com.datai.setting.model.domain.DataiConfigEnvironment;
import com.datai.setting.service.IDataiConfigEnvironmentService;
import com.datai.setting.config.SalesforceConfigCacheManager;
import com.datai.setting.event.EnvironmentSwitchEvent;
import com.datai.common.core.domain.model.LoginUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 配置环境Service业务层处理
 *
 * @author datai
 * @date 2025-12-24
 */
@Service
public class DataiConfigEnvironmentServiceImpl implements IDataiConfigEnvironmentService {
    private static final Logger logger = LoggerFactory.getLogger(DataiConfigEnvironmentServiceImpl.class);

    @Autowired
    private DataiConfigEnvironmentMapper dataiConfigEnvironmentMapper;

    @Autowired
    private SalesforceConfigCacheManager cacheManager;

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 查询配置环境
     *
     * @param id 配置环境主键
     * @return 配置环境
     */
    @Override
    public DataiConfigEnvironment selectDataiConfigEnvironmentById(Long id)
    {
        return dataiConfigEnvironmentMapper.selectDataiConfigEnvironmentById(id);
    }

    /**
     * 查询配置环境列表
     *
     * @param dataiConfigEnvironment 配置环境
     * @return 配置环境
     */
    @Override
    public List<DataiConfigEnvironment> selectDataiConfigEnvironmentList(DataiConfigEnvironment dataiConfigEnvironment)
    {
        return dataiConfigEnvironmentMapper.selectDataiConfigEnvironmentList(dataiConfigEnvironment);
    }

    /**
     * 新增配置环境
     *
     * @param dataiConfigEnvironment 配置环境
     * @return 结果
     */
    @Override
    public int insertDataiConfigEnvironment(DataiConfigEnvironment dataiConfigEnvironment)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiConfigEnvironment.setCreateTime(DateUtils.getNowDate());
                dataiConfigEnvironment.setUpdateTime(DateUtils.getNowDate());
                dataiConfigEnvironment.setCreateBy(username);
                dataiConfigEnvironment.setUpdateBy(username);
            return dataiConfigEnvironmentMapper.insertDataiConfigEnvironment(dataiConfigEnvironment);
    }

    /**
     * 修改配置环境
     *
     * @param dataiConfigEnvironment 配置环境
     * @return 结果
     */
    @Override
    public int updateDataiConfigEnvironment(DataiConfigEnvironment dataiConfigEnvironment)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiConfigEnvironment.setUpdateTime(DateUtils.getNowDate());
                dataiConfigEnvironment.setUpdateBy(username);
        return dataiConfigEnvironmentMapper.updateDataiConfigEnvironment(dataiConfigEnvironment);
    }

    /**
     * 批量删除配置环境
     *
     * @param ids 需要删除的配置环境主键
     * @return 结果
     */
    @Override
    public int deleteDataiConfigEnvironmentByIds(Long[] ids)
    {
        return dataiConfigEnvironmentMapper.deleteDataiConfigEnvironmentByIds(ids);
    }

    /**
     * 删除配置环境信息
     *
     * @param id 配置环境主键
     * @return 结果
     */
    @Override
    public int deleteDataiConfigEnvironmentById(Long id)
    {
        return dataiConfigEnvironmentMapper.deleteDataiConfigEnvironmentById(id);
    }

    @Override
    public boolean switchEnvironment(String environmentCode, String orgType, String switchReason) {
        logger.info("[环境切换] 开始切换环境: {}, ORG类型: {}, 原因: {}", environmentCode, orgType, switchReason);

        try {
            DataiConfigEnvironment oldEnvironment = getCurrentActiveEnvironment(orgType);

            boolean switchResult = cacheManager.switchEnvironment(environmentCode, orgType);

            if (switchResult) {
                DataiConfigEnvironment newEnvironment = getCurrentActiveEnvironment(orgType);

                applicationContext.publishEvent(
                    new EnvironmentSwitchEvent(this, oldEnvironment, newEnvironment, switchReason, orgType)
                );

                logger.info("[环境切换] 环境切换成功: {} -> {}, ORG类型: {}", 
                    oldEnvironment != null ? oldEnvironment.getEnvironmentCode() : "null",
                    newEnvironment != null ? newEnvironment.getEnvironmentCode() : "null",
                    orgType);
            } else {
                logger.error("[环境切换] 环境切换失败: {}, ORG类型: {}", environmentCode, orgType);
            }

            return switchResult;
        } catch (Exception e) {
            logger.error("[环境切换] 环境切换异常: {}, ORG类型: {}, 错误信息: {}", environmentCode, orgType, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public DataiConfigEnvironment getCurrentActiveEnvironment(String orgType) {
        String currentEnvironmentCode = cacheManager.getCurrentEnvironmentCode(orgType);
        if (currentEnvironmentCode == null) {
            logger.warn("[环境查询] 当前环境编码为空, ORG类型: {}", orgType);
            return null;
        }

        DataiConfigEnvironment query = new DataiConfigEnvironment();
        query.setEnvironmentCode(currentEnvironmentCode);
        query.setIsActive(true);
        query.setOrgType(orgType);

        List<DataiConfigEnvironment> environments = dataiConfigEnvironmentMapper.selectDataiConfigEnvironmentList(query);
        if (environments != null && !environments.isEmpty()) {
            return environments.get(0);
        }

        logger.warn("[环境查询] 未找到激活的环境: {}, ORG类型: {}", currentEnvironmentCode, orgType);
        return null;
    }
}
