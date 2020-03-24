package io.rapidw.loader.master.controller;

import io.rapidw.loader.master.config.ApiController;
import io.rapidw.loader.master.response.DataResponse;
import io.rapidw.loader.master.response.LineResponse;
import io.rapidw.loader.master.service.ReportService;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

@ApiController
public class ReportController {

    private final ReportService reportService;

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
    private static String format(Long time) {
        Instant instant = Instant.ofEpochSecond(time);
        return formatter.format(instant);
    }

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/reports/rps")
    public DataResponse<List<LineResponse>> getRpsReports() {
        List<LineResponse> lineResponses = new LinkedList<>();
        reportService.getRpsStats().forEach((k,v) -> {
            LineResponse response = new LineResponse();
            response.setTime(format(k));
            response.setType(LineResponse.Type.TOTAL);
            response.setValue(v.getTotalCount().get());
            lineResponses.add(response);
            response = new LineResponse();
            response.setTime(format(k));
            response.setType(LineResponse.Type.SUCCESS);
            response.setValue(v.getSuccessCount().get());
            lineResponses.add(response);
            response = new LineResponse();
            response.setTime(format(k));
            response.setType(LineResponse.Type.ERROR);
            response.setValue(v.getErrorCount().get());
            lineResponses.add(response);
            response = new LineResponse();
            response.setTime(format(k));
            response.setType(LineResponse.Type.TIMEOUT);
            response.setValue(v.getTimeoutCount().get());
            lineResponses.add(response);
        });
        return DataResponse.of(lineResponses);
    }

    @GetMapping("/reports/responseTime")
    public DataResponse<List<LineResponse>> getResponseTime() {
        List<LineResponse> responses = new LinkedList<>();
        reportService.getResponseTimeStats().forEach((k, v) -> {
            LineResponse response = new LineResponse();
            response.setTime(format(k));
            response.setType(LineResponse.Type.MIN);
            response.setValue(v.getMin());
            responses.add(response);

            response = new LineResponse();
            response.setTime(format(k));
            response.setType(LineResponse.Type.MAX);
            response.setValue(v.getMax());
            responses.add(response);

            response = new LineResponse();
            response.setTime(format(k));
            response.setType(LineResponse.Type.AVG);
            response.setValue(v.getAvg());
            responses.add(response);
        });
        return DataResponse.of(responses);
    }
}
