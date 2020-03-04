package io.rapidw.loader.master.grpc;

import io.rapidw.loader.common.gen.LoaderServiceGrpc;
import io.rapidw.loader.common.gen.LoaderServiceOuterClass;
import io.rapidw.loader.master.service.ReportService;
import io.rapidw.loader.master.service.SupervisorService;
import io.grpc.Context;
import io.grpc.stub.StreamObserver;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.SocketAddress;
import java.util.List;

@Component
@Slf4j
public class GrpcService extends LoaderServiceGrpc.LoaderServiceImplBase {

    private final ReportService reportService;
    private final SupervisorService supervisorService;
    @Getter
    private StreamObserver<LoaderServiceOuterClass.MasterMessage> responseObserver;

    public GrpcService(SupervisorService supervisorService, ReportService reportService) {
        this.supervisorService = supervisorService;
        this.reportService = reportService;
    }


    @Override
    public StreamObserver<LoaderServiceOuterClass.SupervisorMessage> loaderChat(
        StreamObserver<LoaderServiceOuterClass.MasterMessage> responseObserver) {

        this.responseObserver = responseObserver;

        return new StreamObserver<LoaderServiceOuterClass.SupervisorMessage>() {
            @Override
            public void onNext(LoaderServiceOuterClass.SupervisorMessage supervisorMessage) {
                SocketAddress address = GrpcInterceptor.ADDRESS_KEY.get(Context.current());
                switch (supervisorMessage.getMessageOneofCase()) {
                    case REGISTER_REQ:
                        log.info("registering new agent: {}", address);
                        supervisorService.addSupervisor(address, responseObserver);
//                        agentService.printAgentList();
                        responseObserver.onNext(
                                LoaderServiceOuterClass.MasterMessage.newBuilder()
                                        .setRegisterResp(LoaderServiceOuterClass.RegisterResp.newBuilder()
                                                .build())
                                        .build());
                        break;
                    case SUPERVISOR_CONFIG_RESP:
                        log.info("supervisor config resp: {}", address);
                        onSupervisorConfigResp(supervisorMessage);
                        break;
                    case AGENT_CONFIG_RESP:
                        log.info("agent config resp: {}", address);
                        onAgentConfigResp(supervisorMessage);
                        break;
                    case LOAD_RESP:
                        log.info("load resp: {}", address);
                        onLoadResp(supervisorMessage);
                        break;
                    case REPORTS:
                        log.info("reports: {}", address);
                        onReports(supervisorMessage);
                        break;
                    default:
                        log.error("unknown message type");
                        break;
                }
            }

            @Override
            public void onError(Throwable throwable) {
                SocketAddress address = GrpcInterceptor.ADDRESS_KEY.get(Context.current());
                log.info("error supervisor at {}", address);

                supervisorService.removeSupervisor(address);
            }

            @Override
            public void onCompleted() {
                log.info("onCompleted");
            }
        };
    }

    private void onReports(LoaderServiceOuterClass.SupervisorMessage supervisorMessage) {
        List<LoaderServiceOuterClass.Report> reports = supervisorMessage.getReports().getReportList();
        reports.forEach(reportService::addReport);
    }

    private void onLoadResp(LoaderServiceOuterClass.SupervisorMessage supervisorMessage) {
    }

    private void onAgentConfigResp(LoaderServiceOuterClass.SupervisorMessage supervisorMessage) {
    }

    private void onSupervisorConfigResp(LoaderServiceOuterClass.SupervisorMessage supervisorMessage) {

    }
}
