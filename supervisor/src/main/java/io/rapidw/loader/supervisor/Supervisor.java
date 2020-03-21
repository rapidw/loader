package io.rapidw.loader.supervisor;

import com.google.common.util.concurrent.RateLimiter;
import com.google.protobuf.Timestamp;
import io.rapidw.loader.api.Agent;
import io.rapidw.loader.api.RoundReporter;
import io.rapidw.loader.common.gen.LoaderServiceOuterClass;
import io.rapidw.loader.common.utils.JarStreamClassLoader;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.util.ServiceLoader;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarInputStream;

@Component
@Slf4j
public class Supervisor {

    private volatile boolean running;
    private Agent agent;
    private final Reporter reporter;
    private final GrpcClient grpcClient;
    private final SupervisorConfig supervisorConfig;
    private int perAgentTotalLimit;
    private int durationLimit;
    private RateLimiter rateLimiter;
    private ScheduledExecutorService executorService;

    public Supervisor(SupervisorConfig supervisorConfig, MasterConfig masterConfig) {
        this.grpcClient = new GrpcClient(supervisorConfig, masterConfig, this);
        this.executorService = Executors.newScheduledThreadPool(1);
        this.reporter = new Reporter(this.grpcClient, this.executorService);
        this.supervisorConfig = supervisorConfig;
    }

    @SneakyThrows
    @EventListener(ApplicationReadyEvent.class)
    public void startGrpcClient() {
        this.grpcClient.start();
    }

    @SneakyThrows
    public void loadAgent(byte[] jar) {
        JarStreamClassLoader classLoader = new JarStreamClassLoader(new JarInputStream(new ByteArrayInputStream(jar)), this.getClass().getClassLoader());
        this.agent = ServiceLoader.load(Agent.class, classLoader).iterator().next();
    }

    public void config(LoaderServiceOuterClass.SupervisorConfigReq supervisorConfigReq) {
        if (supervisorConfigReq.getRpsLimit() != 0) {
            this.rateLimiter = RateLimiter.create(supervisorConfigReq.getRpsLimit());
        }
        this.perAgentTotalLimit = supervisorConfigReq.getPerAgentTotalLimit();
        this.durationLimit = supervisorConfigReq.getDurationLimit();
    }

    public void configAgent(byte[] agentParamsBytes, byte[] agentConfigBytes) {
        agent.config(agentParamsBytes, agentConfigBytes);
    }

    public void start() {
        this.reporter.start();
        if (this.durationLimit > 0) {
            this.executorService.schedule(() -> {
                this.running = false;
            }, this.durationLimit, TimeUnit.SECONDS);
        }
        if (this.perAgentTotalLimit > 0) {
            this.reporter.setPerAgentTotalCount(this.perAgentTotalLimit);
        }

        this.running = true;
        while (this.perAgentTotalLimit > 0 && this.running) {
            if (this.rateLimiter != null) {
                this.rateLimiter.acquire();
            }
            this.roundStart();
            this.perAgentTotalLimit--;
        }
        stop();
    }

    public void stop() {
        this.agent.stop();
        this.reporter.stop();
    }

    public void roundStart() {
        agent.roundStart(new RoundReporter() {
            private Instant startTime = Instant.now();
            @Override
            public void success() {
                log.info("agent round success");
                Instant finishTime = Instant.now();
                reporter.report(LoaderServiceOuterClass.Report.newBuilder()
                    .setStartTime(Timestamp.newBuilder()
                        .setSeconds(startTime.getEpochSecond())
                        .setNanos(startTime.getNano())
                        .build())
                    .setFinishTime(Timestamp.newBuilder()
                        .setSeconds(finishTime.getEpochSecond())
                        .setNanos(finishTime.getNano())
                        .build())
                    .setStatus(LoaderServiceOuterClass.Report.Status.SUCCESS)
                    .build()
                );
            }

            @Override
            public void error() {
                log.info("agent round error");
                reporter.report(LoaderServiceOuterClass.Report.newBuilder()
                    .setStatus(LoaderServiceOuterClass.Report.Status.ERROR)
                    .build());
            }

            @Override
            public void timeout() {
                log.info("agent round timeout");
                reporter.report(LoaderServiceOuterClass.Report.newBuilder()
                    .setStatus(LoaderServiceOuterClass.Report.Status.TIMEOUT)
                    .build());
            }
        });

    }
}
