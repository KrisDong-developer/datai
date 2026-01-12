-- 添加实时同步字段
ALTER TABLE `datai_integration_object` ADD COLUMN `is_realtime_sync` tinyint(1) NOT NULL DEFAULT 0 COMMENT '实时同步' AFTER `error_message`;