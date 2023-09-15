package tech.finovy.distributed.id.dao;

import tech.finovy.distributed.id.model.SegmentAlloc;
import tech.finovy.distributed.id.mapper.IDAllocMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class IDAllocDaoImpl implements IDAllocDao {

    private final IDAllocMapper mapper;

    public IDAllocDaoImpl(IDAllocMapper mapper) {
        this.mapper = mapper;
    }

    @Transactional
    @Override
    public SegmentAlloc updateMaxIdAndGetAlloc(String key) {
        mapper.updateMaxId(key);
        return mapper.getAlloc(key);
    }

    @Transactional
    @Override
    public SegmentAlloc updateMaxIdByCustomStep(String key, Integer step) {
        mapper.updateMaxIdByCustomStep(key, step);
        return mapper.getAlloc(key);
    }

    @Override
    public List<String> getAllTags() {
        return mapper.getAllTags();
    }
}
