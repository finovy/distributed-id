package tech.finovy.distributed.id.core.service;

import tech.finovy.distributed.id.constants.Constants;
import tech.finovy.distributed.id.constants.Status;
import tech.finovy.distributed.id.constants.TypeEnum;
import tech.finovy.distributed.id.core.AbstractIdService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import tech.finovy.distributed.id.exception.BusinessException;
import tech.finovy.distributed.id.exception.InitException;
import tech.finovy.distributed.id.exception.ZKException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@ConditionalOnBean(SnowflakeZookeeperHolder.class)
@ConditionalOnProperty(name = "distributed.snowflake.enable", havingValue = "true", matchIfMissing = true)
public class SnowflakeServiceImpl extends AbstractIdService {

    private volatile boolean initOK;
    private final long startTime = 1684814400000L;
    private final long workerId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    private static final Random RANDOM = new Random();

    public SnowflakeServiceImpl(SnowflakeZookeeperHolder holder) {
        if (timeGen() < startTime) {
            log.error("snowflake not support start time greater than current time");
            throw new ZKException("snowflake not support start time greater than current time");
        }
        if (holder.init()) {
            workerId = holder.getWorkerId();
            log.info("start success use zk workerId: {}", workerId);
        } else {
            log.error("snowflake core is not init ok");
            throw new ZKException("snowflake core is not init ok");
        }
        if (workerId < 0 || workerId > Constants.SNOWFLAKE_MAX_WORKER_ID) {
            log.error("zk worker id must gte 0 and lte 1023");
            throw new ZKException("zk worker id must gte 0 and lte 1023");
        }
        initOK = true;
    }


    @Override
    public synchronized List<Long> getIds(String key, int batch) {
        if (!initOK) {
            throw new InitException(Status.EXCEPTION_INIT_FALSE.getMessage());
        }
        ArrayList<Long> ids = new ArrayList<>();
        for (int i = 0; i < batch; i++) {
            long id = getId(timeGen());
            if (id < 0) {
                throw new BusinessException(Status.EXCEPTION_SNOWFLAKE_TIMESTAMP.getMessage());
            }
            ids.add(id);
        }
        return ids;
    }

    private long getId(long timestamp) {
        if (timestamp < lastTimestamp) {
            long offset = lastTimestamp - timestamp;
            if (offset <= 5) {
                try {
                    wait(offset << 1);
                    timestamp = timeGen();
                    if (timestamp < lastTimestamp) {
                        return -1;
                    }
                } catch (InterruptedException e) {
                    log.error("snowflake wait interrupted");
                    return -1;
                }
            } else {
                return -1;
            }
        }
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & Constants.SNOWFLAKE_SEQUENCE_MASK;
            if (sequence == 0) {
                //seq 为0的时候表示是下一毫秒时间开始对seq做随机
                sequence = RANDOM.nextInt(100);
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            // 如果是新的ms开始
            sequence = RANDOM.nextInt(100);
        }
        lastTimestamp = timestamp;
        return ((timestamp - this.startTime) << Constants.SNOWFLAKE_TIMESTAMP_LEFT_SHIFT) | (workerId << Constants.SNOWFLAKE_WORKER_ID_SHIFT) | sequence;
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    protected long timeGen() {
        return System.currentTimeMillis();
    }

    @Override
    public TypeEnum getType() {
        return TypeEnum.snowflake;
    }

}
