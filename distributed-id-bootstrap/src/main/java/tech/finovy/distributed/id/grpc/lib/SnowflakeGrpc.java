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
public final class SnowflakeGrpc {

  private SnowflakeGrpc() {}

  public static final String SERVICE_NAME = "Snowflake";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<DistributedIdProto.IdRequest,
      DistributedIdProto.IdResponse> METHOD_GET_ID =
      io.grpc.MethodDescriptor.<DistributedIdProto.IdRequest, DistributedIdProto.IdResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "Snowflake", "getId"))
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
              "Snowflake", "getIds"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              DistributedIdProto.IdListRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              DistributedIdProto.IdListResponse.getDefaultInstance()))
          .build();

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static SnowflakeStub newStub(io.grpc.Channel channel) {
    return new SnowflakeStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static SnowflakeBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new SnowflakeBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static SnowflakeFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new SnowflakeFutureStub(channel);
  }

  /**
   */
  public static abstract class SnowflakeImplBase implements io.grpc.BindableService {

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
  public static final class SnowflakeStub extends io.grpc.stub.AbstractStub<SnowflakeStub> {
    private SnowflakeStub(io.grpc.Channel channel) {
      super(channel);
    }

    private SnowflakeStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected SnowflakeStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new SnowflakeStub(channel, callOptions);
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
  public static final class SnowflakeBlockingStub extends io.grpc.stub.AbstractStub<SnowflakeBlockingStub> {
    private SnowflakeBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private SnowflakeBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected SnowflakeBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new SnowflakeBlockingStub(channel, callOptions);
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
  public static final class SnowflakeFutureStub extends io.grpc.stub.AbstractStub<SnowflakeFutureStub> {
    private SnowflakeFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private SnowflakeFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected SnowflakeFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new SnowflakeFutureStub(channel, callOptions);
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
    private final SnowflakeImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(SnowflakeImplBase serviceImpl, int methodId) {
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

  private static final class SnowflakeDescriptorSupplier implements io.grpc.protobuf.ProtoFileDescriptorSupplier {
    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return DistributedIdProto.getDescriptor();
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (SnowflakeGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new SnowflakeDescriptorSupplier())
              .addMethod(METHOD_GET_ID)
              .addMethod(METHOD_GET_IDS)
              .build();
        }
      }
    }
    return result;
  }
}
