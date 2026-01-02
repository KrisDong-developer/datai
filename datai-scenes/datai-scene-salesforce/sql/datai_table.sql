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

 Date: 30/12/2025 11:50:18
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for datai_config_audit_log
-- ----------------------------
DROP TABLE IF EXISTS `datai_config_audit_log`;
CREATE TABLE `datai_config_audit_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
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
  `dept_id` bigint NULL DEFAULT NULL COMMENT '部门ID',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '更新人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE COMMENT '主键索引',
  INDEX `idx_operation_type`(`operation_type` ASC) USING BTREE COMMENT '普通索引',
  INDEX `idx_object_type`(`object_type` ASC) USING BTREE COMMENT '普通索引',
  INDEX `idx_operator`(`operator` ASC) USING BTREE COMMENT '普通索引',
  INDEX `idx_operation_time`(`operation_time` ASC) USING BTREE COMMENT '普通索引',
  INDEX `idx_request_id`(`request_id` ASC) USING BTREE COMMENT '普通索引',
  INDEX `idx_object_id`(`object_id` ASC) USING BTREE COMMENT '普通索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '配置审计日志表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for datai_config_environment
-- ----------------------------
DROP TABLE IF EXISTS `datai_config_environment`;
CREATE TABLE `datai_config_environment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '环境ID',
  `environment_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '环境名称',
  `environment_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '环境编码',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '环境描述',
  `is_active` tinyint NOT NULL DEFAULT 1 COMMENT '是否激活',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '部门ID',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '更新人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE COMMENT '主键索引',
  UNIQUE INDEX `uk_environment_code`(`dept_id` ASC, `environment_code` ASC) USING BTREE COMMENT '唯一约束',
  INDEX `idx_environment_name`(`environment_name` ASC) USING BTREE COMMENT '普通索引'
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '配置环境表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for datai_config_snapshot
-- ----------------------------
DROP TABLE IF EXISTS `datai_config_snapshot`;
CREATE TABLE `datai_config_snapshot`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '快照ID',
  `snapshot_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '快照号',
  `environment_id` bigint NOT NULL COMMENT '环境ID',
  `snapshot_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '快照描述',
  `snapshot_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '快照内容',
  `config_count` int NULL DEFAULT NULL COMMENT '配置数量',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '快照状态',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '部门ID',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE COMMENT '主键索引',
  INDEX `idx_version_id`(`snapshot_number` ASC) USING BTREE COMMENT '普通索引',
  INDEX `idx_snapshot_time`(`snapshot_desc` ASC) USING BTREE COMMENT '普通索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '配置快照表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for datai_configuration
-- ----------------------------
DROP TABLE IF EXISTS `datai_configuration`;
CREATE TABLE `datai_configuration`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '配置键',
  `config_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '配置值',
  `environment_id` bigint NULL DEFAULT NULL COMMENT '环境ID',
  `is_sensitive` tinyint NOT NULL DEFAULT 0 COMMENT '是否敏感配置',
  `is_encrypted` tinyint NOT NULL DEFAULT 0 COMMENT '是否加密存储',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '配置描述',
  `is_active` tinyint NOT NULL DEFAULT 1 COMMENT '是否激活',
  `version` int NULL DEFAULT NULL COMMENT '版本号',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '部门ID',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '更新人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE COMMENT '主键索引',
  UNIQUE INDEX `uk_config_key_environment`(`dept_id` ASC, `config_key` ASC, `environment_id` ASC) USING BTREE COMMENT '唯一约束',
  INDEX `idx_environment_id`(`environment_id` ASC) USING BTREE COMMENT '普通索引',
  INDEX `idx_config_key`(`config_key` ASC) USING BTREE COMMENT '普通索引'
) ENGINE = InnoDB AUTO_INCREMENT = 170 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for datai_integration_api_call_log
-- ----------------------------
DROP TABLE IF EXISTS `datai_integration_api_call_log`;
CREATE TABLE `datai_integration_api_call_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `api_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'API类型',
  `connection_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '连接类',
  `method_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '方法名',
  `execution_time` bigint NULL DEFAULT NULL COMMENT '耗时(ms)',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态',
  `error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '异常信息',
  `call_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '调用时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '部门ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_api_call_log_api_type`(`api_type` ASC) USING BTREE,
  INDEX `idx_api_call_log_connection_class`(`connection_class` ASC) USING BTREE,
  INDEX `idx_api_call_log_method_name`(`method_name` ASC) USING BTREE,
  INDEX `idx_api_call_log_status`(`status` ASC) USING BTREE,
  INDEX `idx_api_call_log_call_time`(`call_time` ASC) USING BTREE,
  INDEX `idx_api_call_log_dept_id`(`dept_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'API调用日志' ROW_FORMAT = DYNAMIC;

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
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '部门ID',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_datai_integration_batch_api_start_date`(`api` ASC, `sync_start_date` DESC) USING BTREE,
  INDEX `idx_datai_integration_batch_sync_status`(`sync_status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '数据批次表' ROW_FORMAT = DYNAMIC;

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
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '部门ID',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_datai_integration_batch_history_api_label`(`dept_id` ASC, `api` ASC, `label` ASC) USING BTREE,
  INDEX `idx_datai_integration_batch_history_sync_start_time`(`sync_start_time` ASC) USING BTREE,
  INDEX `idx_datai_integration_batch_history_sync_end_time`(`sync_end_time` ASC) USING BTREE,
  INDEX `idx_datai_integration_batch_history_sync_status`(`sync_status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '数据批次历史表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for datai_integration_field
-- ----------------------------
DROP TABLE IF EXISTS `datai_integration_field`;
CREATE TABLE `datai_integration_field`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `api` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '所属对象API',
  `field` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '字段API',
  `label` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字段标签',
  `is_createable` tinyint NULL DEFAULT 0 COMMENT '是否可创建',
  `is_nillable` tinyint NULL DEFAULT 0 COMMENT '是否为空',
  `is_updateable` tinyint NULL DEFAULT 0 COMMENT '是否可更新',
  `is_defaulted_on_create` tinyint NULL DEFAULT 0 COMMENT '是否默认值',
  `is_unique` tinyint NULL DEFAULT 0 COMMENT '是否唯一',
  `is_filterable` tinyint NULL DEFAULT 0 COMMENT '是否可过滤',
  `is_sortable` tinyint NULL DEFAULT 0 COMMENT '是否可排序',
  `is_aggregatable` tinyint NULL DEFAULT 0 COMMENT '是否可聚合',
  `is_groupable` tinyint NULL DEFAULT 0 COMMENT '是否可分组',
  `is_polymorphic_foreign_key` tinyint NULL DEFAULT 0 COMMENT '是否多态外键',
  `is_external_id` tinyint NULL DEFAULT 0 COMMENT '是否外部ID',
  `is_custom` tinyint NULL DEFAULT 0 COMMENT '是否自定义字段',
  `is_calculated` tinyint NULL DEFAULT 0 COMMENT '是否计算字段',
  `is_auto_number` tinyint NULL DEFAULT 0 COMMENT '是否自动编号字段',
  `is_case_sensitive` tinyint NULL DEFAULT 0 COMMENT '是否区分大小写',
  `is_encrypted` tinyint NULL DEFAULT 0 COMMENT '是否加密字段',
  `is_html_formatted` tinyint NULL DEFAULT 0 COMMENT '是否HTML格式字段',
  `is_id_lookup` tinyint NULL DEFAULT 0 COMMENT '是否可通过ID查找',
  `is_permissionable` tinyint NULL DEFAULT 0 COMMENT '是否可设置权限',
  `is_restricted_picklist` tinyint NULL DEFAULT 0 COMMENT '是否限制选择列表',
  `is_restricted_delete` tinyint NULL DEFAULT 0 COMMENT '是否限制删除',
  `is_write_requires_master_read` tinyint NULL DEFAULT 0 COMMENT '写入时是否需要主读',
  `query_by_distance` tinyint NULL DEFAULT 0 COMMENT '是否可以通过距离查询',
  `search_prefilterable` tinyint NULL DEFAULT 0 COMMENT '是否可以预过滤搜索',
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
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '部门ID',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_datai_integration_field_api_field`(`dept_id` ASC, `api` ASC, `field` ASC) USING BTREE,
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
  `dept_id` bigint NULL DEFAULT NULL COMMENT '部门ID',
  `api` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '对象API',
  `field` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '字段API',
  `controlling_field` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '控制字段API',
  `dependent` tinyint NOT NULL DEFAULT 0 COMMENT '是否依赖字段',
  `lookup_filter` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '过滤条件',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_datai_integration_filter_lookup_api_field`(`api` ASC, `field` ASC) USING BTREE,
  INDEX `idx_datai_integration_filter_lookup_controlling_field`(`controlling_field` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 53496 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字段过滤查找信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for datai_integration_metadata_change
-- ----------------------------
DROP TABLE IF EXISTS `datai_integration_metadata_change`;
CREATE TABLE `datai_integration_metadata_change`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `change_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '变更类型',
  `operation_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '操作类型',
  `object_api` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '对象API',
  `field_api` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字段API',
  `object_label` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '对象名',
  `field_label` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字段名',
  `change_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '变更时间',
  `change_user` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '变更人',
  `change_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '变更原因',
  `sync_status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '同步状态',
  `sync_time` datetime NULL DEFAULT NULL COMMENT '同步时间',
  `sync_error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '异常信息',
  `retry_count` int NOT NULL DEFAULT 0 COMMENT '重试次数',
  `last_retry_time` datetime NULL DEFAULT NULL COMMENT '重试时间',
  `is_custom` tinyint(1) NULL DEFAULT 0 COMMENT '是否自定义',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '部门ID',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '对象元数据变更表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for datai_integration_object
-- ----------------------------
DROP TABLE IF EXISTS `datai_integration_object`;
CREATE TABLE `datai_integration_object`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '部门ID',
  `api` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '对象API',
  `label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '显示名称',
  `label_plural` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '复数名称',
  `key_prefix` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ID前缀',
  `namespace` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '命名空间',
  `is_queryable` tinyint NULL DEFAULT 1 COMMENT '可查询',
  `is_createable` tinyint NULL DEFAULT 1 COMMENT '可创建',
  `is_updateable` tinyint NULL DEFAULT 1 COMMENT '可更新',
  `is_deletable` tinyint NULL DEFAULT 1 COMMENT '可删除',
  `is_replicateable` tinyint NULL DEFAULT 0 COMMENT '可同步复制',
  `is_retrieveable` tinyint NULL DEFAULT 1 COMMENT '可检索',
  `is_searchable` tinyint NULL DEFAULT 0 COMMENT '可搜索',
  `is_custom` tinyint NULL DEFAULT 0 COMMENT '是否自定义对象',
  `is_custom_setting` tinyint NULL DEFAULT 0 COMMENT '是否自定义设置',
  `is_work` tinyint NULL DEFAULT 0 COMMENT '启用同步',
  `is_incremental` tinyint NULL DEFAULT 1 COMMENT '增量更新',
  `object_index` int NULL DEFAULT 0 COMMENT '排序权重',
  `batch_field` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'LastModifiedDate' COMMENT '批次字段',
  `total_rows` int NULL DEFAULT 0 COMMENT '本地记录数',
  `last_sync_date` datetime NULL DEFAULT NULL COMMENT '最近同步时间',
  `last_full_sync_date` datetime NULL DEFAULT NULL COMMENT '全量同步时间',
  `sync_status` tinyint NULL DEFAULT 0 COMMENT '状态',
  `error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '失败原因',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_dept_api`(`dept_id` ASC, `api` ASC) USING BTREE,
  INDEX `idx_is_work`(`dept_id` ASC, `is_work` ASC) USING BTREE,
  INDEX `idx_key_prefix`(`key_prefix` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '对象信息表' ROW_FORMAT = DYNAMIC;

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
  `is_active` tinyint NULL DEFAULT 1 COMMENT '是否激活',
  `is_default` tinyint NULL DEFAULT 0 COMMENT '是否默认值',
  `valid_for` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '有效性',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '部门ID',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
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
  `is_blocked` tinyint NOT NULL DEFAULT 0 COMMENT '是否限流',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '部门ID',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_dept_api_limit`(`dept_id` ASC, `api_type` ASC, `limit_type` ASC) USING BTREE,
  INDEX `idx_reset_time`(`reset_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'API限流监控表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for datai_integration_sync_log
-- ----------------------------
DROP TABLE IF EXISTS `datai_integration_sync_log`;
CREATE TABLE `datai_integration_sync_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `batch_id` int NOT NULL COMMENT '关联批次ID',
  `object_api` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '对象API',
  `record_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'SF记录ID',
  `operation_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '操作类型',
  `operation_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '操作状态',
  `error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '错误信息',
  `execution_time` decimal(10, 3) NULL DEFAULT NULL COMMENT '执行时间(秒)',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '部门ID',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_log_dept_batch`(`dept_id` ASC, `batch_id` ASC) USING BTREE,
  INDEX `idx_log_dept_object`(`dept_id` ASC, `object_api` ASC) USING BTREE,
  INDEX `idx_log_dept_status`(`dept_id` ASC, `operation_status` ASC) USING BTREE,
  INDEX `idx_log_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '数据同步日志表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for datai_sf_login_history
-- ----------------------------
DROP TABLE IF EXISTS `datai_sf_login_history`;
CREATE TABLE `datai_sf_login_history`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `login_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '登录类型',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户名',
  `password_encrypted` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '加密密码',
  `security_token_encrypted` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '加密安全令牌',
  `client_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'OAuth客户端ID',
  `client_secret_encrypted` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '加密客户端密钥',
  `grant_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'OAuth授权类型',
  `org_alias` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '组织别名',
  `private_key_path` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '私有密钥路径',
  `code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'OAuth授权码',
  `state` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'OAuth state参数',
  `session_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会话ID',
  `environment_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '环境编码',
  `environment_id` bigint NULL DEFAULT NULL COMMENT '环境ID',
  `instance_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '实例URL',
  `organization_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '组织ID',
  `request_ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '请求IP',
  `request_port` int NULL DEFAULT NULL COMMENT '请求端口',
  `user_agent` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户代理',
  `device_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '设备类型',
  `browser_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '浏览器类型',
  `os_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作系统',
  `login_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '登录状态',
  `error_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '错误代码',
  `error_message` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '错误信息',
  `session_id_result` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '结果会话ID',
  `refresh_token_encrypted` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '加密刷新令牌',
  `token_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '令牌类型',
  `expires_in` int NULL DEFAULT NULL COMMENT '过期时间(秒)',
  `request_time` datetime NULL DEFAULT NULL COMMENT '请求时间',
  `response_time` datetime NULL DEFAULT NULL COMMENT '响应时间',
  `duration_ms` bigint NULL DEFAULT NULL COMMENT '处理耗时(毫秒)',
  `operator` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作人',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '部门ID',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_login_type`(`login_type` ASC) USING BTREE,
  INDEX `idx_username`(`username` ASC) USING BTREE,
  INDEX `idx_login_status`(`login_status` ASC) USING BTREE,
  INDEX `idx_request_time`(`request_time` ASC) USING BTREE,
  INDEX `idx_environment_code`(`environment_code` ASC) USING BTREE,
  INDEX `idx_request_ip`(`request_ip` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '登录历史信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for datai_sf_login_session
-- ----------------------------
DROP TABLE IF EXISTS `datai_sf_login_session`;
CREATE TABLE `datai_sf_login_session`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '会话ID',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `login_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录类型',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '会话状态',
  `login_time` datetime NOT NULL COMMENT '登录时间',
  `expire_time` datetime NOT NULL COMMENT '过期时间',
  `last_activity_time` datetime NOT NULL COMMENT '最后活动时间',
  `login_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '登录IP',
  `device_info` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '设备信息',
  `browser_info` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '浏览器信息',
  `session_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '会话ID',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '部门ID',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_username`(`username` ASC) USING BTREE,
  INDEX `idx_login_type`(`login_type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_expire_time`(`expire_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '登录会话信息表' ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
