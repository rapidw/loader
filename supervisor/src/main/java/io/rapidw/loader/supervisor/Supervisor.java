package io.rapidw.loader.supervisor;

import com.google.common.util.concurrent.RateLimiter;
import com.google.protobuf.Timestamp;
import io.rapidw.loader.api.Agent;
import io.rapidw.loader.api.RoundReporter;
import io.rapidw.loader.common.gen.LoaderServiceOuterClass;
import io.rapidw.loader.common.utils.JarStreamClassLoader;
import lombok.SneakyThrows;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.util.ServiceLoader;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.jar.JarInputStream;

@Component
public class Supervisor {

    private volatile boolean running;
    private Agent agent;
    private final Reporter reporter;
    private final GrpcClient grpcClient;
    private final AppConfig appConfig;
    private volatile boolean perAgentTotalLimitEnabled = false;
    private int perAgentTotalLimit;
    private volatile boolean durationLimitEnabled = false;
    private int durationLimit;
    private RateLimiter rateLimiter;
    private AtomicInteger totalCount;
    private ScheduledExecutorService executorService;

    public Supervisor(AppConfig appConfig) {
        this.grpcClient = new GrpcClient(appConfig, this);
        this.executorService = Executors.newScheduledThreadPool(1);
        this.reporter = new Reporter(this.grpcClient, this.executorService);
        this.appConfig = appConfig;
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
        this.rateLimiter = RateLimiter.create(supervisorConfigReq.getRpsLimit());
        if (supervisorConfigReq.getPerAgentTotalLimit() != 0) {
            this.perAgentTotalLimitEnabled = true;
            this.perAgentTotalLimit = supervisorConfigReq.getPerAgentTotalLimit();
        }
        if (supervisorConfigReq.getDurationLimit() != 0) {
            this.durationLimitEnabled = true;
            this.durationLimit = supervisorConfigReq.getDurationLimit();
        }
    }

    public void configAgent(byte[] agentParamsBytes, byte[] agentConfigBytes) {
        agent.config(agentParamsBytes, agentConfigBytes);
    }

    public void start() {
        if (this.perAgentTotalLimitEnabled) {
            this.totalCount = new AtomicInteger();
        }
        if (this.durationLimitEnabled) {
            this.executorService.schedule(() -> {
                this.running = false;
            }, durationLimit, TimeUnit.SECONDS);
        }

        while (this.running) {
            this.rateLimiter.acquire();
            this.roundStart();
        }
        stop();
    }

    private void stop() {
        this.agent.stop();
        this.reporter.stop();
    }

    public void roundStart() {
        if (running) {
            if (perAgentTotalLimitEnabled) {
                if (totalCount.incrementAndGet() > this.perAgentTotalLimit) {
                    this.running = false;
                }
            }
            agent.roundStart(new RoundReporter() {
                private Instant startTime = Instant.now();
                @Override
                public void success() {
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
                    reporter.report(LoaderServiceOuterClass.Report.newBuilder()
                        .setStatus(LoaderServiceOuterClass.Report.Status.ERROR)
                        .build());
                }

                @Override
                public void timeout() {
                    reporter.report(LoaderServiceOuterClass.Report.newBuilder()
                        .setStatus(LoaderServiceOuterClass.Report.Status.TIMEOUT)
                        .build());
                }
            });
        } else {
            this.stop();
        }
    }
}
