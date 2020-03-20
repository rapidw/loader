package io.rapidw.loader.master.service;

import io.rapidw.loader.api.TestStrategy;
import io.rapidw.loader.common.utils.JarStreamClassLoader;
import io.rapidw.loader.master.exception.AppException;
import io.rapidw.loader.master.exception.AppStatus;
import io.rapidw.loader.master.request.TestingConfigRequest;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.ServiceLoader;
import java.util.jar.JarInputStream;

@Service
public class TestingService {

    public TestingService(SupervisorService supervisorService) {
        this.supervisorService = supervisorService;
    }

    public enum Mode {BURST, CONTINUOUS}
    private volatile boolean running = false;
    private Integer agentCount;
    private int rpsLimit;
    private Integer perAgentTotalLimit;
    private Integer durationLimit;
    private TestStrategy masterTestStratedy;
    private final SupervisorService supervisorService;

    public void start(TestingConfigRequest testingConfigRequest, byte[] agentParamsBytes, byte[] strategyConfigBytes, byte[] jarBytes) throws IOException {
        if (running) {
            throw new AppException(AppStatus.SYSTEM_ERROR, "another test is running");
        }

        this.agentCount = testingConfigRequest.getSupervisorsIds().size();
        this.rpsLimit = testingConfigRequest.getThroughputLimit();
        this.perAgentTotalLimit = testingConfigRequest.getPerAgentTotalLimit();
        this.durationLimit = testingConfigRequest.getDurationLimit();

        JarStreamClassLoader classLoader = new JarStreamClassLoader(new JarInputStream(new ByteArrayInputStream(jarBytes)), this.getClass().getClassLoader());
        this.masterTestStratedy = ServiceLoader.load(TestStrategy.class, classLoader).iterator().next();

        this.masterTestStratedy.init(strategyConfigBytes);

        supervisorService.configSupervisor(agentCount, this.rpsLimit, this.perAgentTotalLimit, this.durationLimit);

        supervisorService.loadAgent(jarBytes);

        List<byte[]> agentConfigBytes = this.masterTestStratedy.generateAgentConfig(this.agentCount, this.perAgentTotalLimit);
        supervisorService.configAgent(agentParamsBytes, agentConfigBytes);

        supervisorService.startAgent();
    }
}
