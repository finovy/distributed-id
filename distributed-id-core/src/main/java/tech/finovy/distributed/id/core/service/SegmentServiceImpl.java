package tech.finovy.distributed.id.core.service;

import com.google.common.collect.Lists;
import org.springframework.util.CollectionUtils;
import tech.finovy.distributed.id.constants.Constants;
import tech.finovy.distributed.id.constants.Status;
import tech.finovy.distributed.id.constants.TypeEnum;
import tech.finovy.distributed.id.core.AbstractIdService;
import tech.finovy.distributed.id.core.model.Segment;
import tech.finovy.distributed.id.core.model.SegmentBuffer;
import tech.finovy.distributed.id.dao.IDAllocDao;
import tech.finovy.distributed.id.exception.BusinessException;
import tech.finovy.distributed.id.exception.InitException;
import tech.finovy.distributed.id.model.SegmentAlloc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import tech.finovy.distributed.id.thread.NamedThreadFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@ConditionalOnProperty(name = {"distributed.segment.enable"}, havingValue = "true", matchIfMissing = true)
public class SegmentServiceImpl extends AbstractIdService {

    private volatile boolean initOK = false;
    private final Map<String, SegmentBuffer> cache = new ConcurrentHashMap<>();
    private final IDAllocDao dataBase;

    private static final ExecutorService executorService = new ThreadPoolExecutor(
            8, 2 * Math.max(Runtime.getRuntime().availableProcessors(), 16),
            60L, TimeUnit.SECONDS,
            new LinkedBlockingDeque<>(100),
            new NamedThreadFactory("Thread-Segment-Update", 2 * Math.max(Runtime.getRuntime().availableProcessors(), 16)),
            new ThreadPoolExecutor.CallerRunsPolicy());

    public SegmentServiceImpl(IDAllocDao dataBase) {
        this.dataBase = dataBase;
    }

    @Override
    public void init(){
        updateCacheFromDb();
        initOK = true;
        updateCacheFromDbAtEveryMinute();
    }


    @Override
    public List<Long> getIds(String key, int size) {
        if (!initOK) {
            throw new InitException(Status.EXCEPTION_INIT_FALSE.getMessage());
        }
        if (cache.containsKey(key)) {
            SegmentBuffer buffer = cache.get(key);
            if (!buffer.isInitOk()) {
                synchronized (buffer) {
                    if (!buffer.isInitOk()) {
                        try {
                            // 初始化SegmentBuffer
                            updateSegmentFromDb(key, buffer.getCurrent());
                            log.info("init buffer update key {} {} from db", key, buffer.getCurrent());
                            buffer.setInitOk(true);
                        } catch (Exception e) {
                            log.warn("init buffer key {} {} exception {}", key, buffer.getCurrent(), e);
                        }
                    }
                }
            }
            List<Long> ids = Lists.newArrayList();
            for (int i = 0; i < size; i++) {
                ids.add(getIdFromSegmentBuffer(cache.get(key)));
            }
            return ids;
        }
        throw new BusinessException(Status.EXCEPTION_KEY_NOT_EXISTS.getMessage());
    }


    private void updateCacheFromDbAtEveryMinute() {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor(
                new NamedThreadFactory("Thread-Check-DBCache", 1));
        // 60s后延迟执行,每60秒加载一遍数据
        service.scheduleWithFixedDelay(this::updateCacheFromDb, 0, 60, TimeUnit.SECONDS);
    }

    private void updateCacheFromDb() {
        try {
            List<String> dbTags = dataBase.getAllTags();
            if (CollectionUtils.isEmpty(dbTags)) {
                return;
            }
            List<String> cacheTags = new ArrayList<String>(cache.keySet());
            Set<String> insertTagsSet = new HashSet<>(dbTags);
            Set<String> removeTagsSet = new HashSet<>(cacheTags);
            // db中新增的tags加载到cache
            for (int i = 0; i < cacheTags.size(); i++) {
                // 将已经存在缓存的tag从查询数据中删除
                insertTagsSet.remove(cacheTags.get(i));
            }
            for (String tag : insertTagsSet) {
                SegmentBuffer buffer = new SegmentBuffer();
                buffer.setKey(tag);
                Segment segment = buffer.getCurrent();
                segment.setValue(new AtomicLong(0));
                segment.setMax(0);
                segment.setStep(0);
                cache.put(tag, buffer);
                log.info("add tag {} from db to id cache, segmentBuffer {}", tag, buffer);
            }
            // cache中已失效的tags从cache删除
            for (int i = 0; i < dbTags.size(); i++) {
                removeTagsSet.remove(dbTags.get(i));
            }
            for (String tag : removeTagsSet) {
                cache.remove(tag);
                log.info("remove tag {} from id cache", tag);
            }
        } catch (Exception e) {
            log.warn("update cache from db exception", e);
        }
    }


    private void updateSegmentFromDb(String key, Segment segment) {
        SegmentBuffer buffer = segment.getBuffer();
        SegmentAlloc segmentAlloc;
        // 如果buffer没有初始化,从数据库拿数据更新
        if (!buffer.isInitOk()) {
            segmentAlloc = dataBase.updateMaxIdAndGetAlloc(key);
            // Alloc中的step为DB中的step
            buffer.setStep(segmentAlloc.getStep());
            buffer.setMinStep(segmentAlloc.getStep());
        } else if (buffer.getUpdateTimestamp() == 0) {
            // 第二次更新buffer
            segmentAlloc = dataBase.updateMaxIdAndGetAlloc(key);
            // Alloc中的step为DB中的step
            buffer.setUpdateTimestamp(System.currentTimeMillis());
            buffer.setStep(segmentAlloc.getStep());
            buffer.setMinStep(segmentAlloc.getStep());
        } else {
            // 对比buffer的更新时间
            long duration = System.currentTimeMillis() - buffer.getUpdateTimestamp();
            int nextStep = buffer.getStep();
            // 如果更新时间在15分钟内
            if (duration < Constants.SEGMENT_DURATION) {
                // 将下一步步长增加两倍
                if (nextStep * 2 > Constants.SEGMENT_MAX_STEP) {
                    // 如果大于最大步长,什么都不做
                } else {
                    nextStep = nextStep * 2;
                }
            } else if (duration < Constants.SEGMENT_DURATION * 2) {
                // 如果更新时间大于15分钟,小于30分钟什么都不做
            } else {
                // 如果更新时间在30分钟外,将下一步步长除以2,直到到数据库设置步长为止
                nextStep = nextStep / 2 >= buffer.getMinStep() ? nextStep / 2 : nextStep;
            }
            log.info("key {} DB step {} duration {} min, nextStep {}",
                    key, buffer.getStep(), String.format("%.2f", ((double) duration / (1000 * 60))), nextStep);
            segmentAlloc = dataBase.updateMaxIdByCustomStep(key, nextStep);
            buffer.setUpdateTimestamp(System.currentTimeMillis());
            buffer.setStep(nextStep);
            // Alloc的step为DB中的step
            buffer.setMinStep(segmentAlloc.getStep());
        }
        // must set value before set max
        long value = segmentAlloc.getMaxId() - buffer.getStep();
        segment.getValue().set(value);
        segment.setMax(segmentAlloc.getMaxId());
        segment.setStep(buffer.getStep());
    }

    /**
     * 获取分布式id
     */
    private Long getIdFromSegmentBuffer(final SegmentBuffer buffer) {
        while (true) {
            // 加读锁
            buffer.rLock().lock();
            try {
                // 获取当前segment
                final Segment segment = buffer.getCurrent();
                // 如果下一个segment处于不可切换状态,当前segment消耗大于10%步长step,而且buffer不在下个segment加载中,则放入线程池加载下个segment
                if (!buffer.isNextReady() && (segment.getIdle() < 0.9 * segment.getStep()) && buffer.getThreadRunning().compareAndSet(false, true)) {
                    executorService.execute(() -> {
                        // 取模下个segment的index是0还是1
                        Segment next = buffer.getSegments()[buffer.nextPos()];
                        boolean updateOk = false;
                        try {
                            // 更新下一个SegmentBuffer
                            updateSegmentFromDb(buffer.getKey(), next);
                            updateOk = true;
                            log.info("update segment {} from db {}", buffer.getKey(), next);
                        } catch (Exception e) {
                            log.warn(buffer.getKey() + " updateSegmentFromDb exception", e);
                        } finally {
                            if (updateOk) {
                                // 加写锁
                                buffer.wLock().lock();
                                buffer.setNextReady(true);
                                buffer.getThreadRunning().set(false);
                                // 解写锁
                                buffer.wLock().unlock();
                            } else {
                                buffer.getThreadRunning().set(false);
                            }
                        }
                    });
                }
                // 获取分布式id,并且返回
                long value = segment.getValue().getAndIncrement();
                if (value < segment.getMax()) {
                    return value;
                }
            } finally {
                // 解读锁
                buffer.rLock().unlock();
            }
            // 如果当前segment没拿到分布式id,进行等待
            waitAndSleep(buffer);
            buffer.wLock().lock();
            try {
                // 获取分布式id,并且返回
                final Segment segment = buffer.getCurrent();
                long value = segment.getValue().getAndIncrement();
                if (value < segment.getMax()) {
                    return value;
                }
                // segment处于可切换状态,切换至下个segment
                if (buffer.isNextReady()) {
                    buffer.switchPos();
                    buffer.setNextReady(false);
                } else {
                    log.error("Both two segments in {} are not ready!", buffer);
                    throw new InitException(Status.EXCEPTION_SEGMENTS_IS_NULL.getMessage());
                }
            } finally {
                buffer.wLock().unlock();
            }
        }
    }

    private void waitAndSleep(SegmentBuffer buffer) {
        int roll = 0;
        while (buffer.getThreadRunning().get()) {
            roll += 1;
            if (roll > 10000) {
                try {
                    TimeUnit.MILLISECONDS.sleep(10);
                    break;
                } catch (InterruptedException e) {
                    log.warn("Thread {} Interrupted", Thread.currentThread().getName());
                    break;
                }
            }
        }
    }

    @Override
    public TypeEnum getType() {
        return TypeEnum.segment;
    }
}
