package tech.finovy.distributed.id.grpc.lib;

import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
public final class SegmentGrpc {

  private SegmentGrpc() {}

  public static final String SERVICE_NAME = "Segment";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<DistributedIdProto.IdRequest,
      DistributedIdProto.IdResponse> METHOD_GET_ID =
      io.grpc.MethodDescriptor.<DistributedIdProto.IdRequest, DistributedIdProto.IdResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "Segment", "getId"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              DistributedIdProto.IdRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              DistributedIdProto.IdResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<DistributedIdProto.IdListRequest,
      DistributedIdProto.IdListResponse> METHOD_GET_IDS =
      io.grpc.MethodDescriptor.<DistributedIdProto.IdListRequest, DistributedIdProto.IdListResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "Segment", "getIds"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              DistributedIdProto.IdListRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              DistributedIdProto.IdListResponse.getDefaultInstance()))
          .build();

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static SegmentStub newStub(io.grpc.Channel channel) {
    return new SegmentStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static SegmentBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new SegmentBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static SegmentFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new SegmentFutureStub(channel);
  }

  /**
   */
  public static abstract class SegmentImplBase implements io.grpc.BindableService {

    /**
     */
    public void getId(DistributedIdProto.IdRequest request,
        io.grpc.stub.StreamObserver<DistributedIdProto.IdResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_GET_ID, responseObserver);
    }

    /**
     */
    public void getIds(DistributedIdProto.IdListRequest request,
        io.grpc.stub.StreamObserver<DistributedIdProto.IdListResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_GET_IDS, responseObserver);
    }

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            METHOD_GET_ID,
            asyncUnaryCall(
              new MethodHandlers<
                DistributedIdProto.IdRequest,
                DistributedIdProto.IdResponse>(
                  this, METHODID_GET_ID)))
          .addMethod(
            METHOD_GET_IDS,
            asyncUnaryCall(
              new MethodHandlers<
                DistributedIdProto.IdListRequest,
                DistributedIdProto.IdListResponse>(
                  this, METHODID_GET_IDS)))
          .build();
    }
  }

  /**
   */
  public static final class SegmentStub extends io.grpc.stub.AbstractStub<SegmentStub> {
    private SegmentStub(io.grpc.Channel channel) {
      super(channel);
    }

    private SegmentStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected SegmentStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new SegmentStub(channel, callOptions);
    }

    /**
     */
    public void getId(DistributedIdProto.IdRequest request,
        io.grpc.stub.StreamObserver<DistributedIdProto.IdResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_GET_ID, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getIds(DistributedIdProto.IdListRequest request,
        io.grpc.stub.StreamObserver<DistributedIdProto.IdListResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_GET_IDS, getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class SegmentBlockingStub extends io.grpc.stub.AbstractStub<SegmentBlockingStub> {
    private SegmentBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private SegmentBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected SegmentBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new SegmentBlockingStub(channel, callOptions);
    }

    /**
     */
    public DistributedIdProto.IdResponse getId(DistributedIdProto.IdRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_GET_ID, getCallOptions(), request);
    }

    /**
     */
    public DistributedIdProto.IdListResponse getIds(DistributedIdProto.IdListRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_GET_IDS, getCallOptions(), request);
    }
  }

  /**
   */
  public static final class SegmentFutureStub extends io.grpc.stub.AbstractStub<SegmentFutureStub> {
    private SegmentFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private SegmentFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected SegmentFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new SegmentFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<DistributedIdProto.IdResponse> getId(
        DistributedIdProto.IdRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_GET_ID, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<DistributedIdProto.IdListResponse> getIds(
        DistributedIdProto.IdListRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_GET_IDS, getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_ID = 0;
  private static final int METHODID_GET_IDS = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final SegmentImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(SegmentImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_ID:
          serviceImpl.getId((DistributedIdProto.IdRequest) request,
              (io.grpc.stub.StreamObserver<DistributedIdProto.IdResponse>) responseObserver);
          break;
        case METHODID_GET_IDS:
          serviceImpl.getIds((DistributedIdProto.IdListRequest) request,
              (io.grpc.stub.StreamObserver<DistributedIdProto.IdListResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @Override
    @SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static final class SegmentDescriptorSupplier implements io.grpc.protobuf.ProtoFileDescriptorSupplier {
    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return DistributedIdProto.getDescriptor();
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (SegmentGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new SegmentDescriptorSupplier())
              .addMethod(METHOD_GET_ID)
              .addMethod(METHOD_GET_IDS)
              .build();
        }
      }
    }
    return result;
  }
}
