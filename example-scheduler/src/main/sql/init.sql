CREATE TABLE `new_qrtz_schedule_job` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `job_name` varchar(200) NOT NULL COMMENT '任务名称',
  `spring_id` varchar(100) NOT NULL COMMENT 'spring bean id',
  `job_status` int(2) NOT NULL DEFAULT '0' COMMENT '任务状态  0:终止状态， 1:开启状态',
  `cron_expression` varchar(50) NOT NULL COMMENT 'cron 表达式',
  `method_name` varchar(200) NOT NULL COMMENT '执行方法名（无参）',
  `is_concurrent` int(2) NOT NULL DEFAULT '1' COMMENT '是否同步 0:否,1是',
  `ext_params` varchar(5000) DEFAULT NULL COMMENT '任务执行参数',
  `is_runing` int(2) DEFAULT NULL COMMENT '是否正则运行 0:否,1是',
  `run_server_ip` varchar(200) DEFAULT NULL COMMENT '运行服务器ip',
  `run_time` bigint(20) DEFAULT NULL COMMENT '运行耗费时间',
  `description` varchar(200) DEFAULT NULL COMMENT '任务描述',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `ab_test` varchar(30) DEFAULT NULL COMMENT 'ab_test保留',
  `big_uid` bigint(20) DEFAULT NULL COMMENT '统一Uid字段保留',
  `sub_login_channel_code` varchar(60) DEFAULT NULL COMMENT '子渠道',
  `login_channel_code` varchar(50) DEFAULT NULL COMMENT '登陆渠道号',
  `brand_id` int(11) DEFAULT '0' COMMENT '品牌id',
  PRIMARY KEY (`id`),
  KEY `index_create_time` (`create_time`) USING BTREE,
  KEY `index_spring_id` (`spring_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='任务调度记录表';

CREATE TABLE `new_qrtz_schedule_log` (
  `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(19) DEFAULT NULL COMMENT '操作人id',
  `real_name` varchar(20) DEFAULT NULL COMMENT '操作人姓名',
  `spring_id` varchar(200) DEFAULT NULL COMMENT 'spring bean id',
  `method_name` varchar(200) DEFAULT NULL COMMENT '调度任务',
  `operater_type` varchar(100) DEFAULT NULL COMMENT '操作类型',
  `change_before` varchar(5000) DEFAULT NULL COMMENT '原始值',
  `change_afrer` varchar(5000) DEFAULT NULL COMMENT '操作后的值',
  `operater_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `ab_test` varchar(30) DEFAULT NULL COMMENT 'ab_test保留',
  `big_uid` bigint(20) DEFAULT NULL COMMENT '统一Uid字段保留',
  `sub_login_channel_code` varchar(60) DEFAULT NULL COMMENT '子渠道',
  `login_channel_code` varchar(50) DEFAULT NULL COMMENT '登陆渠道号',
  `brand_id` int(11) DEFAULT '0' COMMENT '品牌id',
  PRIMARY KEY (`id`),
  KEY `index_operater_date` (`operater_date`),
  KEY `index_method_name` (`method_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='调度任务操作记录表';