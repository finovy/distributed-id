package tech.finovy.distributed.id.dao;

import tech.finovy.distributed.id.model.SegmentAlloc;

import java.util.List;

public interface IDAllocDao {

    SegmentAlloc updateMaxIdAndGetAlloc(String tag);

    SegmentAlloc updateMaxIdByCustomStep(String key, Integer step);

    List<String> getAllTags();

}
