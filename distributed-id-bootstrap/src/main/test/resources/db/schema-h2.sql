CREATE TABLE distributed_id_alloc (
  biz_tag VARCHAR NOT NULL COMMENT 'biz_tag',
  max_id BIGINT NOT NULL COMMENT 'branch id',
  step int NOT NULL COMMENT 'step',
  description VARCHAR NOT NULL COMMENT 'description',
  update_time TIMESTAMP NOT NULL COMMENT 'update_time',
  PRIMARY KEY (biz_tag)
);


CREATE TABLE distributed_id_persistence (
  biz_tag VARCHAR NOT NULL COMMENT 'biz_tag',
  max_id BIGINT NOT NULL COMMENT 'branch id',
  redis_status int NOT NULL COMMENT 'step',
  description VARCHAR NOT NULL COMMENT 'description',
  update_time TIMESTAMP NOT NULL COMMENT 'update_time',
  PRIMARY KEY (biz_tag)
);
