package io.rapidw.loader.master.service;

import io.rapidw.loader.api.TestStrategy;
import io.rapidw.loader.common.utils.JarStreamClassLoader;
import io.rapidw.loader.master.entity.Testing;
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

    private Testing testing;
    private TestStrategy masterTestStratedy;
    private final SupervisorService supervisorService;

    public void start(TestingConfigRequest testingConfigRequest, String StrategyParams, byte[] agentParamsBytes, byte[] jarBytes) throws IOException {
        if (testing != null && testing.isRunning()) {
            throw new AppException(AppStatus.SYSTEM_ERROR, "another test is running");
        }

        this.testing = new Testing();
        this.testing.setOccupiedSupervisors(testingConfigRequest.getSupervisorIds());
        this.testing.setRpsLimit(testingConfigRequest.getRpsLimit());
        this.testing.setPerAgentTotalLimit(testingConfigRequest.getPerAgentTotalLimit());
        this.testing.setDurationLimit(testingConfigRequest.getDurationLimit());

        JarStreamClassLoader classLoader = new JarStreamClassLoader(new JarInputStream(new ByteArrayInputStream(jarBytes)), this.getClass().getClassLoader());
        this.masterTestStratedy = ServiceLoader.load(TestStrategy.class, classLoader).iterator().next();

        this.masterTestStratedy.init(StrategyParams);

        supervisorService.configSupervisor(testingConfigRequest.getSupervisorIds(), testingConfigRequest.getRpsLimit(),
            testingConfigRequest.getDurationLimit(), testingConfigRequest.getPerAgentTotalLimit());

        supervisorService.loadAgent(jarBytes);

        List<byte[]> agentConfigBytes = this.masterTestStratedy.generateAgentConfig(
            testingConfigRequest.getSupervisorIds().size(), testingConfigRequest.getPerAgentTotalLimit());
        supervisorService.configAgent(agentParamsBytes, agentConfigBytes);

        supervisorService.startAgent();
    }

    public Testing get() {
        return testing;
    }
}
