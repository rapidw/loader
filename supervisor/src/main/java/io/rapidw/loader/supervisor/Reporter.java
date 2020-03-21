package io.rapidw.loader.supervisor;

import io.rapidw.loader.common.gen.LoaderServiceOuterClass;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Reporter {

    @Setter
    private int perAgentTotalCount;
    private final GrpcClient grpcClient;
    private final ScheduledExecutorService executorService;
    private ScheduledFuture future;

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

    public void stop() {
        log.debug("reporter stop");
        reportTask();
        this.future.cancel(false);
    }

    public void report(LoaderServiceOuterClass.Report report) {
        log.debug("new report");
        if (perAgentTotalCount > 0) {
            perAgentTotalCount--;
        }
        reports.put(report);
    }

    public void reportTask() {
        if (reports.size() > 0) {
            log.debug("report size: {}", reports.size());
            List<LoaderServiceOuterClass.Report> reportList = new LinkedList<>();
            reports.drainTo(reportList);
            grpcClient.sendReport(reportList);
        }
    }
}
