package io.rapidw.loader.supervisor;

import io.rapidw.loader.common.gen.LoaderServiceOuterClass;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Reporter {

    private final GrpcClient grpcClient;
    private final ScheduledExecutorService executorService;
    private ScheduledFuture future;

    private LinkedTransferQueue<LoaderServiceOuterClass.Report> reports = new LinkedTransferQueue<>();

    public Reporter(GrpcClient grpcClient, ScheduledExecutorService executorService) {
        this.grpcClient = grpcClient;
        this.executorService = executorService;
    }

    public void start() {
        this.future = this.executorService.scheduleAtFixedRate(this::reportTask, 0, 3, TimeUnit.SECONDS);
    }

    public void stop() {
        this.future.cancel(false);
    }

    public void report(LoaderServiceOuterClass.Report report) {
        reports.put(report);
    }

    public void reportTask() {
        if (reports.size() > 0) {
            List<LoaderServiceOuterClass.Report> reportList = new LinkedList<>();
            reports.drainTo(reportList);
            grpcClient.sendReport(reportList);
        }
    }
}
