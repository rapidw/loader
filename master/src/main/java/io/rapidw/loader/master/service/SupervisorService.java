package io.rapidw.loader.master.service;

import io.grpc.stub.StreamObserver;
import io.rapidw.loader.common.gen.LoaderServiceOuterClass;
import io.rapidw.loader.master.config.AppConfig;
import io.rapidw.loader.master.entity.Supervisor;
import io.rapidw.loader.master.exception.AppException;
import io.rapidw.loader.master.exception.AppStatus;
import io.rapidw.loader.master.request.SupervisorDeployRequest;
import io.rapidw.loader.master.response.PagedResponse;
import io.rapidw.loader.master.response.SupervisorInfo;
import io.rapidw.sshdeployer.SshDeployer;
import io.rapidw.sshdeployer.SshDeployerOptions;
import io.rapidw.sshdeployer.task.CommandTask;
import io.rapidw.sshdeployer.task.ScpClasspathFileUploadTask;
import io.rapidw.sshdeployer.task.ScpLocalFileUploadTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.file.FileSystemNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class SupervisorService {

    private HashMap<Integer, Supervisor> supervisors = new HashMap<>();
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
            .task(
                new CommandTask(
                    String.format("nohup /root/%s/bin/java -jar supervisor.jar " +
                            "--master.host=%s --master.port=%s --supervisor.path=%s > nohup.log 2>&1 </dev/null &",
                        appConfig.getJre().getFolderName(),
                        appConfig.getGrpcServer().getHost(),
                        appConfig.getGrpcServer().getPort(),
                        supervisorDeployRequest.getPath()
                    )
                )
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

    public void addSupervisor(SocketAddress address, String path, StreamObserver<LoaderServiceOuterClass.MasterMessage> responseObserver) {
        int id = Supervisor.nextId();
        supervisors.put(id, Supervisor.builder().id(id).address(address).path(path).status(Supervisor.Status.READY).responseObserver(responseObserver).build());
    }

    public void removeSupervisor(int id) {

        Supervisor supervisor = supervisors.remove(id);
        supervisor.close();
    }

    public void removeSupervisor(SocketAddress address) {
        Integer id = null;
        for (Map.Entry<Integer, Supervisor> entry: this.supervisors.entrySet()) {
            if (entry.getValue().getAddress().equals(address)) {
                id = entry.getKey();
            }
        }
        if (id != null) {
            supervisors.remove(id);
        } else {
            throw new AppException(AppStatus.INTERNAL_SERVER_ERROR, "remove supervisor which does not exist");
        }
    }

    public void loadAgent(byte[] data) {
        supervisorsInUse.forEach(supervisor -> supervisor.loadAgent(data));
    }

    public void configSupervisor(List<Integer> supervisorIds, int qpsLimit, int durationLimit, int perAgentTotalLimit) {
        this.supervisorsInUse = new LinkedList<>();
        for (int id: supervisorIds) {
            Supervisor supervisor = this.supervisors.get(id);
            if (supervisor == null) {
                throw new AppException(AppStatus.BAD_REQUEST, "invalid supervisor id");
            }
            this.supervisorsInUse.add(this.supervisors.get(id));
        }
        this.supervisorsInUse.forEach(supervisor -> supervisor.configSupervisor(qpsLimit, perAgentTotalLimit, durationLimit));
    }

    public void configAgent(byte[] agentParamsBytes, List<byte[]> agentConfigBytesList) {
        if (agentConfigBytesList != null && agentConfigBytesList.size() == this.supervisorsInUse.size()) {
            for (int i = 0; i < supervisorsInUse.size(); i++) {
                supervisorsInUse.get(i).configAgent(agentParamsBytes, agentConfigBytesList.get(i));
            }
        } else {
            for (Supervisor supervisor : supervisorsInUse) {
                supervisor.configAgent(agentParamsBytes, null);
            }
        }
    }

    public void startAgent() {
        supervisorsInUse.forEach(Supervisor::startAgent);
    }

    public PagedResponse.PagedData<SupervisorInfo> getAllSupervisors() {
        List<SupervisorInfo> supervisorInfoList = new LinkedList<>();
        for (int i = 0; i < supervisors.size(); i++) {
            Supervisor current = supervisors.get(i);
            SocketAddress address = current.getAddress();
            if (address instanceof InetSocketAddress) {
                InetSocketAddress inetAddress = (InetSocketAddress) address;
                supervisorInfoList.add(SupervisorInfo.builder()
                    .id(i)
                    .host(inetAddress.getAddress().getHostAddress())
                    .path(current.getPath())
                    .status(current.getStatus())
                    .build()
                );
            }

        }
        return PagedResponse.PagedData.<SupervisorInfo>builder()
            .data(supervisorInfoList)
            .pageNum(1)
            .pageSize(20)
            .total(supervisorInfoList.size()).build();
    }

}
