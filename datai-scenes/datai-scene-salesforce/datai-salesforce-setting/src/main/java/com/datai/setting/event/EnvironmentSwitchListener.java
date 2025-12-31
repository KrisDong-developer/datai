package com.datai.setting.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.datai.common.utils.DateUtils;
import com.datai.common.utils.SecurityUtils;
import com.datai.setting.config.SalesforceConfigCacheManager;
import com.datai.setting.model.domain.DataiConfigEnvironment;
import com.datai.setting.model.domain.DataiConfiguration;
import com.datai.setting.service.IDataiConfigurationService;

import java.util.List;

/**
 * 环境切换监听器
 * 监听环境切换事件并执行相应的处理逻辑
 *
 * @author datai
 * @date 2025-12-25
 */
@Component
public class EnvironmentSwitchListener {

    private static final Logger logger = LoggerFactory.getLogger(EnvironmentSwitchListener.class);

    @Autowired
    private SalesforceConfigCacheManager cacheManager;

    @Autowired
    private IDataiConfigurationService configurationService;

    @Autowired
    private IDataiConfigurationService configService;

    @Async
    @EventListener
    public void handleEnvironmentSwitch(EnvironmentSwitchEvent event) {
        long startTime = System.currentTimeMillis();
        DataiConfigEnvironment oldEnv = event.getOldEnvironment();
        DataiConfigEnvironment newEnv = event.getNewEnvironment();
        String switchReason = event.getSwitchReason();

        logger.info("[环境切换] 开始处理环境切换事件: {} -> {}, 原因: {}, 开始时间: {}",
                oldEnv != null ? oldEnv.getEnvironmentCode() : "null",
                newEnv != null ? newEnv.getEnvironmentCode() : "null",
                switchReason,
                DateUtils.getNowDate());

        try {
            String username = SecurityUtils.getLoginUser() != null ? 
                    SecurityUtils.getLoginUser().getUsername() : "system";

            logger.info("[环境切换] 操作用户: {}", username);
            logger.info("[环境切换] 切换原因: {}", switchReason);

            if (oldEnv != null) {
                logger.info("[环境切换] 旧环境信息: ID={}, 名称={}, 编码={}",
                        oldEnv.getId(), oldEnv.getEnvironmentName(), oldEnv.getEnvironmentCode());
            }

            if (newEnv != null) {
                logger.info("[环境切换] 新环境信息: ID={}, 名称={}, 编码={}",
                        newEnv.getId(), newEnv.getEnvironmentName(), newEnv.getEnvironmentCode());
            }

            logger.info("[环境切换] 开始执行环境切换...");
            if (newEnv != null) {
                boolean switchSuccess = cacheManager.switchEnvironment(newEnv.getEnvironmentCode());
                
                if (switchSuccess) {
                    logger.info("[环境切换] 环境切换成功: {}", newEnv.getEnvironmentCode());

                    DataiConfiguration query = new DataiConfiguration();
                    query.setEnvironmentId(newEnv.getId());
                    query.setIsActive(true);
                    List<DataiConfiguration> configs = configService.selectDataiConfigurationList(query);
                    logger.info("[环境切换] 新环境配置数量: {}", configs.size());
                } else {
                    logger.error("[环境切换] 环境切换失败: {}", newEnv.getEnvironmentCode());
                    throw new RuntimeException("环境切换失败");
                }
            }

            long endTime = System.currentTimeMillis();
            logger.info("[环境切换] 环境切换处理完成，总耗时: {}ms", (endTime - startTime));
            logger.info("[环境切换] 当前环境: {}", cacheManager.getCurrentEnvironmentCode());

        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            logger.error("[环境切换] 环境切换处理失败，开始时间: {}, 耗时: {}ms, 错误信息: {}",
                    startTime, (endTime - startTime), e.getMessage(), e);
            throw new RuntimeException("环境切换处理失败", e);
        }
    }
}
