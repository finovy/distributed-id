package tech.finovy.distributed.id.dao;

public interface IDPersistDao {


    Long updateMaxIdByCustomStep(String key, Long step);


}
