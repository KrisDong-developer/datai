-- 创建实时同步日志表
CREATE TABLE IF NOT EXISTS `datai_integration_realtime_sync_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `object_name` varchar(255) NOT NULL COMMENT '对象名称',
  `record_id` varchar(255) NOT NULL COMMENT '记录ID',
  `operation_type` varchar(50) NOT NULL COMMENT '操作类型 (INSERT/UPDATE/DELETE)',
  `change_data` json NOT NULL COMMENT '变更数据',
  `sync_status` varchar(50) NOT NULL COMMENT '同步状态 (SUCCESS/FAILED/PENDING)',
  `error_message` text COMMENT '错误信息',
  `retry_count` int(11) DEFAULT '0' COMMENT '重试次数',
  `salesforce_timestamp` datetime COMMENT 'Salesforce时间戳',
  `sync_timestamp` datetime NOT NULL COMMENT '同步时间戳',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_object_name` (`object_name`),
  KEY `idx_record_id` (`record_id`),
  KEY `idx_sync_status` (`sync_status`),
  KEY `idx_sync_timestamp` (`sync_timestamp`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实时同步日志表';

-- 添加注释
ALTER TABLE `datai_integration_realtime_sync_log` COMMENT='实时同步日志表，记录Salesforce CDC同步操作的详细信息';
