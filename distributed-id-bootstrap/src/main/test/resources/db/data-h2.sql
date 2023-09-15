INSERT INTO `distributed_id_alloc`(`biz_tag`, `max_id`, `step`, `description`, `update_time`)
VALUES
('id-h2-local', 100, 10000, 'test', '2023-06-03 00:00:00');


INSERT INTO `distributed_id_persistence`(`biz_tag`, `max_id`, `redis_status`, `description`, `update_time`)
VALUES
('id-h2-local', 0, 0, 'test', '2023-06-03 00:00:00');
