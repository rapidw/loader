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
                    case REGISTER:
                        String path = supervisorMessage.getRegister().getPath();
                        log.info("registering new agent: {}:{}", address, path);
                        supervisorService.addSupervisor(address, path, responseObserver);
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
                responseObserver.onCompleted();
            }

            @Override
            public void onCompleted() {
                SocketAddress address = GrpcInterceptor.ADDRESS_KEY.get(Context.current());
                log.info("onCompleted: {}", address);
                supervisorService.removeSupervisor(address);
                responseObserver.onCompleted();
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
