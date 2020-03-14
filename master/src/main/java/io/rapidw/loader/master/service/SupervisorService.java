package io.rapidw.loader.master.service;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import io.rapidw.loader.common.gen.LoaderServiceOuterClass;
import io.rapidw.loader.master.config.AppConfig;
import io.rapidw.loader.master.exception.AppException;
import io.rapidw.loader.master.exception.AppStatus;
import io.rapidw.loader.master.request.SupervisorDeployRequest;
import io.rapidw.sshdeployer.SshDeployer;
import io.rapidw.sshdeployer.SshDeployerOptions;
import io.rapidw.sshdeployer.task.CommandTask;
import io.rapidw.sshdeployer.task.ScpClasspathFileUploadTask;
import io.rapidw.sshdeployer.task.ScpLocalFileUploadTask;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.SocketAddress;
import java.nio.file.FileSystemNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SupervisorService {

    private List<Supervisor> supervisors = new ArrayList<>();
    private List<Supervisor> supervisorsInUse;

    private final AppConfig appConfig;

    public SupervisorService(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    public void deploySupervisor(SupervisorDeployRequest supervisorDeployRequest) {
        SshDeployerOptions options = SshDeployerOptions.builder()
            .host(supervisorDeployRequest.getHost())
            .port(supervisorDeployRequest.getPort())
            .username(supervisorDeployRequest.getUsername())
            .password(supervisorDeployRequest.getPassword())
            .build();
        SshDeployer deployer = new SshDeployer(options);
        deployer.task(new ScpClasspathFileUploadTask("/supervisor.jar", supervisorDeployRequest.getPath() + "/supervisor.jar"))
            .task(new ScpLocalFileUploadTask(appConfig.getJre().getFilePath(), supervisorDeployRequest.getPath() + "/jre.tar.gz"))
            .task(new CommandTask("tar zxf /root/jre.tar.gz"))
            .task(new CommandTask(String.format("nohup /root/%s/bin/java -jar supervisor.jar " +
                    "--supervisor.grpcServer.host=%s --supervisor.grpcServer.port=%s &",
                appConfig.getJre().getFolderName(),
                appConfig.getGrpcServer().getHost(),
                appConfig.getGrpcServer().getPort()))
            );

        try {
            deployer.run();
        } catch (FileSystemNotFoundException e) {
            throw new AppException(AppStatus.BAD_REQUEST, "file not found");
        } catch (Exception e) {
            log.error("ssh deploy error", e);
            throw new AppException(AppStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void addSupervisor(SocketAddress address, StreamObserver<LoaderServiceOuterClass.MasterMessage> responseObserver) {
        supervisors.add(Supervisor.builder().address(address).responseObserver(responseObserver).build());
    }

    public void removeSupervisor(SocketAddress address) {

        int i;
        for (i = 0; i < supervisors.size(); i++) {
            if (supervisors.get(i).getAddress().equals(address)) {
                break;
            }
        }
        supervisors.remove(i);
    }

    public void loadAgent(byte[] data) {
        supervisorsInUse.forEach(supervisor -> supervisor.loadAgent(data));
    }

    public void configSupervisor(int agentCount, int qpsLimit, int perAgentTotalLimit, int durationLimit) {
        this.supervisorsInUse = this.supervisors.subList(0, agentCount);
        this.supervisorsInUse.forEach(supervisor -> supervisor.configSupervisor(qpsLimit, perAgentTotalLimit, durationLimit));
    }

    public void configAgent(byte[] agentParamsBytes, List<byte[]> agentConfigBytes) {
        for (int i = 0; i < supervisorsInUse.size(); i++) {
            supervisorsInUse.get(i).configAgent(agentParamsBytes, agentConfigBytes.get(i));
        }
    }

    public void startAgent() {
        supervisorsInUse.forEach(Supervisor::startAgent);
    }

    @Builder
    @Getter
    private static class Supervisor {
        private SocketAddress address;
        private StreamObserver<LoaderServiceOuterClass.MasterMessage> responseObserver;

        public void loadAgent(byte[] data) {
            responseObserver.onNext(LoaderServiceOuterClass.MasterMessage.newBuilder()
                .setLoadReq(LoaderServiceOuterClass.LoadReq.newBuilder()
                    .setData(ByteString.copyFrom(data))
                    .build())
                .build()
            );
        }

        public void configAgent(byte[] agentParamBytes, byte[] agentConfigBytes) {
            responseObserver.onNext(LoaderServiceOuterClass.MasterMessage.newBuilder()
                .setAgentConfigReq(LoaderServiceOuterClass.AgentConfigReq.newBuilder()
                    .setAgentParamsBytes(ByteString.copyFrom(agentParamBytes))
                    .setAgentConfigBytes(ByteString.copyFrom(agentConfigBytes))
                    .build())
                .build()
            );
        }

        public void configSupervisor(int rpsLimit, int perAgentTotalLimit, int durationLimit) {
            responseObserver.onNext(LoaderServiceOuterClass.MasterMessage.newBuilder()
                .setSupervisorConfigReq(LoaderServiceOuterClass.SupervisorConfigReq.newBuilder()
                    .setRpsLimit(rpsLimit)
                    .setPerAgentTotalLimit(perAgentTotalLimit)
                    .setDurationLimit(durationLimit)
                    .build())
                .build()
            );
        }

        public void startAgent() {
            responseObserver.onNext(LoaderServiceOuterClass.MasterMessage.newBuilder()
                .setStartReq(LoaderServiceOuterClass.StartReq.newBuilder()
                    .build())
                .build()
            );
        }
    }
}
