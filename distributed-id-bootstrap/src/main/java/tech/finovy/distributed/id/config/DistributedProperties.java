package tech.finovy.distributed.id.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "distributed")
public class DistributedProperties {

    private HttpProperties http = new HttpProperties();
    private GrpcProperties grpc = new GrpcProperties();
    private DubboProperties dubbo = new DubboProperties();

    public HttpProperties getHttp() {
        return http;
    }

    public void setHttp(HttpProperties http) {
        this.http = http;
    }

    public GrpcProperties getGrpc() {
        return grpc;
    }

    public void setGrpc(GrpcProperties grpc) {
        this.grpc = grpc;
    }

    public DubboProperties getDubbo() {
        return dubbo;
    }

    public void setDubbo(DubboProperties dubbo) {
        this.dubbo = dubbo;
    }

    public static class HttpProperties {
        private boolean enable;

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }
    }

    public static class GrpcProperties {
        private boolean enable;

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }
    }

    public static class DubboProperties {
        private boolean enable;

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }
    }
}
