package tech.finovy.distributed.id.core.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Ryan.luo
 */
@Slf4j
@RefreshScope
@Configuration
public class ExecutorConfig {

    @Value("${executor.pool-size:24}")
    private int corePoolSize;
    @Value("${executor.max-pool-size:500}")
    private int maxPoolSize;
    @Value("${executor.queue-size:100}")
    private int queueSize;

    @Bean
    public ThreadPoolExecutor executor() {
        log.info("=== init thread pool  corePoolSize: {}  max-pool-size : {} queueSize:{} ===", corePoolSize, maxPoolSize, queueSize);
        return new ThreadPoolExecutor(corePoolSize, maxPoolSize, 60L, TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(queueSize),
                new BasicThreadFactory.Builder().namingPattern(String.join("-", "id-", "%s")).build(), new ThreadPoolExecutor.CallerRunsPolicy());
    }

}
