package tech.finovy.distributed.id.grpc;

import tech.finovy.distributed.id.core.service.SnowflakeServiceImpl;
import tech.finovy.distributed.id.grpc.lib.DistributedIdProto;
import tech.finovy.distributed.id.grpc.lib.SnowflakeGrpc;
import net.devh.boot.grpc.server.service.GrpcService;

import javax.annotation.Nullable;

@GrpcService
public class SnowFlakeGrpcService extends SnowflakeGrpc.SnowflakeImplBase {
    private final SnowflakeServiceImpl coreService;
    private final GrpcBaseService grpcBaseService;

    public SnowFlakeGrpcService(@Nullable SnowflakeServiceImpl coreService, GrpcBaseService grpcBaseService) {
        this.coreService = coreService;
        this.grpcBaseService = grpcBaseService;
    }

    public void getId(DistributedIdProto.IdRequest request,
                      io.grpc.stub.StreamObserver<DistributedIdProto.IdResponse> responseObserver) {
        grpcBaseService.getId(coreService, request, responseObserver);
    }

    public void getIds(DistributedIdProto.IdListRequest request,
                       io.grpc.stub.StreamObserver<DistributedIdProto.IdListResponse> responseObserver) {
        grpcBaseService.getIds(coreService, request, responseObserver);
    }
}
