package tech.finovy.distributed.id.core.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: Ryan Luo
 * @Date: 2023/6/15 20:18
 */
@Getter
@RefreshScope
@Configuration
public class GlobalConfiguration {

    @Value("${distributed.redis.step:5000}")
    private Long redisIdStep;

    @Value("${distributed.redis.debug:false}")
    private boolean redisDebug;

    @Value("${distributed.redis.fail-open:false}")
    private boolean redisMockFailure;

}
