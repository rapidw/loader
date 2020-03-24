package io.rapidw.loader.supervisor;

import com.google.protobuf.Timestamp;
import io.rapidw.loader.api.Agent;
import io.rapidw.loader.api.RoundReporter;
import io.rapidw.loader.common.gen.LoaderServiceOuterClass;
import io.rapidw.loader.common.utils.JarStreamClassLoader;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.util.ServiceLoader;
import java.util.concurrent.*;
import java.util.jar.JarInputStream;

@Component
@Slf4j
public class Supervisor {

    private volatile boolean running;
    private volatile boolean closed = false;
    private Agent agent;
    private final Reporter reporter;
    private final GrpcClient grpcClient;
    private final SupervisorConfig supervisorConfig;
    private int perAgentTotalLimit;
    private int durationLimit;
//    private RateLimiter rateLimiter;
    private ScheduledExecutorService executorService;
    private Semaphore semaphore;

    public Supervisor(SupervisorConfig supervisorConfig, MasterConfig masterConfig) {
        this.grpcClient = new GrpcClient(supervisorConfig, masterConfig, this);
        this.executorService = Executors.newScheduledThreadPool(1, new BasicThreadFactory.Builder()
            .namingPattern("workerthread-%d")
            .daemon(true)
            .build());
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

    public void config(LoaderServiceOuterClass.SupervisorConfig supervisorConfigReq) {
        if (supervisorConfigReq.getRpsLimit() != 0) {
//            this.rateLimiter = RateLimiter.create(supervisorConfigReq.getRpsLimit());
            this.semaphore = new Semaphore(supervisorConfigReq.getRpsLimit());
        }
        this.perAgentTotalLimit = supervisorConfigReq.getPerAgentTotalLimit();
        this.durationLimit = supervisorConfigReq.getDurationLimit();
    }

    public void configAgent(byte[] agentParamsBytes, byte[] agentConfigBytes) {
        agent.config(agentParamsBytes, agentConfigBytes);
    }

    @SneakyThrows
    public void start() {
        log.debug("first round start");
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
            log.debug("per agent: {}", this.perAgentTotalLimit);
//            if (this.rateLimiter != null) {
//                log.debug("rate limited");
//                this.rateLimiter.acquire();
//            }
            if (this.semaphore != null) {
                this.semaphore.acquire();
            }
            roundStart();
            this.perAgentTotalLimit--;
        }
        close();
    }

    @SneakyThrows
    public void close() {

        log.debug("require stop");
        CountDownLatch latch = new CountDownLatch(2);
        this.agent.stop(latch::countDown);
        this.reporter.stop(latch::countDown);
        log.debug("wait agent and reporter stop");
        latch.await();
        log.debug("agent and reporter stopped");

        grpcClient.sendComplete();
    }

    public void roundStart() {
        agent.roundStart(new RoundReporter() {
            private Instant startTime = Instant.now();

            @Override
            public void success(Instant startTime) {
                this.startTime = startTime;
                success();
            }

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
                semaphore.release();
            }

            @Override
            public void error() {
                log.info("supervisor round error recv");
                reporter.report(LoaderServiceOuterClass.Report.newBuilder()
                    .setStartTime(Timestamp.newBuilder()
                        .setSeconds(startTime.getEpochSecond())
                        .setNanos(startTime.getNano())
                        .build())
                    .setStatus(LoaderServiceOuterClass.Report.Status.ERROR)
                    .build());
                semaphore.release();
            }

            @Override
            public void timeout() {
                log.info("supervisor round timeout recv");
                reporter.report(LoaderServiceOuterClass.Report.newBuilder()
                    .setStartTime(Timestamp.newBuilder()
                        .setSeconds(startTime.getEpochSecond())
                        .setNanos(startTime.getNano())
                        .build())
                    .setStatus(LoaderServiceOuterClass.Report.Status.TIMEOUT)
                    .build());
                semaphore.release();
            }
        });

    }
}
