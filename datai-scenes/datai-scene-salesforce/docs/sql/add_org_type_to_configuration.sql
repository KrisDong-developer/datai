-- 为 datai_configuration 表添加 org_type 字段
-- 执行日期: 2025-01-14
-- 描述: 添加 ORG 类型字段以支持源 ORG 和目标 ORG 的区分

-- 添加 org_type 字段
ALTER TABLE `datai_configuration` 
ADD COLUMN `org_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'ORG类型' 
AFTER `environment_id`;

-- 添加 org_type 字段索引
ALTER TABLE `datai_configuration` 
ADD INDEX `idx_org_type`(`org_type` ASC) COMMENT '普通索引';
