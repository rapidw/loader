package io.rapidw.loader.master.service;

import io.rapidw.loader.common.gen.LoaderServiceOuterClass;
import io.rapidw.loader.master.response.ReportStat;
import lombok.Getter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class ReportService {

    private ConcurrentLinkedQueue<LoaderServiceOuterClass.Report> reports = new ConcurrentLinkedQueue<>();
    @Getter
    private ConcurrentHashMap<Long, ReportStat> stats;


    public void addReport(LoaderServiceOuterClass.Report report) {
        reports.add(report);
    }


    @Scheduled(fixedRate = 1000)
    public void stat() {
        if (!reports.isEmpty()) {
            LoaderServiceOuterClass.Report temp = reports.poll();
            if (temp != null) {
                ReportStat aa = new ReportStat();
                ReportStat tempStat = stats.putIfAbsent(temp.getStartTime().getSeconds(), aa);
                if (tempStat == null) {
                    tempStat = aa;
                }
                switch (temp.getStatus()) {
                    case SUCCESS:
                        tempStat.increaseSuccessCount();
                        break;
                    case ERROR:
                        tempStat.increaseErrorCount();
                        break;
                    case TIMEOUT:
                        tempStat.increaseTimeoutCount();
                        break;
                }
            }
        }
    }
}
