/*
 Navicat Premium Dump SQL

 Source Server         : 本地数据库
 Source Server Type    : MySQL
 Source Server Version : 90400 (9.4.0)
 Source Host           : localhost:3306
 Source Schema         : ry

 Target Server Type    : MySQL
 Target Server Version : 90400 (9.4.0)
 File Encoding         : 65001

 Date: 24/12/2025 09:39:46
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for datai_config_audit_log
-- ----------------------------
DROP TABLE IF EXISTS `datai_config_audit_log`;
CREATE TABLE `datai_config_audit_log`  (
  `log_id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `operation_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作类型',
  `object_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '对象类型',
  `object_id` bigint NULL DEFAULT NULL COMMENT '对象ID',
  `old_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '旧值',
  `new_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '新值',
  `operation_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作描述',
  `operator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作人',
  `operation_time` datetime NOT NULL COMMENT '操作时间',
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `user_agent` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户代理',
  `request_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '请求ID',
  `result` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'SUCCESS' COMMENT '操作结果',
  `error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '错误信息',
  `tenant_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户编号',
  PRIMARY KEY (`log_id`) USING BTREE COMMENT '主键索引',
  INDEX `idx_operation_type`(`operation_type` ASC) USING BTREE COMMENT '普通索引',
  INDEX `idx_object_type`(`object_type` ASC) USING BTREE COMMENT '普通索引',
  INDEX `idx_operator`(`operator` ASC) USING BTREE COMMENT '普通索引',
  INDEX `idx_operation_time`(`operation_time` ASC) USING BTREE COMMENT '普通索引',
  INDEX `idx_request_id`(`request_id` ASC) USING BTREE COMMENT '普通索引',
  INDEX `idx_object_id`(`object_id` ASC) USING BTREE COMMENT '普通索引'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '配置审计日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for datai_config_environment
-- ----------------------------
DROP TABLE IF EXISTS `datai_config_environment`;
CREATE TABLE `datai_config_environment`  (
  `environment_id` bigint NOT NULL AUTO_INCREMENT COMMENT '环境ID',
  `environment_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '环境名称',
  `environment_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '环境编码',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '环境描述',
  `is_active` int NOT NULL DEFAULT 1 COMMENT '是否激活',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '更新人',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `tenant_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户编号',
  PRIMARY KEY (`environment_id`) USING BTREE COMMENT '主键索引',
  UNIQUE INDEX `uk_environment_code`(`environment_code` ASC) USING BTREE COMMENT '唯一约束',
  INDEX `idx_environment_name`(`environment_name` ASC) USING BTREE COMMENT '普通索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '配置环境表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for datai_config_snapshot
-- ----------------------------
DROP TABLE IF EXISTS `datai_config_snapshot`;
CREATE TABLE `datai_config_snapshot`  (
  `snapshot_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '快照ID',
  `version_id` bigint NOT NULL COMMENT '版本ID',
  `snapshot_time` datetime NOT NULL COMMENT '快照时间',
  `snapshot_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '快照内容',
  `config_count` int NOT NULL COMMENT '配置数量',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `tenant_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户编号',
  PRIMARY KEY (`snapshot_id`) USING BTREE COMMENT '主键索引',
  INDEX `idx_version_id`(`version_id` ASC) USING BTREE COMMENT '普通索引',
  INDEX `idx_snapshot_time`(`snapshot_time` ASC) USING BTREE COMMENT '普通索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '配置快照表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for datai_config_version
-- ----------------------------
DROP TABLE IF EXISTS `datai_config_version`;
CREATE TABLE `datai_config_version`  (
  `version_id` bigint NOT NULL AUTO_INCREMENT COMMENT '版本ID',
  `version_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '版本号',
  `version_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '版本描述',
  `snapshot_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '快照ID',
  `snapshot_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '快照内容',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '版本状态',
  `publish_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '更新人',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `tenant_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户编号',
  PRIMARY KEY (`version_id`) USING BTREE COMMENT '主键索引',
  UNIQUE INDEX `uk_version_number`(`version_number` ASC) USING BTREE COMMENT '唯一约束',
  INDEX `idx_status`(`status` ASC) USING BTREE COMMENT '普通索引',
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE COMMENT '普通索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '配置版本表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for datai_configuration
-- ----------------------------
DROP TABLE IF EXISTS `datai_configuration`;
CREATE TABLE `datai_configuration`  (
  `config_id` bigint NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '配置键',
  `config_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '配置值',
  `environment_id` bigint NULL DEFAULT NULL COMMENT '环境ID',
  `is_sensitive` int NOT NULL DEFAULT 0 COMMENT '是否敏感配置',
  `is_encrypted` int NOT NULL DEFAULT 0 COMMENT '是否加密存储',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '配置描述',
  `is_active` int NOT NULL DEFAULT 1 COMMENT '是否激活',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '更新人',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `tenant_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户编号',
  PRIMARY KEY (`config_id`) USING BTREE COMMENT '主键索引',
  UNIQUE INDEX `uk_config_key_environment`(`config_key` ASC, `environment_id` ASC) USING BTREE COMMENT '唯一约束',
  INDEX `idx_environment_id`(`environment_id` ASC) USING BTREE COMMENT '普通索引',
  INDEX `idx_config_key`(`config_key` ASC) USING BTREE COMMENT '普通索引'
) ENGINE = InnoDB AUTO_INCREMENT = 90 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for datai_integration_batch
-- ----------------------------
DROP TABLE IF EXISTS `datai_integration_batch`;
CREATE TABLE `datai_integration_batch`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '批次ID',
  `api` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '对象API',
  `label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '对象名称',
  `sf_num` int NULL DEFAULT NULL COMMENT 'SF数据量',
  `db_num` int NULL DEFAULT NULL COMMENT '本地数据量',
  `sync_type` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '同步类型',
  `batch_field` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '批次字段',
  `sync_status` tinyint NULL DEFAULT NULL COMMENT '同步状态',
  `sync_start_date` datetime NULL DEFAULT NULL COMMENT '开始同步时间',
  `sync_end_date` datetime NULL DEFAULT NULL COMMENT '结束同步时间',
  `first_sync_time` datetime NULL DEFAULT NULL COMMENT '首次同步时间',
  `last_sync_time` datetime NULL DEFAULT NULL COMMENT '最后同步时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新者',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '000000' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_datai_integration_batch_api_start_date`(`api` ASC, `sync_start_date` DESC) USING BTREE,
  INDEX `idx_datai_integration_batch_sync_status`(`sync_status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '数据批次表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for datai_integration_batch_history
-- ----------------------------
DROP TABLE IF EXISTS `datai_integration_batch_history`;
CREATE TABLE `datai_integration_batch_history`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '编号ID',
  `api` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '对象API',
  `label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '对象名称',
  `batch_id` int NULL DEFAULT NULL COMMENT '批次ID',
  `batch_field` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '批次字段',
  `sync_num` int NULL DEFAULT NULL COMMENT '同步数据量',
  `sync_type` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '同步类型',
  `sync_status` tinyint NULL DEFAULT NULL COMMENT '同步状态',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `cost` bigint NULL DEFAULT NULL COMMENT '耗费时间',
  `sync_start_time` datetime NULL DEFAULT NULL COMMENT '开始同步时间',
  `sync_end_time` datetime NULL DEFAULT NULL COMMENT '结束同步时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新者',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '000000' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_datai_integration_batch_history_api_label`(`api` ASC, `label` ASC) USING BTREE,
  INDEX `idx_datai_integration_batch_history_sync_start_time`(`sync_start_time` ASC) USING BTREE,
  INDEX `idx_datai_integration_batch_history_sync_end_time`(`sync_end_time` ASC) USING BTREE,
  INDEX `idx_datai_integration_batch_history_sync_status`(`sync_status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '数据批次历史表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for datai_integration_field
-- ----------------------------
DROP TABLE IF EXISTS `datai_integration_field`;
CREATE TABLE `datai_integration_field`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `api` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '所属对象API',
  `field` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '字段API',
  `label` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字段标签',
  `is_createable` tinyint(1) NULL DEFAULT 0 COMMENT '是否可创建',
  `is_nillable` tinyint(1) NULL DEFAULT 0 COMMENT '是否为空',
  `is_updateable` tinyint(1) NULL DEFAULT 0 COMMENT '是否可更新',
  `is_defaulted_on_create` tinyint(1) NULL DEFAULT 0 COMMENT '是否默认值',
  `is_unique` tinyint(1) NULL DEFAULT 0 COMMENT '是否唯一',
  `is_filterable` tinyint(1) NULL DEFAULT 0 COMMENT '是否可过滤',
  `is_sortable` tinyint(1) NULL DEFAULT 0 COMMENT '是否可排序',
  `is_aggregatable` tinyint(1) NULL DEFAULT 0 COMMENT '是否可聚合',
  `is_groupable` tinyint(1) NULL DEFAULT 0 COMMENT '是否可分组',
  `is_polymorphic_foreign_key` tinyint(1) NULL DEFAULT 0 COMMENT '是否多态外键',
  `is_external_id` tinyint(1) NULL DEFAULT 0 COMMENT '是否外部ID',
  `is_custom` tinyint(1) NULL DEFAULT 0 COMMENT '是否自定义字段',
  `is_calculated` tinyint(1) NULL DEFAULT 0 COMMENT '是否计算字段',
  `is_auto_number` tinyint(1) NULL DEFAULT 0 COMMENT '是否自动编号字段',
  `is_case_sensitive` tinyint(1) NULL DEFAULT 0 COMMENT '是否区分大小写',
  `is_encrypted` tinyint(1) NULL DEFAULT 0 COMMENT '是否加密字段',
  `is_html_formatted` tinyint(1) NULL DEFAULT 0 COMMENT '是否HTML格式字段',
  `is_id_lookup` tinyint(1) NULL DEFAULT 0 COMMENT '是否可通过ID查找',
  `is_permissionable` tinyint(1) NULL DEFAULT 0 COMMENT '是否可设置权限',
  `is_restricted_picklist` tinyint(1) NULL DEFAULT 0 COMMENT '是否限制选择列表',
  `is_restricted_delete` tinyint(1) NULL DEFAULT 0 COMMENT '是否限制删除',
  `is_write_requires_master_read` tinyint(1) NULL DEFAULT 0 COMMENT '写入时是否需要主读',
  `query_by_distance` tinyint(1) NULL DEFAULT 0 COMMENT '是否可以通过距离查询',
  `search_prefilterable` tinyint(1) NULL DEFAULT 0 COMMENT '是否可以预过滤搜索',
  `field_data_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字段数据类型',
  `field_length` int NULL DEFAULT NULL COMMENT '字段长度',
  `field_precision` int NULL DEFAULT NULL COMMENT '数字字段的精度',
  `field_scale` int NULL DEFAULT NULL COMMENT '数字字段的小数位数',
  `field_byte_length` int NULL DEFAULT NULL COMMENT '字段的字节长度',
  `soap_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'SOAP类型',
  `relationship_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关系名称',
  `reference_target_field` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '引用目标字段',
  `reference_to` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '引用目标对象的API名称列表（逗号分隔）',
  `polymorphic_foreign_field` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '多态映射字段',
  `relationship_order` int NULL DEFAULT NULL COMMENT '关系顺序',
  `default_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '默认值',
  `calculated_formula` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '计算字段的公式',
  `inline_help_text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '内联帮助文本',
  `mask` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '掩码',
  `mask_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '掩码类型',
  `picklist_values` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '选择列表值 (JSON或其他格式)',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '000000' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_datai_integration_field_api_field`(`api` ASC, `field` ASC) USING BTREE,
  INDEX `idx_datai_integration_field_api_updateable`(`api` ASC, `is_updateable` ASC) USING BTREE,
  INDEX `idx_datai_integration_field_is_createable`(`is_createable` ASC) USING BTREE,
  INDEX `idx_datai_integration_field_is_nillable`(`is_nillable` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 125 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '对象字段信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for datai_integration_filter_lookup
-- ----------------------------
DROP TABLE IF EXISTS `datai_integration_filter_lookup`;
CREATE TABLE `datai_integration_filter_lookup`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `api` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '对象API',
  `field` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '字段API',
  `controlling_field` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '控制字段API',
  `dependent` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否依赖字段',
  `lookup_filter` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '过滤条件',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '000000' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_datai_integration_filter_lookup_api_field`(`api` ASC, `field` ASC) USING BTREE,
  INDEX `idx_datai_integration_filter_lookup_controlling_field`(`controlling_field` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 53496 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字段过滤查找信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for datai_integration_log
-- ----------------------------
DROP TABLE IF EXISTS `datai_integration_log`;
CREATE TABLE `datai_integration_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `batch_id` int NOT NULL COMMENT '关联批次ID',
  `object_api` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '对象API',
  `record_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'SF记录ID',
  `operation_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '操作类型',
  `operation_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '操作状态',
  `error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '错误信息',
  `execution_time` decimal(10, 3) NULL DEFAULT NULL COMMENT '执行时间(秒)',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '000000' COMMENT '租户编号',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_log_tenant_batch`(`tenant_id` ASC, `batch_id` ASC) USING BTREE,
  INDEX `idx_log_tenant_object`(`tenant_id` ASC, `object_api` ASC) USING BTREE,
  INDEX `idx_log_tenant_status`(`tenant_id` ASC, `operation_status` ASC) USING BTREE,
  INDEX `idx_log_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '数据同步日志表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for datai_integration_object
-- ----------------------------
DROP TABLE IF EXISTS `datai_integration_object`;
CREATE TABLE `datai_integration_object`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `api` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '对象API',
  `label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '显示名称',
  `label_plural` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '复数名称',
  `key_prefix` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ID前缀',
  `namespace` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '命名空间',
  `is_queryable` tinyint(1) NULL DEFAULT 1 COMMENT '可查询',
  `is_createable` tinyint(1) NULL DEFAULT 1 COMMENT '可创建',
  `is_updateable` tinyint(1) NULL DEFAULT 1 COMMENT '可更新',
  `is_deletable` tinyint(1) NULL DEFAULT 1 COMMENT '可删除',
  `is_replicateable` tinyint(1) NULL DEFAULT 0 COMMENT '可同步复制',
  `is_retrieveable` tinyint(1) NULL DEFAULT 1 COMMENT '可检索',
  `is_searchable` tinyint(1) NULL DEFAULT 0 COMMENT '可搜索',
  `is_custom` tinyint(1) NULL DEFAULT 0 COMMENT '是否自定义对象',
  `is_custom_setting` tinyint(1) NULL DEFAULT 0 COMMENT '是否自定义设置',
  `is_work` tinyint(1) NULL DEFAULT 0 COMMENT '启用同步',
  `is_incremental` tinyint(1) NULL DEFAULT 1 COMMENT '增量更新',
  `object_index` int NULL DEFAULT 0 COMMENT '排序权重',
  `batch_field` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'LastModifiedDate' COMMENT '批次字段',
  `total_rows` int NULL DEFAULT 0 COMMENT '本地记录数',
  `last_sync_date` datetime NULL DEFAULT NULL COMMENT '最近同步时间',
  `last_full_sync_date` datetime NULL DEFAULT NULL COMMENT '全量同步时间',
  `sync_status` tinyint(1) NULL DEFAULT 0 COMMENT '状态:0未同步,1全量,2增量,3完成,4失败',
  `error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '失败原因',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '000000' COMMENT '租户ID',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_tenant_api`(`tenant_id` ASC, `api` ASC) USING BTREE,
  INDEX `idx_is_work`(`tenant_id` ASC, `is_work` ASC) USING BTREE,
  INDEX `idx_key_prefix`(`key_prefix` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '对象同步控制表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for datai_integration_picklist
-- ----------------------------
DROP TABLE IF EXISTS `datai_integration_picklist`;
CREATE TABLE `datai_integration_picklist`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `api` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '对象API',
  `field` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '字段API',
  `picklist_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '选择列表值',
  `picklist_label` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '选择列表标签',
  `picklist_index` int NULL DEFAULT NULL COMMENT '排序',
  `is_active` tinyint(1) NULL DEFAULT 1 COMMENT '是否激活',
  `is_default` tinyint(1) NULL DEFAULT 0 COMMENT '是否默认值',
  `valid_for` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '有效性',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '000000' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_datai_integration_picklist_api_field`(`api` ASC, `field` ASC) USING BTREE,
  INDEX `idx_datai_integration_picklist_is_active`(`is_active` ASC) USING BTREE,
  INDEX `idx_datai_integration_picklist_is_default`(`is_default` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 53820 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字段选择列表信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for datai_integration_rate_limit
-- ----------------------------
DROP TABLE IF EXISTS `datai_integration_rate_limit`;
CREATE TABLE `datai_integration_rate_limit`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `api_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '接口类型',
  `limit_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '限制维度',
  `current_usage` int NOT NULL DEFAULT 0 COMMENT '已用额度',
  `max_limit` int NOT NULL DEFAULT 0 COMMENT '总额度',
  `remaining_val` int NOT NULL DEFAULT 0 COMMENT '剩余额度',
  `reset_time` datetime NULL DEFAULT NULL COMMENT '重置时间',
  `is_blocked` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否限流',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '000000' COMMENT '租户ID',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_tenant_api_limit`(`tenant_id` ASC, `api_type` ASC, `limit_type` ASC) USING BTREE,
  INDEX `idx_reset_time`(`reset_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'API限流监控表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for datai_picklist
-- ----------------------------
DROP TABLE IF EXISTS `datai_picklist`;
CREATE TABLE `datai_picklist`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `object_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '对象名',
  `field_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '字段名',
  `item_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '选项值',
  `item_label` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '显示标签',
  `sort_no` int NULL DEFAULT 0 COMMENT '排序',
  `is_active` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用',
  `is_default` tinyint(1) NULL DEFAULT 0 COMMENT '是否默认',
  `valid_for` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '依赖位图',
  `parent_val` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '父级关联值',
  `lang` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'zh_CN' COMMENT '语言',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '000000' COMMENT '租户ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_field_item`(`tenant_id` ASC, `object_name` ASC, `field_name` ASC, `item_value` ASC, `lang` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '对象选项列表信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for datai_sf_failed_login
-- ----------------------------
DROP TABLE IF EXISTS `datai_sf_failed_login`;
CREATE TABLE `datai_sf_failed_login`  (
  `failed_id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `tenant_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户编号',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户名',
  `login_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '登录类型',
  `failed_time` datetime NOT NULL COMMENT '失败时间',
  `failed_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '失败原因',
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `device_info` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '设备信息',
  `lock_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'UNLOCKED' COMMENT '锁定状态',
  `lock_time` datetime NULL DEFAULT NULL COMMENT '锁定时间',
  `unlock_time` datetime NULL DEFAULT NULL COMMENT '解锁时间',
  `lock_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '锁定原因',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`failed_id`) USING BTREE,
  INDEX `idx_username`(`username` ASC) USING BTREE,
  INDEX `idx_ip_address`(`ip_address` ASC) USING BTREE,
  INDEX `idx_failed_time`(`failed_time` ASC) USING BTREE,
  INDEX `idx_lock_status`(`lock_status` ASC) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 32 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '失败登录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for datai_sf_login_audit
-- ----------------------------
DROP TABLE IF EXISTS `datai_sf_login_audit`;
CREATE TABLE `datai_sf_login_audit`  (
  `audit_id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `tenant_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户编号',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户名',
  `operation_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作类型',
  `result` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作结果',
  `operation_time` datetime NOT NULL COMMENT '操作时间',
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `device_info` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '设备信息',
  `browser_info` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '浏览器信息',
  `login_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '登录类型',
  `error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '错误信息',
  `session_id` bigint NULL DEFAULT NULL COMMENT '会话ID',
  `token_id` bigint NULL DEFAULT NULL COMMENT 'SeesionID',
  `request_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '请求ID',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`audit_id`) USING BTREE,
  INDEX `idx_username`(`username` ASC) USING BTREE,
  INDEX `idx_operation_type`(`operation_type` ASC) USING BTREE,
  INDEX `idx_result`(`result` ASC) USING BTREE,
  INDEX `idx_operation_time`(`operation_time` ASC) USING BTREE,
  INDEX `idx_login_type`(`login_type` ASC) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE,
  INDEX `idx_session_id`(`session_id` ASC) USING BTREE,
  INDEX `idx_token_id`(`token_id` ASC) USING BTREE,
  INDEX `idx_request_id`(`request_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 37 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '登录审计日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for datai_sf_login_session
-- ----------------------------
DROP TABLE IF EXISTS `datai_sf_login_session`;
CREATE TABLE `datai_sf_login_session`  (
  `session_id` bigint NOT NULL AUTO_INCREMENT COMMENT '会话ID',
  `tenant_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户编号',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `login_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录类型',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '会话状态',
  `login_time` datetime NOT NULL COMMENT '登录时间',
  `expire_time` datetime NOT NULL COMMENT '过期时间',
  `last_activity_time` datetime NOT NULL COMMENT '最后活动时间',
  `login_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '登录IP',
  `device_info` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '设备信息',
  `browser_info` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '浏览器信息',
  `session_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '会话标识',
  `token_id` bigint NULL DEFAULT NULL COMMENT 'SeesionID',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`session_id`) USING BTREE,
  INDEX `idx_username`(`username` ASC) USING BTREE,
  INDEX `idx_login_type`(`login_type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_expire_time`(`expire_time` ASC) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE,
  INDEX `idx_token_id`(`token_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '登录会话表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for datai_sf_login_statistics
-- ----------------------------
DROP TABLE IF EXISTS `datai_sf_login_statistics`;
CREATE TABLE `datai_sf_login_statistics`  (
  `stat_id` bigint NOT NULL AUTO_INCREMENT COMMENT '统计ID',
  `tenant_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户编号',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `stat_hour` int NULL DEFAULT NULL COMMENT '统计小时',
  `login_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录类型',
  `success_count` int NOT NULL DEFAULT 0 COMMENT '成功次数',
  `failed_count` int NOT NULL DEFAULT 0 COMMENT '失败次数',
  `refresh_count` int NOT NULL DEFAULT 0 COMMENT '刷新次数',
  `revoke_count` int NOT NULL DEFAULT 0 COMMENT '吊销次数',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`stat_id`) USING BTREE,
  UNIQUE INDEX `uk_stat_dimension`(`tenant_id` ASC, `stat_date` ASC, `stat_hour` ASC, `login_type` ASC) USING BTREE,
  INDEX `idx_stat_date`(`stat_date` ASC) USING BTREE,
  INDEX `idx_stat_hour`(`stat_hour` ASC) USING BTREE,
  INDEX `idx_login_type`(`login_type` ASC) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 37 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '登录统计表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for datai_sf_token
-- ----------------------------
DROP TABLE IF EXISTS `datai_sf_token`;
CREATE TABLE `datai_sf_token`  (
  `token_id` bigint NOT NULL AUTO_INCREMENT COMMENT 'SeesionID',
  `tenant_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户编号',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `access_token` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '访问Seesion',
  `refresh_token` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '刷新Seesion',
  `access_token_expire` datetime NOT NULL COMMENT '访问Seesion过期时间',
  `refresh_token_expire` datetime NULL DEFAULT NULL COMMENT '刷新Seesion过期时间',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Seesion状态',
  `instance_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '实例URL',
  `login_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录类型',
  `client_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '客户端ID',
  `scope` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '作用域',
  `token_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Seesion类型',
  `organization_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '组织ID',
  `user_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户ID',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`token_id`) USING BTREE,
  INDEX `idx_username`(`username` ASC) USING BTREE,
  INDEX `idx_access_token`(`access_token`(255) ASC) USING BTREE,
  INDEX `idx_refresh_token`(`refresh_token`(255) ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_access_expire`(`access_token_expire` ASC) USING BTREE,
  INDEX `idx_refresh_expire`(`refresh_token_expire` ASC) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Seesion表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for datai_sf_token_binding
-- ----------------------------
DROP TABLE IF EXISTS `datai_sf_token_binding`;
CREATE TABLE `datai_sf_token_binding`  (
  `binding_id` bigint NOT NULL AUTO_INCREMENT COMMENT '绑定ID',
  `tenant_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户编号',
  `token_id` bigint NOT NULL COMMENT 'SeesionID',
  `binding_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '绑定类型',
  `device_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '设备ID',
  `binding_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '绑定IP',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '绑定状态',
  `binding_time` datetime NOT NULL COMMENT '绑定时间',
  `expire_time` datetime NULL DEFAULT NULL COMMENT '过期时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`binding_id`) USING BTREE,
  INDEX `idx_token_id`(`token_id` ASC) USING BTREE,
  INDEX `idx_binding_type`(`binding_type` ASC) USING BTREE,
  INDEX `idx_device_id`(`device_id` ASC) USING BTREE,
  INDEX `idx_binding_ip`(`binding_ip` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Seesion绑定表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
