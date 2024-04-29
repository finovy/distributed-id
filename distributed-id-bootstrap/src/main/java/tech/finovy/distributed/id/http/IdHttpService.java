package tech.finovy.distributed.id.http;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import tech.finovy.distributed.id.BaseService;
import tech.finovy.distributed.id.config.DistributedProperties;
import tech.finovy.distributed.id.constants.TypeEnum;
import tech.finovy.distributed.id.core.AbstractIdService;
import tech.finovy.distributed.id.core.CoreServiceManager;
import tech.finovy.distributed.id.core.event.DistributedIdEventPublisher;
import tech.finovy.distributed.id.exception.BusinessException;
import tech.finovy.distributed.id.response.R;

import java.util.List;

import static tech.finovy.distributed.id.util.WrapperUtil.wrapper;
import static tech.finovy.distributed.id.util.WrapperUtil.wrapperList;

@Slf4j
@Component
public class IdHttpService extends BaseService {

    private final CoreServiceManager manager;

    private final DistributedProperties properties;

    public IdHttpService(CoreServiceManager manager, DistributedIdEventPublisher eventPublisher, DistributedProperties properties) {
        this.manager = manager;
        this.properties= properties;
        eventPublisher.publishStartEvent("Http-protocol started");
    }

    @Bean
    public RouterFunction<ServerResponse> idRoutes() {
        if (!properties.getHttp().isEnable()) {
            log.info("Http is disable,will skip");
            return null;
        }
        return RouterFunctions.route()
                .GET("/distributed/{type}/{key}", request -> handleIdRequest(request, false, false))
                .GET("/distributed/{type}/{key}/{batch}", request -> handleIdRequest(request, true, false))
                .GET("/distributed/{type}/{key}/{prefix}/{length}", request -> handleIdRequest(request, false, true))
                .GET("/distributed/{type}/{key}/{batch}/{prefix}/{length}", request -> handleIdRequest(request, true, true))
                .build();
    }

    private Mono<ServerResponse> handleIdRequest(ServerRequest request, boolean batch, boolean join) {
        String type = request.pathVariable("type");
        String key = request.pathVariable("key");
        String prefix = null;
        Integer length = null;
        if (join) {
            prefix = request.pathVariable("prefix");
            length = Integer.valueOf(request.pathVariable("length"));
        }
        if (batch) {
            int batchCount = Integer.parseInt(request.pathVariable("batch"));
            return ServerResponse.ok().bodyValue(process(type, key, batchCount, prefix, length));
        }
        return ServerResponse.ok().bodyValue(process(type, key, null, prefix, length));
    }


    private R process(String type, String key, Integer batch, String prefix, Integer length) {
        try {
            final AbstractIdService t = manager.services.get(TypeEnum.valueOf(type));
            if (batch == null) {
                final Long id = this.getIds(t, key, 1);
                if (StringUtils.isBlank(prefix)) {
                    return R.data(id);
                }
                return R.data(wrapper(id, prefix, length));
            } else {
                final List<Long> ids = this.getIds(t, key, batch);
                if (StringUtils.isBlank(prefix)) {
                    return R.data(ids);
                }
                return R.data(wrapperList(ids, prefix, length));
            }
        } catch (BusinessException e) {
            return R.fail(500, e.getMessage());
        } catch (Exception e) {
            log.error("Exception:{}", e.getMessage(), e);
            return R.fail(500, "distributed-id server error");
        }
    }
}
