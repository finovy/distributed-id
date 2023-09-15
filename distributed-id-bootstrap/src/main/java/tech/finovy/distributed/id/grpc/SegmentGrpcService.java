package tech.finovy.distributed.id.grpc;


import tech.finovy.distributed.id.core.service.SegmentServiceImpl;
import tech.finovy.distributed.id.grpc.lib.DistributedIdProto;
import tech.finovy.distributed.id.grpc.lib.SegmentGrpc;
import net.devh.boot.grpc.server.service.GrpcService;

import javax.annotation.Nullable;

@GrpcService
public class SegmentGrpcService extends SegmentGrpc.SegmentImplBase {

    private final SegmentServiceImpl coreService;
    private final GrpcBaseService grpcBaseService;

    public SegmentGrpcService(@Nullable SegmentServiceImpl coreService, GrpcBaseService grpcBaseService) {
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
