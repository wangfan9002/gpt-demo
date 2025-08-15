CREATE TABLE `user` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `user_type` tinyint(1) NOT NULL DEFAULT 1 COMMENT 'ys:1 hz:2',
    `user_name` varchar(50) DEFAULT '',
    `unit_name` varchar(100) DEFAULT '',
    `password` varchar(100) DEFAULT '',
    `email` varchar(100) DEFAULT '',
    `mobile` varchar(20) DEFAULT '',
    `token_secret` varchar(256) DEFAULT '' COMMENT 'token密钥',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `last_update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag` bigint(10) NOT NULL DEFAULT '0' COMMENT '软删除， 0：正常， 非0：删除',
     UNIQUE KEY `uk_mobile` (`mobile`),
     PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4  COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';