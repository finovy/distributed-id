package tech.finovy.distributed.id.dao;

import tech.finovy.distributed.id.mapper.IDPersistMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class IDPersistDaoImpl implements IDPersistDao {

    private final IDPersistMapper mapper;

    public IDPersistDaoImpl(IDPersistMapper mapper) {
        this.mapper = mapper;
    }


    @Transactional
    @Override
    public Long updateMaxIdByCustomStep(String key, Long step) {
        final Long preMaxId = mapper.selectMaxIdForUpdate(key, true);
        mapper.updateMaxIdByCustomStep(key, step);
        return preMaxId + step;
    }

}
