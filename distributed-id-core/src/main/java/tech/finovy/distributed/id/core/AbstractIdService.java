package tech.finovy.distributed.id.core;

import tech.finovy.distributed.id.DistributedIdService;
import tech.finovy.distributed.id.util.WrapperUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @Author: Ryan Luo
 * @Date: 2023/6/5 17:52
 */
@Slf4j
public abstract class AbstractIdService implements DistributedIdService, TypeService {

    public void init() {
    }

    @Override
    public List<String> getIds(String key, int batch, String prefix, int length) {
        basicCheck(key, batch);
        if (length <= 0) {
            throw new RuntimeException("length must > 0");
        }
        if (prefix == null) {
            throw new RuntimeException("prefix cannot be null");
        }
        return WrapperUtil.wrapperList(this.getIds(key, batch), prefix, length);
    }

    public void basicCheck(String key, int batch) {
        if (key == null) {
            throw new RuntimeException("key cannot be null");
        }
        if (batch <= 0) {
            throw new RuntimeException("batch must > 0");
        }
    }
}
