package tech.finovy.distributed.id.grpc;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import tech.finovy.distributed.id.DistributedIdService;
import tech.finovy.distributed.id.constants.Status;
import tech.finovy.distributed.id.core.event.DistributedIdEventPublisher;
import tech.finovy.distributed.id.grpc.lib.DistributedIdProto;
import tech.finovy.distributed.id.BaseService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: Ryan Luo
 * @Date: 2023/6/5 17:06
 */
@ConditionalOnProperty(value = "distributed.grpc.enable", havingValue = "true", matchIfMissing = true)
@Service
public class GrpcBaseService extends BaseService {

    public GrpcBaseService(DistributedIdEventPublisher eventPublisher) {
        eventPublisher.publishStartEvent("Grpc-protocol started");
    }

    protected void getId(DistributedIdService t, DistributedIdProto.IdRequest request,
                         io.grpc.stub.StreamObserver<DistributedIdProto.IdResponse> responseObserver) {
        // 处理请求并生成响应
        String key = request.getKey();
        // 构建响应消息
        DistributedIdProto.IdResponse response = DistributedIdProto.IdResponse.newBuilder()
                .setData(t.getId(key))
                .setMessage(Status.SUCCESS.getMessage())
                .setCode(Status.SUCCESS.getCode())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    protected void getIds(DistributedIdService t, DistributedIdProto.IdListRequest request,
                          io.grpc.stub.StreamObserver<DistributedIdProto.IdListResponse> responseObserver) {
        // 处理请求并生成响应
        String key = request.getKey();
        int batch = request.getBatch();
        // 构建响应消息
        final List<Long> ids = t.getIds(key, batch);
        final DistributedIdProto.IdListResponse.Builder builder = DistributedIdProto.IdListResponse.newBuilder();
        for (Long id : ids) {
            builder.addData(id);
        }
        builder.setMessage(Status.SUCCESS.getMessage());
        final DistributedIdProto.IdListResponse response = builder.setCode(Status.SUCCESS.getCode()).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
