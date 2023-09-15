package tech.finovy.distributed.id;


import tech.finovy.distributed.id.exception.BusinessException;
import tech.finovy.distributed.id.exception.InitException;

/**
 * @Author: Ryan Luo
 * @Date: 2023/6/5 16:43
 */
public class BaseService {

    public <R> R getIds(DistributedIdService t, String key, int batch) {
        if (t == null) {
            throw new InitException("service disabled");
        }
        if (batch < 1) {
            throw new BusinessException("batch must be >= 1");
        }
        return (R) ((batch == 1) ? t.getId(key) : t.getIds(key, batch));
    }

}
