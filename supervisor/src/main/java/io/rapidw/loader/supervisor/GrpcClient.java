package io.rapidw.loader.supervisor;


import io.grpc.Channel;
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


    public GrpcClient(SupervisorConfig supervisorConfig, MasterConfig masterConfig, Supervisor supervisor) {
        this.supervisorConfig = supervisorConfig;
        this.masterConfig = masterConfig;
        this.supervisor = supervisor;
    }

    public void start() throws Exception {
        CountDownLatch finishLatch = new CountDownLatch(1);

        Channel channel = ManagedChannelBuilder.forAddress(masterConfig.getHost(), masterConfig.getPort())
            .usePlaintext().build();
        LoaderServiceGrpc.LoaderServiceStub stub = LoaderServiceGrpc.newStub(channel);
        StreamObserver<LoaderServiceOuterClass.MasterMessage> responseObserver = new StreamObserver<LoaderServiceOuterClass.MasterMessage>() {
            @Override
            public void onNext(LoaderServiceOuterClass.MasterMessage masterMessage) {
                onMasterMessage(masterMessage);
            }

            @Override
            public void onError(Throwable t) {
                log.error("onError", t);
                supervisor.stop();
                finishLatch.countDown();
            }

            @Override
            public void onCompleted() {
                supervisor.stop();
                log.info("master closed connection, exit");
                finishLatch.countDown();
            }
        };

        requestObserver = stub.loaderChat(responseObserver);
        // supervisor向master发送RegisterReq，注册
        log.info("registering to master {}:{}", masterConfig.getHost(), masterConfig.getPort());
        requestObserver.onNext(LoaderServiceOuterClass.SupervisorMessage.newBuilder().
            setRegisterReq(LoaderServiceOuterClass.RegisterReq.newBuilder().setPath(supervisorConfig.getPath()).build()).build());

        finishLatch.await();
    }

    private void onMasterMessage(LoaderServiceOuterClass.MasterMessage masterMessage) {
        switch (masterMessage.getMessageOneofCase()) {
            case REGISTER_RESP:
                onRegisterResp();
                break;
            case LOAD_REQ:
                onLoadReq(masterMessage.getLoadReq());
                break;
            case SUPERVISOR_CONFIG_REQ:
                onSupervisorConfigReq(masterMessage.getSupervisorConfigReq());
                break;
            case AGENT_CONFIG_REQ:
                onAgentConfigReq(masterMessage.getAgentConfigReq());
                break;
            case START_REQ:
                onStartReq();
                break;
        }
    }

    private void onRegisterResp() {
        log.info("register success");
    }

    @SneakyThrows
    private void onLoadReq(LoaderServiceOuterClass.LoadReq loadReq) {
        log.info("loading agent");
        supervisor.loadAgent(loadReq.getData().toByteArray());
    }

    private void onSupervisorConfigReq(LoaderServiceOuterClass.SupervisorConfigReq supervisorConfigReq) {
        log.info("configuring supervisor");
        supervisor.config(supervisorConfigReq);
    }

    private void onAgentConfigReq(LoaderServiceOuterClass.AgentConfigReq agentConfigReq) {
        log.info("configuring agent");
        supervisor.configAgent(agentConfigReq.getAgentParamsBytes().toByteArray(), agentConfigReq.getAgentConfigBytes().toByteArray());
    }

    private void onStartReq() {
        log.info("starting");
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
}
