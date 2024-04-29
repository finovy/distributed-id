package tech.finovy.distributed.id.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import tech.finovy.distributed.id.constants.TypeEnum;
import tech.finovy.distributed.id.core.event.DistributedIdEventPublisher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Ryan Luo
 * @Date: 2023/6/5 17:43
 */
@Slf4j
@Service
public class CoreServiceManager {

    public Map<TypeEnum, AbstractIdService> services = new HashMap<>(3);

    private final List<AbstractIdService> serviceList;
    private final DistributedIdEventPublisher eventPublisher;

    public CoreServiceManager(List<AbstractIdService> services, DistributedIdEventPublisher eventPublisher) {
        this.serviceList = services;
        this.eventPublisher = eventPublisher;
    }

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        for (AbstractIdService service : serviceList) {
            service.init();
            this.services.put(service.getType(), service);
            eventPublisher.publishStartEvent(service.getType() + "-provider started");
        }
    }

}
