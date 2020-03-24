package io.rapidw.loader.master.service;

import io.rapidw.loader.common.gen.LoaderServiceOuterClass;
import io.rapidw.loader.master.entity.ResponseTimeStat;
import io.rapidw.loader.master.entity.RpsStat;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static io.rapidw.loader.common.gen.LoaderServiceOuterClass.Report.Status.SUCCESS;

@Service
@Slf4j
public class ReportService {

    private ConcurrentLinkedQueue<LoaderServiceOuterClass.Report> reports = new ConcurrentLinkedQueue<>();
    @Getter
    private ConcurrentHashMap<Long, RpsStat> rpsStats = new ConcurrentHashMap<>();
    @Getter
    private ConcurrentHashMap<Long, ResponseTimeStat> responseTimeStats = new ConcurrentHashMap<>();

    private DescriptiveStatistics statistics;

    public void addReport(LoaderServiceOuterClass.Report report) {
        reports.add(report);
    }


    @Scheduled(fixedRate = 1000)
    public void stat() {
        if (!reports.isEmpty()) {
            log.info("processing reports");
            LoaderServiceOuterClass.Report report = this.reports.poll();
            if (report != null) {
                RpsStat tempRpsStat = rpsStats.computeIfAbsent(report.getStartTime().getSeconds(), (key) -> new RpsStat());
                switch (report.getStatus()) {
                    case SUCCESS:
                        tempRpsStat.increaseSuccessCount();
                        break;
                    case ERROR:
                        tempRpsStat.increaseErrorCount();
                        break;
                    case TIMEOUT:
                        tempRpsStat.increaseTimeoutCount();
                        break;
                }

                ResponseTimeStat responseTimeStat = responseTimeStats.computeIfAbsent(report.getStartTime().getSeconds(), (key) -> new ResponseTimeStat());
                if (report.getStatus() == SUCCESS) {
                    long seconds =  report.getFinishTime().getSeconds() - report.getStartTime().getSeconds();
                    long nanos = report.getFinishTime().getNanos() - report.getStartTime().getNanos();
                    responseTimeStat.add(seconds *  1000 + nanos / 1000000);
                }
            }
        }
    }
}
