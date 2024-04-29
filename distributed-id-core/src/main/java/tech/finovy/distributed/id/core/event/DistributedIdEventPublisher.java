package tech.finovy.distributed.id.core.event;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DistributedIdEventPublisher {


    public static final List<DistributedIdStartEvent> events = new ArrayList<>();

    public void publishStartEvent(String desc) {
        // 发布自定义事件
        events.add(new DistributedIdStartEvent(desc));
    }
}
