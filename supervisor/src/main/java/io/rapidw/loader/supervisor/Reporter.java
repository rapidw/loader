package io.rapidw.loader.supervisor;

import io.rapidw.loader.api.StopCallback;
import io.rapidw.loader.common.gen.LoaderServiceOuterClass;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class Reporter {

    @Setter
    private int perAgentTotalCount;
    private final GrpcClient grpcClient;
    private final ScheduledExecutorService executorService;
    private ScheduledFuture future;
    private StopCallback stopCallback;
    private AtomicInteger reportCount = new AtomicInteger();

    private LinkedTransferQueue<LoaderServiceOuterClass.Report> reports = new LinkedTransferQueue<>();

    public Reporter(GrpcClient grpcClient, ScheduledExecutorService executorService) {
        this.grpcClient = grpcClient;
        this.executorService = executorService;
    }
    public void start(int perAgentTotalCount) {
        this.perAgentTotalCount = perAgentTotalCount;
        start();
    }

    public void start() {
        log.debug("reporter start");
        this.future = this.executorService.scheduleAtFixedRate(this::reportTask, 0, 3, TimeUnit.SECONDS);
    }

    @SneakyThrows
    public void stop(StopCallback stopCallback) {
        log.debug("reporter stop received");
        this.stopCallback = stopCallback;
//        reportTask();
        if (perAgentTotalCount == 0) {
            this.future.cancel(false);
        }
    }

    public void report(LoaderServiceOuterClass.Report report) {
        log.debug("new report received");
        if (perAgentTotalCount > 0) {
            perAgentTotalCount--;
        }
        reports.put(report);
    }

    public void reportTask() {
        if (reports.size() > 0) {
            List<LoaderServiceOuterClass.Report> reportList = new LinkedList<>();
            reports.drainTo(reportList);
            log.debug("reportTask: report size: {}, {}", reportList.size(), reportCount.addAndGet(reportList.size()));
            grpcClient.sendReport(reportList);
        }
        if (perAgentTotalCount == 0) {
            stopCallback.stopped();
        }
    }
}
