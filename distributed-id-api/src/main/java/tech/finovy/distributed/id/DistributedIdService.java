package tech.finovy.distributed.id;

import java.util.List;

/**
 * @Author: Ryan Luo
 * @Date: 2023/5/31 21:57
 */
public interface DistributedIdService {

    default Long getId(String key){
        return this.getIds(key,1).stream().findFirst().get();
    }


    List<Long> getIds(String key, int batch);

    default String getId(String key, String prefix, int length) {
        return this.getIds(key, 1, prefix, length).stream().findFirst().get();
    }

    List<String> getIds(String key, int batch, String prefix, int length);
}
