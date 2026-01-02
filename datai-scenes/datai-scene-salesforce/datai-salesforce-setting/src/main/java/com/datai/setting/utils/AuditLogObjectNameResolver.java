package com.datai.setting.utils;

import com.datai.setting.model.domain.DataiConfigEnvironment;
import com.datai.setting.model.domain.DataiConfigSnapshot;
import com.datai.setting.model.domain.DataiConfiguration;
import com.datai.setting.service.IDataiConfigEnvironmentService;
import com.datai.setting.service.IDataiConfigSnapshotService;
import com.datai.setting.service.IDataiConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuditLogObjectNameResolver {
    private static final Logger logger = LoggerFactory.getLogger(AuditLogObjectNameResolver.class);

    @Autowired
    private IDataiConfigurationService configurationService;

    @Autowired
    private IDataiConfigEnvironmentService environmentService;

    @Autowired
    private IDataiConfigSnapshotService snapshotService;

    public String resolveObjectName(String objectType, Long objectId) {
        if (objectType == null || objectId == null) {
            return null;
        }

        try {
            switch (objectType) {
                case "CONFIGURATION":
                    return resolveConfigurationName(objectId);
                case "ENVIRONMENT":
                    return resolveEnvironmentName(objectId);
                case "SNAPSHOT":
                    return resolveSnapshotName(objectId);
                default:
                    logger.warn("未知的对象类型: {}", objectType);
                    return null;
            }
        } catch (Exception e) {
            logger.error("解析对象名称失败: objectType={}, objectId={}", objectType, objectId, e);
            return null;
        }
    }

    private String resolveConfigurationName(Long configId) {
        try {
            DataiConfiguration config = configurationService.selectDataiConfigurationById(configId);
            if (config != null) {
                return config.getConfigKey();
            }
        } catch (Exception e) {
            logger.error("查询配置名称失败: configId={}", configId, e);
        }
        return null;
    }

    private String resolveEnvironmentName(Long environmentId) {
        try {
            DataiConfigEnvironment environment = environmentService.selectDataiConfigEnvironmentById(environmentId);
            if (environment != null) {
                return environment.getEnvironmentName();
            }
        } catch (Exception e) {
            logger.error("查询环境名称失败: environmentId={}", environmentId, e);
        }
        return null;
    }

    private String resolveSnapshotName(Long snapshotId) {
        try {
            DataiConfigSnapshot snapshot = snapshotService.selectDataiConfigSnapshotById(snapshotId);
            if (snapshot != null) {
                return snapshot.getSnapshotNumber();
            }
        } catch (Exception e) {
            logger.error("查询快照名称失败: snapshotId={}", snapshotId, e);
        }
        return null;
    }
}
