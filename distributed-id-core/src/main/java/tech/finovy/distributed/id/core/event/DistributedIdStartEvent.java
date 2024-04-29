package tech.finovy.distributed.id.core.event;


public class DistributedIdStartEvent {

    private final String desc;

    public DistributedIdStartEvent(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
