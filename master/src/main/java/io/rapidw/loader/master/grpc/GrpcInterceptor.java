package io.rapidw.loader.master.grpc;

import io.grpc.*;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;

@Slf4j
public class GrpcInterceptor implements ServerInterceptor {

    public static final Context.Key<SocketAddress> ADDRESS_KEY = Context.key("addr");

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
        SocketAddress address = call.getAttributes().get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR);
        Context context = Context.current().withValue(ADDRESS_KEY, address);
        return Contexts.interceptCall(context, call, headers, next);
    }
}
