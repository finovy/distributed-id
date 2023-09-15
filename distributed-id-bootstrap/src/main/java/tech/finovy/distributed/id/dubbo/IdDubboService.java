package tech.finovy.distributed.id.dubbo;

import tech.finovy.distributed.id.DistributedIdService;
import tech.finovy.distributed.id.core.AbstractIdService;
import org.apache.dubbo.config.ServiceConfig;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: Ryan Luo
 * @Date: 2023/6/10 15:57
 */
@Service
public class IdDubboService {

    public IdDubboService(List<AbstractIdService> services) {
        for (AbstractIdService service : services) {
            ServiceConfig<DistributedIdService> dubbo = new ServiceConfig<>();
            dubbo.setInterface(DistributedIdService.class);
            dubbo.setRef(service);
            dubbo.setGroup(service.getType().name());
            final DubboBootstrap instance = DubboBootstrap.getInstance();
            instance.service(dubbo);
        }
    }
}
