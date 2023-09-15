CREATE TABLE `distributed_id_alloc` (
  `biz_tag` varchar(255) NOT NULL COMMENT 'biz_tag',
  `max_id` bigint(20) NOT NULL COMMENT 'branch id',
  `step` int(11) NOT NULL COMMENT 'step',
  `description` varchar(256) NOT NULL COMMENT 'description',
  `update_time` timestamp NOT NULL COMMENT 'update_time',
  PRIMARY KEY (`biz_tag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='distributed_id_alloc';

CREATE TABLE `distributed_id_persistence` (
  `biz_tag` varchar(255) NOT NULL COMMENT 'biz_tag',
  `max_id` bigint(20) NOT NULL COMMENT 'branch id',
  `redis_status` int(11) NOT NULL COMMENT 'redis_error',
  `description` varchar(256) NOT NULL COMMENT 'description',
  `update_time` timestamp NOT NULL COMMENT 'update_time',
  PRIMARY KEY (`biz_tag`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='distributed_id_persistence';
