package tech.finovy.distributed.id;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import tech.finovy.distributed.id.core.event.DistributedIdEventPublisher;
import tech.finovy.distributed.id.core.event.DistributedIdStartEvent;

import java.util.List;

@Slf4j
@Component
public class DistributedApplicationReadyListener implements ApplicationListener<ApplicationReadyEvent> {

    @Resource
    DistributedIdEventPublisher publisher;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        final List<DistributedIdStartEvent> events = publisher.events;
        for (int i = 0; i < events.size(); i++) {
            log.info("{}.{}", i + 1, events.get(i).getDesc());
        }
    }
}
