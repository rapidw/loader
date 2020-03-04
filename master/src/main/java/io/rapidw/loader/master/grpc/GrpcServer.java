package io.rapidw.loader.master.grpc;

import io.rapidw.loader.master.config.AppConfig;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GrpcServer {

    private Server server;
    private final GrpcService grpcService;
    private final AppConfig appConfig;

    @Autowired
    public GrpcServer(GrpcService grpcService, AppConfig appConfig) {
        this.grpcService = grpcService;
        this.appConfig = appConfig;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void start() throws Exception {
        server = ServerBuilder.forPort(appConfig.getGrpcServer().getPort()).addService(
                ServerInterceptors.intercept(grpcService, new GrpcInterceptor())).build();
        server.start();
        log.info("server start at {}", appConfig.getGrpcServer().getPort());
    }
}
