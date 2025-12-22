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

 Date: 22/12/2025 20:39:23
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

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
  `sync_status` tinyint NULL DEFAULT NULL COMMENT '同步状态 1:正常 0:误差',
  `sync_index` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '同步优先级',
  `sync_start_date` datetime NULL DEFAULT NULL COMMENT '开始同步时间',
  `sync_end_date` datetime NULL DEFAULT NULL COMMENT '结束同步时间',
  `first_sync_time` datetime NULL DEFAULT NULL COMMENT '首次同步时间',
  `last_sync_time` datetime NULL DEFAULT NULL COMMENT '最后同步时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` VARCHAR(64) NULL DEFAULT NULL COMMENT '创建者',
  `update_by` VARCHAR(64) NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
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
  `create_by` VARCHAR(64) NULL DEFAULT NULL COMMENT '创建者',
  `update_by` VARCHAR(64) NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
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
  `create_by` VARCHAR(64) NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` VARCHAR(64) NULL DEFAULT NULL COMMENT '更新者',
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
  `create_by` VARCHAR(64) NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` VARCHAR(64) NULL DEFAULT NULL COMMENT '更新者',
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
  `batch_id` int NULL DEFAULT NULL COMMENT '关联批次ID',
  `object_api` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '对象API',
  `record_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '记录ID',
  `operation_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '操作类型: INSERT/UPDATE/DELETE/UPSERT',
  `operation_status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '操作状态: SUCCESS/FAILED/PARTIAL',
  `source_data` json NULL COMMENT '源数据(JSON格式)',
  `target_data` json NULL COMMENT '目标数据(JSON格式)',
  `error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '错误信息',
  `execution_time` decimal(10, 3) NULL DEFAULT NULL COMMENT '执行时间(秒)',
  `sync_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '同步时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '000000' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_datai_integration_sync_log_batch_id`(`batch_id` ASC) USING BTREE,
  INDEX `idx_datai_integration_sync_log_object_api`(`object_api` ASC) USING BTREE,
  INDEX `idx_datai_integration_sync_log_record_id`(`record_id` ASC) USING BTREE,
  INDEX `idx_datai_integration_sync_log_operation_status`(`operation_status` ASC) USING BTREE,
  INDEX `idx_datai_integration_sync_log_sync_time`(`sync_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '数据同步日志表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for datai_integration_object
-- ----------------------------
DROP TABLE IF EXISTS `datai_integration_object`;
CREATE TABLE `datai_integration_object`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `api` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '对象API',
  `label` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '对象标签',
  `label_plural` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '对象复数标签',
  `is_custom` tinyint(1) NULL DEFAULT 0 COMMENT '是否自定义对象',
  `is_queryable` tinyint(1) NULL DEFAULT 0 COMMENT '是否可查询',
  `is_replicateable` tinyint(1) NULL DEFAULT 0 COMMENT '是否可复制',
  `is_retrieveable` tinyint(1) NULL DEFAULT 0 COMMENT '是否可检索',
  `is_searchable` tinyint(1) NULL DEFAULT 0 COMMENT '是否可搜索',
  `is_triggerable` tinyint(1) NULL DEFAULT 0 COMMENT '是否可触发',
  `is_undeletable` tinyint(1) NULL DEFAULT 0 COMMENT '是否可恢复删除',
  `is_updateable` tinyint(1) NULL DEFAULT 0 COMMENT '是否可更新',
  `is_createable` tinyint(1) NULL DEFAULT 0 COMMENT '是否可创建',
  `is_deletable` tinyint(1) NULL DEFAULT 0 COMMENT '是否可删除',
  `is_mergeable` tinyint(1) NULL DEFAULT 0 COMMENT '是否可合并',
  `is_mru` tinyint(1) NULL DEFAULT 0 COMMENT '是否最近使用',
  `key_prefix` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关键前缀',
  `record_type_infos` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '记录类型信息(JSON)',
  `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '000000' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_datai_integration_object_api`(`api` ASC) USING BTREE,
  INDEX `idx_datai_integration_object_label`(`label` ASC) USING BTREE,
  INDEX `idx_datai_integration_object_is_custom`(`is_custom` ASC) USING BTREE,
  INDEX `idx_datai_integration_object_is_queryable`(`is_queryable` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '对象信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for datai_integration_performance
-- ----------------------------
DROP TABLE IF EXISTS `datai_integration_performance`;
CREATE TABLE `datai_integration_performance`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '性能记录ID',
  `batch_id` int NULL DEFAULT NULL COMMENT '关联批次ID',
  `object_api` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '对象API',
  `sync_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '同步类型: FULL/INCREMENTAL/INITIAL',
  `api_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'API类型: SOAP/BULK_V1/BULK_V2',
  `record_count` int NOT NULL DEFAULT 0 COMMENT '处理记录数',
  `success_count` int NOT NULL DEFAULT 0 COMMENT '成功记录数',
  `failed_count` int NOT NULL DEFAULT 0 COMMENT '失败记录数',
  `total_time` decimal(10, 3) NOT NULL COMMENT '总耗时(秒)',
  `avg_time_per_record` decimal(10, 6) NOT NULL COMMENT '平均每条记录耗时(秒)',
  `throughput` decimal(10, 2) NOT NULL COMMENT '吞吐量(记录/秒)',
  `api_calls` int NOT NULL DEFAULT 0 COMMENT 'API调用次数',
  `data_volume` bigint NOT NULL DEFAULT 0 COMMENT '数据量(字节)',
  `memory_usage` bigint NULL DEFAULT NULL COMMENT '内存使用量(字节)',
  `cpu_usage` decimal(5, 2) NULL DEFAULT NULL COMMENT 'CPU使用率(%)',
  `sync_date` date NOT NULL COMMENT '同步日期',
  `sync_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '同步时间',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '000000' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_datai_integration_sync_performance_batch_id`(`batch_id` ASC) USING BTREE,
  INDEX `idx_datai_integration_sync_performance_object_api`(`object_api` ASC) USING BTREE,
  INDEX `idx_datai_integration_sync_performance_sync_date`(`sync_date` ASC) USING BTREE,
  INDEX `idx_datai_integration_sync_performance_api_type`(`api_type` ASC) USING BTREE,
  CONSTRAINT `fk_datai_integration_sync_performance_batch` FOREIGN KEY (`batch_id`) REFERENCES `datai_integration_batch` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '同步性能监控表' ROW_FORMAT = DYNAMIC;

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
  `create_by` VARCHAR(64) NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` VARCHAR(64) NULL DEFAULT NULL COMMENT '更新者',
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
  `id` int NOT NULL AUTO_INCREMENT COMMENT '限流记录ID',
  `api_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'API类型: SOAP/BULK_V1/BULK_V2',
  `window_start` datetime NOT NULL COMMENT '时间窗口开始',
  `window_end` datetime NOT NULL COMMENT '时间窗口结束',
  `request_count` int NOT NULL DEFAULT 0 COMMENT '请求计数',
  `limit_threshold` int NOT NULL DEFAULT 0 COMMENT '限制阈值',
  `remaining_requests` int NOT NULL DEFAULT 0 COMMENT '剩余请求次数',
  `reset_time` datetime NULL DEFAULT NULL COMMENT '重置时间',
  `is_throttled` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否被限流',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '000000' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_datai_integration_rate_limit_api_window`(`api_type` ASC, `window_start` ASC, `window_end` ASC) USING BTREE,
  INDEX `idx_datai_integration_rate_limit_api_type`(`api_type` ASC) USING BTREE,
  INDEX `idx_datai_integration_rate_limit_window_start`(`window_start` ASC) USING BTREE,
  INDEX `idx_datai_integration_rate_limit_is_throttled`(`is_throttled` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'API限流管理表' ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
