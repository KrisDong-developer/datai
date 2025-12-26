-- 为 datai_config_version 表添加缺失的字段
-- 执行日期: 2025-12-26

-- 添加 environment_id 字段
ALTER TABLE `datai_config_version` ADD COLUMN `environment_id` bigint NULL DEFAULT NULL COMMENT '环境ID' AFTER `dept_id`;

-- 添加 publish_status 字段
ALTER TABLE `datai_config_version` ADD COLUMN `publish_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '发布状态' AFTER `status`;

-- 添加 config_count 字段
ALTER TABLE `datai_config_version` ADD COLUMN `config_count` int NULL DEFAULT 0 COMMENT '配置数量' AFTER `publish_time`;

-- 添加 rollback_reason 字段
ALTER TABLE `datai_config_version` ADD COLUMN `rollback_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '回滚原因' AFTER `config_count`;

-- 添加 rollback_time 字段
ALTER TABLE `datai_config_version` ADD COLUMN `rollback_time` datetime NULL DEFAULT NULL COMMENT '回滚时间' AFTER `rollback_reason`;

-- 添加 rollback_by 字段
ALTER TABLE `datai_config_version` ADD COLUMN `rollback_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '回滚人' AFTER `rollback_time`;

-- 添加索引
CREATE INDEX `idx_publish_status` ON `datai_config_version`(`publish_status`) USING BTREE COMMENT '发布状态索引';
CREATE INDEX `idx_environment_id` ON `datai_config_version`(`environment_id`) USING BTREE COMMENT '环境ID索引';
