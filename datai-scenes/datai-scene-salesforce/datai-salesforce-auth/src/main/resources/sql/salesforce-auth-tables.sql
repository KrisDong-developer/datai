-- datai-salesforce-auth 模块数据库表结构
-- 该模块负责Salesforce登录认证和令牌管理，是所有需要访问Salesforce API的模块的基础

-- -----------------------------
-- 登录会话表 (datai_sf_login_session)
-- -----------------------------
-- 用途：存储登录会话信息，用于会话管理和令牌刷新
-- 设计考虑：
-- 1. 支持多种登录方式
-- 2. 支持会话状态管理
-- 3. 支持令牌自动刷新
-- 4. 支持多租户隔离
CREATE TABLE `datai_sf_login_session` (
  -- 会话ID，自增主键，唯一标识一个登录会话
  `session_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '会话ID',
  -- 租户编号，支持多租户隔离
  `tenant_id` VARCHAR(32) DEFAULT NULL COMMENT '租户编号',
  -- 用户名，登录用户的Salesforce用户名
  `username` VARCHAR(255) NOT NULL COMMENT '用户名',
  -- 登录类型，如oauth2、salesforce_cli、legacy_credential等
  `login_type` VARCHAR(50) NOT NULL COMMENT '登录类型',
  -- 会话状态，如ACTIVE、EXPIRED、REVOKED、INVALID
  `status` VARCHAR(20) NOT NULL COMMENT '会话状态',
  -- 登录时间，会话创建时间
  `login_time` DATETIME NOT NULL COMMENT '登录时间',
  -- 过期时间，会话过期时间
  `expire_time` DATETIME NOT NULL COMMENT '过期时间',
  -- 最后活动时间，会话最后活跃时间
  `last_activity_time` DATETIME NOT NULL COMMENT '最后活动时间',
  -- 登录IP地址，用户登录时的IP地址
  `login_ip` VARCHAR(50) DEFAULT NULL COMMENT '登录IP',
  -- 登录设备信息，用户登录时的设备信息
  `device_info` VARCHAR(500) DEFAULT NULL COMMENT '设备信息',
  -- 浏览器信息，用户登录时的浏览器信息
  `browser_info` VARCHAR(500) DEFAULT NULL COMMENT '浏览器信息',
  -- 会话标识，用于标识会话的唯一字符串
  `session_id` VARCHAR(255) DEFAULT NULL COMMENT '会话标识',
  -- 关联的令牌ID，关联到令牌表
  `token_id` BIGINT(20) DEFAULT NULL COMMENT '令牌ID',
  -- 创建人，记录创建会话的用户
  `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
  -- 创建时间，记录会话创建的时间
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  -- 更新人，记录最后更新会话的用户
  `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新人',
  -- 更新时间，记录会话最后更新的时间
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  -- 主键索引，唯一标识会话记录
  PRIMARY KEY (`session_id`),
  -- 普通索引，加速按用户名查询会话
  KEY `idx_username` (`username`),
  -- 普通索引，加速按登录类型查询会话
  KEY `idx_login_type` (`login_type`),
  -- 普通索引，加速按会话状态查询会话
  KEY `idx_status` (`status`),
  -- 普通索引，加速按过期时间查询会话
  KEY `idx_expire_time` (`expire_time`),
  -- 普通索引，加速按租户编号查询会话
  KEY `idx_tenant_id` (`tenant_id`),
  -- 普通索引，加速按令牌ID查询会话
  KEY `idx_token_id` (`token_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录会话表，存储登录会话信息，用于会话管理和令牌刷新';

-- -----------------------------
-- 令牌表 (datai_sf_token)
-- -----------------------------
-- 用途：存储访问令牌和刷新令牌信息，用于令牌管理和验证
-- 设计考虑：
-- 1. 支持访问令牌和刷新令牌的存储
-- 2. 支持令牌状态管理
-- 3. 支持令牌过期时间管理
-- 4. 支持多租户隔离
CREATE TABLE `datai_sf_token` (
  -- 令牌ID，自增主键，唯一标识一个令牌记录
  `token_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '令牌ID',
  -- 租户编号，支持多租户隔离
  `tenant_id` VARCHAR(32) DEFAULT NULL COMMENT '租户编号',
  -- 用户名，令牌所属用户的Salesforce用户名
  `username` VARCHAR(255) NOT NULL COMMENT '用户名',
  -- 访问令牌，用于访问Salesforce API
  `access_token` VARCHAR(1024) NOT NULL COMMENT '访问令牌',
  -- 刷新令牌，用于刷新访问令牌
  `refresh_token` VARCHAR(1024) DEFAULT NULL COMMENT '刷新令牌',
  -- 访问令牌过期时间
  `access_token_expire` DATETIME NOT NULL COMMENT '访问令牌过期时间',
  -- 刷新令牌过期时间
  `refresh_token_expire` DATETIME DEFAULT NULL COMMENT '刷新令牌过期时间',
  -- 令牌状态，如ACTIVE、EXPIRED、REVOKED、INVALID
  `status` VARCHAR(20) NOT NULL COMMENT '令牌状态',
  -- 实例URL，Salesforce实例URL
  `instance_url` VARCHAR(255) NOT NULL COMMENT '实例URL',
  -- 登录类型，如oauth2、salesforce_cli、legacy_credential等
  `login_type` VARCHAR(50) NOT NULL COMMENT '登录类型',
  -- 客户端ID，OAuth应用的客户端ID
  `client_id` VARCHAR(255) DEFAULT NULL COMMENT '客户端ID',
  -- 作用域，令牌的作用域
  `scope` VARCHAR(255) DEFAULT NULL COMMENT '作用域',
  -- 令牌类型，如Bearer
  `token_type` VARCHAR(50) DEFAULT NULL COMMENT '令牌类型',
  -- 组织ID，Salesforce组织ID
  `organization_id` VARCHAR(50) DEFAULT NULL COMMENT '组织ID',
  -- 用户ID，Salesforce用户ID
  `user_id` VARCHAR(50) DEFAULT NULL COMMENT '用户ID',
  -- 创建人，记录创建令牌的用户
  `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
  -- 创建时间，记录令牌创建的时间
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  -- 更新人，记录最后更新令牌的用户
  `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新人',
  -- 更新时间，记录令牌最后更新的时间
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  -- 主键索引，唯一标识令牌记录
  PRIMARY KEY (`token_id`),
  -- 普通索引，加速按用户名查询令牌
  KEY `idx_username` (`username`),
  -- 普通索引，加速按访问令牌查询令牌
  KEY `idx_access_token` (`access_token`(255)),
  -- 普通索引，加速按刷新令牌查询令牌
  KEY `idx_refresh_token` (`refresh_token`(255)),
  -- 普通索引，加速按令牌状态查询令牌
  KEY `idx_status` (`status`),
  -- 普通索引，加速按访问令牌过期时间查询令牌
  KEY `idx_access_expire` (`access_token_expire`),
  -- 普通索引，加速按刷新令牌过期时间查询令牌
  KEY `idx_refresh_expire` (`refresh_token_expire`),
  -- 普通索引，加速按租户编号查询令牌
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='令牌表，存储访问令牌和刷新令牌信息，用于令牌管理和验证';

-- -----------------------------
-- 登录审计日志表 (datai_sf_login_audit)
-- -----------------------------
-- 用途：记录登录相关操作，用于审计和安全分析
-- 设计考虑：
-- 1. 支持多种操作类型的记录
-- 2. 支持登录结果的记录
-- 3. 支持详细的操作上下文信息
-- 4. 支持多租户隔离
CREATE TABLE `datai_sf_login_audit` (
  -- 日志ID，自增主键，唯一标识一条审计日志
  `audit_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  -- 租户编号，支持多租户隔离
  `tenant_id` VARCHAR(32) DEFAULT NULL COMMENT '租户编号',
  -- 用户名，操作相关的用户名
  `username` VARCHAR(255) DEFAULT NULL COMMENT '用户名',
  -- 操作类型，如LOGIN、LOGOUT、REFRESH_TOKEN、VALIDATE_TOKEN、REVOKE_TOKEN等
  `operation_type` VARCHAR(50) NOT NULL COMMENT '操作类型',
  -- 操作结果，如SUCCESS、FAILED、REJECTED
  `result` VARCHAR(20) NOT NULL COMMENT '操作结果',
  -- 操作时间，记录操作执行的时间
  `operation_time` DATETIME NOT NULL COMMENT '操作时间',
  -- 操作IP地址，记录执行操作的客户端IP
  `ip_address` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
  -- 设备信息，记录执行操作的设备信息
  `device_info` VARCHAR(500) DEFAULT NULL COMMENT '设备信息',
  -- 浏览器信息，记录执行操作的浏览器信息
  `browser_info` VARCHAR(500) DEFAULT NULL COMMENT '浏览器信息',
  -- 登录类型，如oauth2、salesforce_cli、legacy_credential等
  `login_type` VARCHAR(50) DEFAULT NULL COMMENT '登录类型',
  -- 错误信息，记录操作失败时的错误详情
  `error_message` TEXT DEFAULT NULL COMMENT '错误信息',
  -- 会话ID，关联到登录会话表
  `session_id` BIGINT(20) DEFAULT NULL COMMENT '会话ID',
  -- 令牌ID，关联到令牌表
  `token_id` BIGINT(20) DEFAULT NULL COMMENT '令牌ID',
  -- 请求ID，用于关联同一请求的多个操作日志
  `request_id` VARCHAR(36) DEFAULT NULL COMMENT '请求ID',
  -- 创建人，记录创建日志的用户
  `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
  -- 创建时间，记录日志创建的时间
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  -- 主键索引，唯一标识审计日志记录
  PRIMARY KEY (`audit_id`),
  -- 普通索引，加速按用户名查询审计日志
  KEY `idx_username` (`username`),
  -- 普通索引，加速按操作类型查询审计日志
  KEY `idx_operation_type` (`operation_type`),
  -- 普通索引，加速按操作结果查询审计日志
  KEY `idx_result` (`result`),
  -- 普通索引，加速按操作时间查询审计日志
  KEY `idx_operation_time` (`operation_time`),
  -- 普通索引，加速按登录类型查询审计日志
  KEY `idx_login_type` (`login_type`),
  -- 普通索引，加速按租户编号查询审计日志
  KEY `idx_tenant_id` (`tenant_id`),
  -- 普通索引，加速按会话ID查询审计日志
  KEY `idx_session_id` (`session_id`),
  -- 普通索引，加速按令牌ID查询审计日志
  KEY `idx_token_id` (`token_id`),
  -- 普通索引，加速按请求ID查询审计日志
  KEY `idx_request_id` (`request_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录审计日志表，记录登录相关操作，用于审计和安全分析';

-- -----------------------------
-- 令牌绑定表 (datai_sf_token_binding)
-- -----------------------------
-- 用途：记录令牌与设备/IP的绑定关系，用于提高安全性
-- 设计考虑：
-- 1. 支持令牌与设备的绑定
-- 2. 支持令牌与IP的绑定
-- 3. 支持绑定状态管理
-- 4. 支持多租户隔离
CREATE TABLE `datai_sf_token_binding` (
  -- 绑定ID，自增主键，唯一标识一个令牌绑定
  `binding_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '绑定ID',
  -- 租户编号，支持多租户隔离
  `tenant_id` VARCHAR(32) DEFAULT NULL COMMENT '租户编号',
  -- 令牌ID，关联到令牌表
  `token_id` BIGINT(20) NOT NULL COMMENT '令牌ID',
  -- 绑定类型，如DEVICE、IP、DEVICE_IP
  `binding_type` VARCHAR(20) NOT NULL COMMENT '绑定类型',
  -- 设备ID，令牌绑定的设备标识
  `device_id` VARCHAR(255) DEFAULT NULL COMMENT '设备ID',
  -- 绑定IP地址，令牌绑定的IP地址
  `binding_ip` VARCHAR(50) DEFAULT NULL COMMENT '绑定IP',
  -- 绑定状态，如ACTIVE、EXPIRED、REVOKED
  `status` VARCHAR(20) NOT NULL COMMENT '绑定状态',
  -- 绑定时间，记录令牌绑定的时间
  `binding_time` DATETIME NOT NULL COMMENT '绑定时间',
  -- 过期时间，记录令牌绑定的过期时间
  `expire_time` DATETIME DEFAULT NULL COMMENT '过期时间',
  -- 创建人，记录创建令牌绑定的用户
  `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
  -- 创建时间，记录令牌绑定创建的时间
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  -- 更新人，记录最后更新令牌绑定的用户
  `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新人',
  -- 更新时间，记录令牌绑定最后更新的时间
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  -- 主键索引，唯一标识令牌绑定记录
  PRIMARY KEY (`binding_id`),
  -- 普通索引，加速按令牌ID查询令牌绑定
  KEY `idx_token_id` (`token_id`),
  -- 普通索引，加速按绑定类型查询令牌绑定
  KEY `idx_binding_type` (`binding_type`),
  -- 普通索引，加速按设备ID查询令牌绑定
  KEY `idx_device_id` (`device_id`),
  -- 普通索引，加速按绑定IP查询令牌绑定
  KEY `idx_binding_ip` (`binding_ip`),
  -- 普通索引，加速按绑定状态查询令牌绑定
  KEY `idx_status` (`status`),
  -- 普通索引，加速按租户编号查询令牌绑定
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='令牌绑定表，记录令牌与设备/IP的绑定关系，用于提高安全性';

-- -----------------------------
-- 失败登录表 (datai_sf_failed_login)
-- -----------------------------
-- 用途：记录登录失败尝试，用于登录锁定和风险检测
-- 设计考虑：
-- 1. 支持记录登录失败原因
-- 2. 支持按用户名和IP统计失败次数
-- 3. 支持登录锁定机制
-- 4. 支持多租户隔离
CREATE TABLE `datai_sf_failed_login` (
  -- 记录ID，自增主键，唯一标识一条失败登录记录
  `failed_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  -- 租户编号，支持多租户隔离
  `tenant_id` VARCHAR(32) DEFAULT NULL COMMENT '租户编号',
  -- 用户名，登录失败的用户名
  `username` VARCHAR(255) DEFAULT NULL COMMENT '用户名',
  -- 登录类型，如password、jwt、client_credentials等
  `login_type` VARCHAR(50) DEFAULT NULL COMMENT '登录类型',
  -- 失败时间，记录登录失败的时间
  `failed_time` DATETIME NOT NULL COMMENT '失败时间',
  -- 失败原因，说明登录失败的具体原因
  `failed_reason` TEXT DEFAULT NULL COMMENT '失败原因',
  -- 操作IP地址，记录登录失败的IP地址
  `ip_address` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
  -- 设备信息，记录登录失败的设备信息
  `device_info` VARCHAR(500) DEFAULT NULL COMMENT '设备信息',
  -- 锁定状态，如LOCKED、UNLOCKED
  `lock_status` VARCHAR(20) NOT NULL DEFAULT 'UNLOCKED' COMMENT '锁定状态',
  -- 锁定时间，记录登录锁定的时间
  `lock_time` DATETIME DEFAULT NULL COMMENT '锁定时间',
  -- 解锁时间，记录登录解锁的时间
  `unlock_time` DATETIME DEFAULT NULL COMMENT '解锁时间',
  -- 锁定原因，说明登录锁定的原因
  `lock_reason` TEXT DEFAULT NULL COMMENT '锁定原因',
  -- 创建人，记录创建失败登录记录的用户
  `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
  -- 创建时间，记录失败登录记录创建的时间
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  -- 主键索引，唯一标识失败登录记录
  PRIMARY KEY (`failed_id`),
  -- 普通索引，加速按用户名查询失败登录记录
  KEY `idx_username` (`username`),
  -- 普通索引，加速按IP地址查询失败登录记录
  KEY `idx_ip_address` (`ip_address`),
  -- 普通索引，加速按失败时间查询失败登录记录
  KEY `idx_failed_time` (`failed_time`),
  -- 普通索引，加速按锁定状态查询失败登录记录
  KEY `idx_lock_status` (`lock_status`),
  -- 普通索引，加速按租户编号查询失败登录记录
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='失败登录表，记录登录失败尝试，用于登录锁定和风险检测';

-- -----------------------------
-- 登录统计表 (datai_sf_login_statistics)
-- -----------------------------
-- 用途：记录登录统计信息，用于生成报表和分析
-- 设计考虑：
-- 1. 支持按时间维度统计
-- 2. 支持按登录类型统计
-- 3. 支持按成功/失败状态统计
-- 4. 支持多租户隔离
CREATE TABLE `datai_sf_login_statistics` (
  -- 统计ID，自增主键，唯一标识一条统计记录
  `stat_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '统计ID',
  -- 租户编号，支持多租户隔离
  `tenant_id` VARCHAR(32) DEFAULT NULL COMMENT '租户编号',
  -- 统计日期，统计数据的日期
  `stat_date` DATE NOT NULL COMMENT '统计日期',
  -- 统计小时，统计数据的小时（0-23），用于小时级统计
  `stat_hour` INT(11) DEFAULT NULL COMMENT '统计小时',
  -- 登录类型，如oauth2、salesforce_cli、legacy_credential等
  `login_type` VARCHAR(50) NOT NULL COMMENT '登录类型',
  -- 成功登录次数
  `success_count` INT(11) NOT NULL DEFAULT 0 COMMENT '成功次数',
  -- 失败登录次数
  `failed_count` INT(11) NOT NULL DEFAULT 0 COMMENT '失败次数',
  -- 令牌刷新次数
  `refresh_count` INT(11) NOT NULL DEFAULT 0 COMMENT '刷新次数',
  -- 令牌吊销次数
  `revoke_count` INT(11) NOT NULL DEFAULT 0 COMMENT '吊销次数',
  -- 创建人，记录创建统计记录的用户
  `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
  -- 创建时间，记录统计记录创建的时间
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  -- 更新人，记录最后更新统计记录的用户
  `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新人',
  -- 更新时间，记录统计记录最后更新的时间
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  -- 主键索引，唯一标识统计记录
  PRIMARY KEY (`stat_id`),
  -- 唯一约束，确保每个统计维度只有一条记录
  UNIQUE KEY `uk_stat_dimension` (`tenant_id`,`stat_date`,`stat_hour`,`login_type`),
  -- 普通索引，加速按统计日期查询统计记录
  KEY `idx_stat_date` (`stat_date`),
  -- 普通索引，加速按统计小时查询统计记录
  KEY `idx_stat_hour` (`stat_hour`),
  -- 普通索引，加速按登录类型查询统计记录
  KEY `idx_login_type` (`login_type`),
  -- 普通索引，加速按租户编号查询统计记录
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录统计表，记录登录统计信息，用于生成报表和分析';