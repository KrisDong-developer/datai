-- 为 datai_integration_object 表添加 is_partitioned 字段
-- 执行日期: 2026-01-06
-- 说明: 添加是否分区字段，用于标识对象是否使用分区表

ALTER TABLE `datai_integration_object` 
ADD COLUMN `is_partitioned` tinyint NULL DEFAULT 0 COMMENT '是否分区' 
AFTER `batch_field`;
