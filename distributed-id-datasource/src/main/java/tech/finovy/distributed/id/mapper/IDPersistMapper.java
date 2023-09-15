package tech.finovy.distributed.id.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Set;

/**
 * @Author: Ryan Luo
 * @Date: 2023/6/3 17:01
 */
@Mapper
public interface IDPersistMapper {

    @Select("SELECT max_id FROM distributed_id_persistence WHERE biz_tag = #{key}")
    Long selectMaxId(@Param("key") String key);

    @Select("SELECT max_id FROM distributed_id_persistence WHERE biz_tag = #{key} AND redis_status = #{redis_status} FOR UPDATE")
    Long selectMaxIdForUpdate(@Param("key") String key ,@Param("redis_status") boolean redisStatus);

    @Update("UPDATE distributed_id_persistence SET max_id = max_id + #{step} WHERE biz_tag = #{key}")
    void updateMaxIdByCustomStep(@Param("key") String key, @Param("step") Long step);

    @Select("SELECT biz_tag FROM distributed_id_persistence")
    Set<String> getAllKeys();

    @Select("SELECT redis_status FROM distributed_id_persistence LIMIT 1")
    Boolean getRedisStatus();

    @Update("UPDATE distributed_id_persistence SET redis_status = #{redis_status}")
    void updateRedisStatus(@Param("redis_status") boolean redisStatus);

}
