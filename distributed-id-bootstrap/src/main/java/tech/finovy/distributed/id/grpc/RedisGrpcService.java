package tech.finovy.distributed.id.grpc;

import tech.finovy.distributed.id.core.service.RedisServiceImpl;
import tech.finovy.distributed.id.grpc.lib.DistributedIdProto;
import tech.finovy.distributed.id.grpc.lib.RedisGrpc;
import net.devh.boot.grpc.server.service.GrpcService;

import javax.annotation.Nullable;

@GrpcService
public class RedisGrpcService extends  RedisGrpc.RedisImplBase {

    private final RedisServiceImpl coreService;
    private final GrpcBaseService grpcBaseService;

    public RedisGrpcService(@Nullable RedisServiceImpl coreService, GrpcBaseService grpcBaseService) {
        this.coreService = coreService;
        this.grpcBaseService = grpcBaseService;
    }

    public void getId(DistributedIdProto.IdRequest request,
                      io.grpc.stub.StreamObserver<DistributedIdProto.IdResponse> responseObserver) {
        grpcBaseService.getId(coreService,request,responseObserver);
    }

    public void getIds(DistributedIdProto.IdListRequest request,
                      io.grpc.stub.StreamObserver<DistributedIdProto.IdListResponse> responseObserver) {
        grpcBaseService.getIds(coreService,request,responseObserver);
    }

}
