package io.rapidw.loader.supervisor;


import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import io.rapidw.loader.common.gen.LoaderServiceGrpc;
import io.rapidw.loader.common.gen.LoaderServiceOuterClass;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class GrpcClient {
    private final SupervisorConfig supervisorConfig;
    private final MasterConfig masterConfig;
    private StreamObserver<LoaderServiceOuterClass.SupervisorMessage> requestObserver;
    private final Supervisor supervisor;
    private CountDownLatch finishLatch;


    public GrpcClient(SupervisorConfig supervisorConfig, MasterConfig masterConfig, Supervisor supervisor) {
        this.supervisorConfig = supervisorConfig;
        this.masterConfig = masterConfig;
        this.supervisor = supervisor;
    }

    public void start() throws Exception {
        finishLatch = new CountDownLatch(1);

        ManagedChannel channel = ManagedChannelBuilder.forAddress(masterConfig.getHost(), masterConfig.getPort()).maxInboundMessageSize(5000000)
            .usePlaintext().build();
        LoaderServiceGrpc.LoaderServiceStub stub = LoaderServiceGrpc.newStub(channel);
        StreamObserver<LoaderServiceOuterClass.MasterMessage> responseObserver = new StreamObserver<LoaderServiceOuterClass.MasterMessage>() {
            @Override
            public void onNext(LoaderServiceOuterClass.MasterMessage masterMessage) {
                onMasterMessage(masterMessage);
            }

            // on error from master, close
            @Override
            public void onError(Throwable t) {
                log.error("grpc connection error", t);
                forceClose();
            }

            // on completed from master, close
            @Override
            public void onCompleted() {
                log.info("master closed grpc connection");
                forceClose();
            }
        };

        requestObserver = stub.loaderChat(responseObserver);
        log.info("registering to master {}:{}", masterConfig.getHost(), masterConfig.getPort());
        requestObserver.onNext(LoaderServiceOuterClass.SupervisorMessage.newBuilder().
            setRegister(LoaderServiceOuterClass.Register.newBuilder().setPath(supervisorConfig.getPath()).build()).build());

        finishLatch.await();
        log.info("grpc client wait finished, close supervisor side");
        requestObserver.onCompleted();
    }

    private void forceClose() {
        log.info("force close");
        System.exit(0);
    }

    public void close() {
        log.debug("closing, release the latch");
        finishLatch.countDown();
    }

    private void onMasterMessage(LoaderServiceOuterClass.MasterMessage masterMessage) {
        switch (masterMessage.getMessageOneofCase()) {
            case LOAD:
                onLoadReq(masterMessage.getLoad());
                break;
            case SUPERVISOR_CONFIG:
                onSupervisorConfigReq(masterMessage.getSupervisorConfig());
                break;
            case AGENT_CONFIG:
                onAgentConfigReq(masterMessage.getAgentConfig());
                break;
            case START:
                onStartReq();
                break;
            case BYE:
                close();
        }
    }

    @SneakyThrows
    private void onLoadReq(LoaderServiceOuterClass.Load loadReq) {
        log.info("loading agent");
        supervisor.loadAgent(loadReq.getData().toByteArray());
    }

    private void onSupervisorConfigReq(LoaderServiceOuterClass.SupervisorConfig supervisorConfig) {
        log.info("configuring supervisor");
        supervisor.config(supervisorConfig);
    }

    private void onAgentConfigReq(LoaderServiceOuterClass.AgentConfig agentConfig) {
        log.info("configuring agent");
        supervisor.configAgent(agentConfig.getAgentParamsBytes().toByteArray(), agentConfig.getAgentConfigBytes().toByteArray());
    }

    private void onStartReq() {
        log.debug("startReq received");
        supervisor.start();
    }

    public void sendReport(List<LoaderServiceOuterClass.Report> reports) {
        this.requestObserver.onNext(LoaderServiceOuterClass.SupervisorMessage.newBuilder()
            .setReports(LoaderServiceOuterClass.Reports.newBuilder()
                .addAllReport(reports)
                .build())
            .build()
        );
    }

    public void sendComplete() {
        this.requestObserver.onNext(LoaderServiceOuterClass.SupervisorMessage.newBuilder()
            .setComplete(LoaderServiceOuterClass.Complete.newBuilder().build())
            .build()
        );
    }
}
