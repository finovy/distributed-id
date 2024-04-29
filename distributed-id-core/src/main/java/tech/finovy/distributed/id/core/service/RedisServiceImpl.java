package tech.finovy.distributed.id.core.service;

import com.google.common.collect.ImmutableList;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RScript;
import org.redisson.client.codec.LongCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.finovy.distributed.id.constants.Constants;
import tech.finovy.distributed.id.constants.TypeEnum;
import tech.finovy.distributed.id.core.AbstractIdService;
import tech.finovy.distributed.id.core.config.GlobalConfiguration;
import tech.finovy.distributed.id.dao.IDPersistDao;
import tech.finovy.distributed.id.exception.BusinessException;
import tech.finovy.distributed.id.mapper.IDPersistMapper;
import tech.finovy.distributed.id.thread.NamedThreadFactory;
import tech.finovy.framework.redisson.holder.RedisContext;
import tech.finovy.framework.redisson.holder.RedisContextHolder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@ConditionalOnProperty(name = "distributed.redis.enable", havingValue = "true", matchIfMissing = true)
public class RedisServiceImpl extends AbstractIdService {

    private static final String addScriptPath = "META-INF/script/redis_incr.lua";
    private static String addScript = "DEFAULT";
    private static final String ID_HEALTH_CHECK = "DISTRIBUTED:ID:CHECK";
    private static final String LOCK_TEMPLATE = "DISTRIBUTED:ID:LOCK:%S";
    private static final String CLEAR_LOCK_TEMPLATE = "DISTRIBUTED:ID:CLEAR:LOCK:%S";
    private static final AtomicBoolean REDIS_IS_OK = new AtomicBoolean(true);
    private final Set<String> keys = new HashSet<>();

    private final GlobalConfiguration config;
    private final IDPersistMapper mapper;
    private final ThreadPoolExecutor executor;
    @Autowired
    private IDPersistDao persistDao;
    private final RedisContext redisContext = RedisContextHolder.get();


    public RedisServiceImpl(GlobalConfiguration config, IDPersistMapper mapper, ThreadPoolExecutor executor) {
        this.config = config;
        this.mapper = mapper;
        this.executor = executor;
    }

    @Override
    public void init() {
        // 获取lua脚本
        addScript = getScript(addScriptPath);
        // 健康检查
        checkRedisHealth();
        // 更新队列缓存
        updateCache();
    }

    private String getScript(String path) {
        ClassLoader classLoader = getClass().getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(path)) {
            if (inputStream != null) {
                try (Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
                    return scanner.useDelimiter("\\A").next();
                }
            }
        } catch (IOException e) {
            log.error("read lua[{}] error:{}", path, e.getMessage(), e);
        }
        throw new BusinessException("load script error:" + path);
    }

    private void checkRedisHealth() {
        // 从数据库获取redis的初始化健康状态
        final Boolean redisStatus = mapper.getRedisStatus();
        REDIS_IS_OK.set(redisStatus == null || redisStatus);
        // 定时监测redis健康状态
        ScheduledExecutorService serviceCheck = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory(getType() + "-Thread-Check-Redis", 1));
        serviceCheck.scheduleWithFixedDelay(() -> {
            boolean redisIsOk;
            if (config.isRedisMockFailure()) {
                Random random = new Random();
                redisIsOk = random.nextBoolean();
                if (!redisIsOk) {
                    log.warn("mock--redis is unHealth");
                }
            } else {
                try {
                    redisContext.getClient().getAtomicLong(ID_HEALTH_CHECK).incrementAndGet();
                    redisIsOk = true;
                } catch (Exception e) {
                    log.warn("redis is unHealth:{}", e.getMessage(), e);
                    redisIsOk = false;
                }
            }
            // 转变redis状态
            if (redisIsOk && !REDIS_IS_OK.get()) {
                for (String key : keys) {
                    final RLock lock = redisContext.getClient().getLock(String.format(CLEAR_LOCK_TEMPLATE, key));
                    if (lock.tryLock()) {
                        try {
                            final String cacheKey = redisContext.createKey(key, "ID");
                            final RMap<String, Long> map = redisContext.getClient().getMap(cacheKey, new LongCodec());
                            map.clear();
                            log.warn("{}/{} redis is recover,and cache clear!", key, cacheKey);
                        } finally {
                            if (lock.isHeldByCurrentThread()) {
                                lock.unlock();
                            }
                        }
                    }
                }
            }
            // 更新数据库redis状态
            mapper.updateRedisStatus(redisIsOk);
            REDIS_IS_OK.compareAndSet(!redisIsOk, redisIsOk);
        }, 0, 2, TimeUnit.SECONDS);
        //
        // 定时获取新增的业务key
        ScheduledExecutorService serviceUpdate = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory(getType() + "-Thread-get-key", 1));
        serviceUpdate.scheduleWithFixedDelay(() -> {
            final Set<String> busKeys = mapper.getAllKeys();
            keys.addAll(busKeys);
        }, 0, 60, TimeUnit.SECONDS);
    }

    private void updateCache() {
        // init
        final Set<String> busKeys = mapper.getAllKeys();
        keys.addAll(busKeys);
        CompletableFuture.runAsync(() -> {
            while (true) {
                for (String key : keys) {
                    try {
                        if (updateCacheFromRedis(key, config.getRedisIdStep())) {
                            if (config.isRedisDebug()) {
                                log.info("update from redis/db finish:[{}]", key);
                            }
                        }
                    } catch (Exception e) {
                        log.error("updateCache error:{}", e.getMessage(), e);
                    }
                }
            }
        }, executor);
    }

    @SneakyThrows
    @Override
    public List<Long> getIds(String key, int batch) {
        try {
            if (REDIS_IS_OK.get()) {
                return getIdsFromRedis(key, (long) batch);
            }
        } catch (Exception e) {
            log.warn("redis is not ok,will generateByDb...{}", e.getMessage(), e);
            REDIS_IS_OK.compareAndSet(true, false);
        }
        return getIdsFromDb(key, (long) batch);
    }

    @SneakyThrows
    private List<Long> getIdsFromRedis(String key, Long batch) {
        // 从redis获取分布式id，如果当前获取到的数据大于等于maxId，则进行等待获取
        List<Long> ids = new ArrayList<>();
        List<Object> keys = ImmutableList.of(redisContext.createKey(key, "ID"));
        int r = 0;
        while (ids.size() < batch) {
            ids = redisContext.getClient().getScript().eval(
                    RScript.Mode.READ_WRITE,
                    addScript.replaceAll("BATCH_SIGN", batch + ""),
                    RScript.ReturnType.MULTI,
                    keys
            );
            r++;
            if (ids.size() < batch && r > 10) {
                try {
                    TimeUnit.MILLISECONDS.sleep(10);
                } catch (InterruptedException e) {
                    log.warn("Thread {} Interrupted", Thread.currentThread().getName());
                }
            }
        }
        return ids;
    }

    @Transactional(timeout = 5)
    public List<Long> getIdsFromDb(String key, Long batch) {
        List<Long> ids = new ArrayList<>();
        // 启动降级措施，从数据库获取数据
        final Long preId = mapper.selectMaxIdForUpdate(key, false);
        if (preId == null) {
            return getIds(key, Math.toIntExact(batch));
        }
        mapper.updateMaxIdByCustomStep(key, batch);
        REDIS_IS_OK.compareAndSet(true, false);
        // 更新内存
        for (int i = 0; i < batch; i++) {
            ids.add(preId + i + 1);
        }
        return ids;
    }

    @SneakyThrows
    public boolean updateCacheFromRedis(String key, Long step) {
        final RLock lock = redisContext.getClient().getLock(String.format(LOCK_TEMPLATE, key));
        if (lock.tryLock()) {
            try {
                final String cacheKey = redisContext.createKey(key, "ID");
                final RMap<String, Long> map = redisContext.getClient().getMap(cacheKey, new LongCodec());
                if (!map.isExists()) {
                    log.info("begin to init: {}", cacheKey);
                    final Long maxIdFromDb = persistDao.updateMaxIdByCustomStep(key, step);
                    map.put("value", maxIdFromDb - step);
                    map.put("max", maxIdFromDb);
                    map.put("step", step);
                    map.put("updateTimestamp", System.currentTimeMillis());
                    return true;
                }
                Long max = map.get("max");
                Long redisStep = map.get("step");
                Long value = map.get("value");
                Long updateTimestamp = map.get("updateTimestamp");
                if (max != null && value != null && redisStep != null && max - value < 0.6 * redisStep) {
                    long duration = System.currentTimeMillis() - updateTimestamp;
                    // 如果更新时间在15分钟内
                    if (duration < Constants.SEGMENT_DURATION) {
                        // 将下一步步长增加两倍
                        if (redisStep * 2 > Constants.SEGMENT_MAX_STEP) {
                            // 如果大于最大步长,什么都不做
                        } else {
                            redisStep = redisStep * 2;
                        }
                    } else if (duration < Constants.SEGMENT_DURATION * 2) {
                        // 如果更新时间大于15分钟,小于30分钟什么都不做
                    } else {
                        // 如果更新时间在30分钟外,将下一步步长除以2,直到到数据库设置步长为止
                        redisStep = redisStep / 2 >= config.getRedisIdStep() ? redisStep / 2 : redisStep;
                    }
                    final Long maxIdFromDb = persistDao.updateMaxIdByCustomStep(key, redisStep);
                    map.put("max", maxIdFromDb);
                    map.put("step", redisStep);
                    map.put("updateTimestamp", System.currentTimeMillis());
                }
                return true;
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }
        return false;
    }


    @Override
    public TypeEnum getType() {
        return TypeEnum.redis;
    }
}
