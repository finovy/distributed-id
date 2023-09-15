package tech.finovy.distributed.id.mapper;

import tech.finovy.distributed.id.model.SegmentAlloc;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @Author: Ryan Luo
 * @Date: 2023/6/3 17:01
 */
@Mapper
public interface IDAllocMapper {

    @Select("SELECT biz_tag, max_id, step FROM distributed_id_alloc WHERE biz_tag = #{tag}")
    @Results(value = {
            @Result(column = "biz_tag", property = "key"),
            @Result(column = "max_id", property = "maxId"),
            @Result(column = "step", property = "step")
    })
    SegmentAlloc getAlloc(@Param("tag") String tag);

    @Update("UPDATE distributed_id_alloc SET max_id = max_id + step WHERE biz_tag = #{tag}")
    void updateMaxId(@Param("tag") String tag);

    @Update("UPDATE distributed_id_alloc SET max_id = max_id + #{step} WHERE biz_tag = #{key}")
    void updateMaxIdByCustomStep(@Param("key") String key, @Param("step") Integer step);

    @Select("SELECT biz_tag FROM distributed_id_alloc")
    List<String> getAllTags();

}
