package io.rapidw.loader.master.controller;

import io.rapidw.loader.master.config.ApiController;
import io.rapidw.loader.master.response.DataResponse;
import io.rapidw.loader.master.response.ReportStat;
import io.rapidw.loader.master.service.ReportService;
import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@ApiController
public class ReportStatController {

    private final ReportService reportService;

    public ReportStatController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/reports")
    public DataResponse<List<ReportResp>> getReports() {
        Map<Long, ReportStat> dd = new HashMap<>();
        ReportStat stat = new ReportStat();
        stat.increaseSuccessCount();
        dd.put(Instant.now().getEpochSecond(), stat);


        List<ReportResp> reportResps = new LinkedList<>();
        dd.forEach((k,v) -> {
            ReportResp reportResp = new ReportResp();
            reportResp.setTime(k * 1000);
            reportResp.setType(ReportResp.Type.TOTAL);
            reportResp.setCount(v.getTotalCount().get());
            reportResps.add(reportResp);
            reportResp = new ReportResp();
            reportResp.setTime(k * 1000);
            reportResp.setType(ReportResp.Type.SUCCESS);
            reportResp.setCount(v.getSuccessCount().get());
            reportResps.add(reportResp);
            reportResp = new ReportResp();
            reportResp.setTime(k * 1000);
            reportResp.setType(ReportResp.Type.ERROR);
            reportResp.setCount(v.getErrorCount().get());
            reportResps.add(reportResp);
            reportResp = new ReportResp();
            reportResp.setTime(k * 1000);
            reportResp.setType(ReportResp.Type.TIMEOUT);
            reportResp.setCount(v.getTimeoutCount().get());
            reportResps.add(reportResp);
        });
        return DataResponse.of(reportResps);
    }

    @Data
    public static class ReportResp {
        public enum Type {TOTAL, SUCCESS, ERROR, TIMEOUT}
        private long time;
        private Type type;
        private int count;
    }

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:m:s").withZone(ZoneId.systemDefault());
    private static String format(Long time) {
        Instant instant = Instant.ofEpochSecond(time);
        return formatter.format(instant);
    }
}
