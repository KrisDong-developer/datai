-- datai-salesforce-config 模块数据库表结构
-- 该模块负责系统配置的管理，包括配置的加载、缓存、更新和变更通知

-- -----------------------------
-- 配置环境表 (datai_config_environment)
-- -----------------------------
-- 用途：存储配置环境信息，支持多环境隔离，如开发、测试、生产等
-- 设计考虑：
-- 1. 支持环境的激活/停用状态管理
-- 2. 环境编码唯一，确保环境标识的一致性
-- 3. 支持租户隔离，满足多租户需求
-- 4. 包含完整的审计字段，便于追溯
CREATE TABLE `datai_config_environment` (
    -- 环境ID，自增主键，唯一标识一个环境
                                            `environment_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '环境ID',
    -- 环境名称，用于显示，如"开发环境"、"生产环境"
                                            `environment_name` VARCHAR(100) NOT NULL COMMENT '环境名称',
    -- 环境编码，用于系统内部标识，如"dev"、"test"、"prod"
                                            `environment_code` VARCHAR(50) NOT NULL COMMENT '环境编码',
    -- 环境描述，详细说明环境的用途和特点
                                            `description` VARCHAR(500) DEFAULT NULL COMMENT '环境描述',
    -- 是否激活，1:激活，0:停用，激活状态的环境才能被使用
                                            `is_active` INT(1) NOT NULL DEFAULT '1' COMMENT '是否激活',
    -- 创建人，记录创建环境的用户
                                            `create_by` VARCHAR(64) NOT NULL COMMENT '创建人',
    -- 创建时间，记录环境创建的时间
                                            `create_time` DATETIME NOT NULL COMMENT '创建时间',
    -- 更新人，记录最后更新环境的用户
                                            `update_by` VARCHAR(64) NOT NULL COMMENT '更新人',
    -- 更新时间，记录环境最后更新的时间
                                            `update_time` DATETIME NOT NULL COMMENT '更新时间',
    -- 备注，存储环境的额外信息
                                            `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    -- 租户编号，支持多租户隔离，不同租户的环境相互独立
                                            `tenant_id` VARCHAR(32) DEFAULT NULL COMMENT '租户编号',
    -- 主键索引，唯一标识环境记录
                                            PRIMARY KEY (`environment_id`) COMMENT '主键索引',
    -- 唯一约束，确保环境编码在系统中唯一
                                            UNIQUE KEY `uk_environment_code` (`environment_code`) COMMENT '唯一约束',
    -- 普通索引，加速环境名称的查询
                                            KEY `idx_environment_name` (`environment_name`) COMMENT '普通索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='配置环境表';


-- -----------------------------
-- 配置表 (datai_configuration)
-- -----------------------------
-- 用途：存储实际的配置键值对，是配置管理的核心表
-- 设计考虑：
-- 1. 支持环境隔离，同一配置键在不同环境下可以有不同的值
-- 2. 支持敏感配置加密存储，提高系统安全性
-- 3. 支持配置的激活/停用状态管理
-- 4. 支持租户隔离，满足多租户需求
-- 5. 包含完整的审计字段，便于追溯
CREATE TABLE `datai_configuration` (
    -- 配置ID，自增主键，唯一标识一个配置项
                                       `config_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '配置ID',
    -- 配置键，系统中唯一标识一个配置，如"salesforce.api.version"、"salesforce.login.url"
                                       `config_key` VARCHAR(255) NOT NULL COMMENT '配置键',
    -- 配置值，配置项的实际值，敏感配置会被加密存储
                                       `config_value` TEXT COMMENT '配置值',
    -- 环境ID，关联到配置环境表，支持多环境隔离，NULL表示全局配置
                                       `environment_id` BIGINT(20) DEFAULT NULL COMMENT '环境ID',
    -- 是否敏感配置，1:是，0:否，敏感配置会被特殊处理和显示
                                       `is_sensitive` INT(1) NOT NULL DEFAULT '0' COMMENT '是否敏感配置',
    -- 是否加密存储，1:是，0:否，敏感配置会被加密后存储
                                       `is_encrypted` INT(1) NOT NULL DEFAULT '0' COMMENT '是否加密存储',
    -- 配置描述，详细说明配置项的用途和使用方法
                                       `description` VARCHAR(500) DEFAULT NULL COMMENT '配置描述',
    -- 是否激活，1:激活，0:停用，激活状态的配置才能被使用
                                       `is_active` INT(1) NOT NULL DEFAULT '1' COMMENT '是否激活',
    -- 创建人，记录创建配置的用户
                                       `create_by` VARCHAR(64) NOT NULL COMMENT '创建人',
    -- 创建时间，记录配置创建的时间
                                       `create_time` DATETIME NOT NULL COMMENT '创建时间',
    -- 更新人，记录最后更新配置的用户
                                       `update_by` VARCHAR(64) NOT NULL COMMENT '更新人',
    -- 更新时间，记录配置最后更新的时间
                                       `update_time` DATETIME NOT NULL COMMENT '更新时间',
    -- 备注，存储配置的额外信息
                                       `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    -- 租户编号，支持多租户隔离，不同租户的配置相互独立
                                       `tenant_id` VARCHAR(32) DEFAULT NULL COMMENT '租户编号',
    -- 主键索引，唯一标识配置记录
                                       PRIMARY KEY (`config_id`) COMMENT '主键索引',
    -- 唯一约束，确保同一环境下配置键唯一
                                       UNIQUE KEY `uk_config_key_environment` (`config_key`,`environment_id`) COMMENT '唯一约束',
    -- 普通索引，加速按环境查询配置
                                       KEY `idx_environment_id` (`environment_id`) COMMENT '普通索引',
    -- 普通索引，加速按配置键查询配置
                                       KEY `idx_config_key` (`config_key`) COMMENT '普通索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='配置表';

-- -----------------------------
-- 配置版本表 (datai_config_version)
-- -----------------------------
-- 用途：存储配置版本信息，支持配置的版本控制和回滚
-- 设计考虑：
-- 1. 支持版本号、版本描述的管理
-- 2. 支持版本状态的生命周期管理（已创建、已发布、已废弃）
-- 3. 关联配置快照，支持配置的完整回滚
-- 4. 支持租户隔离，满足多租户需求
-- 5. 包含完整的审计字段，便于追溯
CREATE TABLE `datai_config_version` (
    -- 版本ID，自增主键，唯一标识一个配置版本
                                        `version_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '版本ID',
    -- 版本号，如"1.0.0"、"1.1.0"，遵循语义化版本规范
                                        `version_number` VARCHAR(50) NOT NULL COMMENT '版本号',
    -- 版本描述，详细说明该版本的变更内容和用途
                                        `version_desc` VARCHAR(500) DEFAULT NULL COMMENT '版本描述',
    -- 快照ID，关联到配置快照表，存储版本对应的配置快照
                                        `snapshot_id` VARCHAR(36) DEFAULT NULL COMMENT '快照ID',
    -- 配置快照内容，JSON格式存储，包含该版本的完整配置信息
                                        `snapshot_content` LONGTEXT COMMENT '快照内容',
    -- 版本状态：CREATED（已创建）、PUBLISHED（已发布）、DEPRECATED（已废弃）
                                        `status` VARCHAR(20) NOT NULL COMMENT '版本状态',
    -- 发布时间，记录版本发布的时间
                                        `publish_time` DATETIME DEFAULT NULL COMMENT '发布时间',
    -- 创建人，记录创建版本的用户
                                        `create_by` VARCHAR(64) NOT NULL COMMENT '创建人',
    -- 创建时间，记录版本创建的时间
                                        `create_time` DATETIME NOT NULL COMMENT '创建时间',
    -- 更新人，记录最后更新版本的用户
                                        `update_by` VARCHAR(64) NOT NULL COMMENT '更新人',
    -- 更新时间，记录版本最后更新的时间
                                        `update_time` DATETIME NOT NULL COMMENT '更新时间',
    -- 备注，存储版本的额外信息
                                        `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    -- 租户编号，支持多租户隔离，不同租户的版本相互独立
                                        `tenant_id` VARCHAR(32) DEFAULT NULL COMMENT '租户编号',
    -- 主键索引，唯一标识版本记录
                                        PRIMARY KEY (`version_id`) COMMENT '主键索引',
    -- 唯一约束，确保版本号在系统中唯一
                                        UNIQUE KEY `uk_version_number` (`version_number`) COMMENT '唯一约束',
    -- 普通索引，加速按状态查询版本
                                        KEY `idx_status` (`status`) COMMENT '普通索引',
    -- 普通索引，加速按创建时间查询版本
                                        KEY `idx_create_time` (`create_time`) COMMENT '普通索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='配置版本表';

-- -----------------------------
-- 配置审计日志表 (datai_config_audit_log)
-- -----------------------------
-- 用途：记录所有配置操作的审计日志，用于系统安全审计和操作追溯
-- 设计考虑：
-- 1. 记录操作类型、对象类型、操作人、操作时间等关键信息
-- 2. 支持操作结果和错误信息的记录
-- 3. 记录操作前后的值变化，便于审计和问题排查
-- 4. 支持按多种条件查询，如操作类型、对象类型、操作人、操作时间等
-- 5. 支持租户隔离，满足多租户需求
CREATE TABLE `datai_config_audit_log` (
    -- 日志ID，自增主键，唯一标识一条审计日志
                                          `log_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    -- 操作类型：CREATE（创建）、UPDATE（更新）、DELETE（删除）、PUBLISH（发布）、ROLLBACK（回滚）、IMPORT（导入）、EXPORT（导出）
                                          `operation_type` VARCHAR(20) NOT NULL COMMENT '操作类型',
    -- 操作对象类型：DATAI_CONFIGURATION（配置）、DATAI_CONFIG_VERSION（版本）、DATAI_CONFIG_ENVIRONMENT（环境）
                                          `object_type` VARCHAR(50) NOT NULL COMMENT '对象类型',
    -- 操作对象ID，关联到具体的操作对象
                                          `object_id` BIGINT(20) DEFAULT NULL COMMENT '对象ID',
    -- 旧值，操作前的对象值
                                          `old_value` TEXT COMMENT '旧值',
    -- 新值，操作后的对象值
                                          `new_value` TEXT COMMENT '新值',
    -- 操作描述，详细说明操作的内容和目的
                                          `operation_desc` VARCHAR(500) DEFAULT NULL COMMENT '操作描述',
    -- 操作人，记录执行操作的用户
                                          `operator` VARCHAR(64) NOT NULL COMMENT '操作人',
    -- 操作时间，记录操作执行的时间
                                          `operation_time` DATETIME NOT NULL COMMENT '操作时间',
    -- 操作IP地址，记录执行操作的客户端IP
                                          `ip_address` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
    -- 用户代理，记录执行操作的客户端信息
                                          `user_agent` VARCHAR(255) DEFAULT NULL COMMENT '用户代理',
    -- 请求ID，用于关联同一请求的多个操作日志
                                          `request_id` VARCHAR(36) DEFAULT NULL COMMENT '请求ID',
    -- 操作结果：SUCCESS（成功）、FAILED（失败）
                                          `result` VARCHAR(20) NOT NULL DEFAULT 'SUCCESS' COMMENT '操作结果',
    -- 错误信息，记录操作失败时的错误详情
                                          `error_message` TEXT DEFAULT NULL COMMENT '错误信息',
    -- 租户编号，支持多租户隔离，不同租户的日志相互独立
                                          `tenant_id` VARCHAR(32) DEFAULT NULL COMMENT '租户编号',
    -- 主键索引，唯一标识日志记录
                                          PRIMARY KEY (`log_id`) COMMENT '主键索引',
    -- 普通索引，加速按操作类型查询日志
                                          KEY `idx_operation_type` (`operation_type`) COMMENT '普通索引',
    -- 普通索引，加速按对象类型查询日志
                                          KEY `idx_object_type` (`object_type`) COMMENT '普通索引',
    -- 普通索引，加速按操作人查询日志
                                          KEY `idx_operator` (`operator`) COMMENT '普通索引',
    -- 普通索引，加速按操作时间查询日志
                                          KEY `idx_operation_time` (`operation_time`) COMMENT '普通索引',
    -- 普通索引，加速按请求ID查询日志
                                          KEY `idx_request_id` (`request_id`) COMMENT '普通索引',
    -- 普通索引，加速按对象ID查询日志
                                          KEY `idx_object_id` (`object_id`) COMMENT '普通索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='配置审计日志表';

-- -----------------------------
-- 配置快照表 (datai_config_snapshot)
-- -----------------------------
-- 用途：存储配置版本的快照内容，支持配置的完整回滚和历史查询
-- 设计考虑：
-- 1. 使用UUID作为主键，确保分布式环境下的唯一性
-- 2. 关联配置版本，支持版本与快照的一对一关系
-- 3. 采用JSON格式存储完整的配置快照，便于恢复
-- 4. 记录快照时间和配置/分组数量，便于快速查询
-- 5. 支持租户隔离，满足多租户需求
-- 6. 包含完整的审计字段，便于追溯
CREATE TABLE `datai_config_snapshot` (
    -- 快照ID，UUID格式，唯一标识一个配置快照
                                         `snapshot_id` VARCHAR(36) NOT NULL COMMENT '快照ID',
    -- 关联版本ID，关联到配置版本表，建立版本与快照的关联
                                         `version_id` BIGINT(20) NOT NULL COMMENT '版本ID',
    -- 快照时间，记录快照创建的时间
                                         `snapshot_time` DATETIME NOT NULL COMMENT '快照时间',
    -- 快照内容，JSON格式存储，包含该版本的完整配置信息，包括配置项、环境等
                                         `snapshot_content` LONGTEXT NOT NULL COMMENT '快照内容',
    -- 配置数量，快照中包含的配置项数量，用于快速查询和验证
                                         `config_count` INT(11) NOT NULL COMMENT '配置数量',
    -- 创建人，记录创建快照的用户
                                         `create_by` VARCHAR(64) NOT NULL COMMENT '创建人',
    -- 创建时间，记录快照创建的时间
                                         `create_time` DATETIME NOT NULL COMMENT '创建时间',
    -- 备注，存储快照的额外信息
                                         `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    -- 租户编号，支持多租户隔离，不同租户的快照相互独立
                                         `tenant_id` VARCHAR(32) DEFAULT NULL COMMENT '租户编号',
    -- 主键索引，唯一标识快照记录
                                         PRIMARY KEY (`snapshot_id`) COMMENT '主键索引',
    -- 普通索引，加速按版本ID查询快照
                                         KEY `idx_version_id` (`version_id`) COMMENT '普通索引',
    -- 普通索引，加速按快照时间查询快照
                                         KEY `idx_snapshot_time` (`snapshot_time`) COMMENT '普通索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='配置快照表';

