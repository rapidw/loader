package io.rapidw.loader.master.controller;

import io.rapidw.loader.master.config.ApiController;
import io.rapidw.loader.master.response.BaseResponse;
import io.rapidw.loader.master.response.DataResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

@ApiController
@Slf4j
public class TestController {
    private List<Data> dataList = new ArrayList<>();

    @GetMapping("/test")
    public DataResponse<List<Data>> test() {
        Data data;
        switch (dataList.size()) {
            case 0:
                data = new Data();
                data.setMonth("Jan");
                data.setCity("Tokyo");
                data.setTemperature(RandomUtils.nextInt(0,40));
                dataList.add(data);
                data = new Data();
                data.setMonth("Jan");
                data.setCity("London");
                data.setTemperature(RandomUtils.nextInt(0,40));
                dataList.add(data);
                break;
            case 2:
                data = new Data();
                data.setMonth("Feb");
                data.setCity("Tokyo");
                data.setTemperature(RandomUtils.nextInt(0,40));
                dataList.add(data);
                data = new Data();
                data.setMonth("Feb");
                data.setCity("London");
                data.setTemperature(RandomUtils.nextInt(0,40));
                dataList.add(data);
                break;
            case 4:
                data = new Data();
                data.setMonth("Mar");
                data.setCity("Tokyo");
                data.setTemperature(RandomUtils.nextInt(0,40));
                dataList.add(data);
                data = new Data();
                data.setMonth("Mar");
                data.setCity("London");
                data.setTemperature(RandomUtils.nextInt(0,40));
                dataList.add(data);
                break;
            case 6:
                data = new Data();
                data.setMonth("Apr");
                data.setCity("Tokyo");
                data.setTemperature(RandomUtils.nextInt(0,40));
                dataList.add(data);
                data = new Data();
                data.setMonth("Apr");
                data.setCity("London");
                data.setTemperature(RandomUtils.nextInt(0,40));
                dataList.add(data);
                break;
        }
        return DataResponse.of(dataList);
    }

    @lombok.Data
    private static class Data {
        private String month;
        private String city;
        private int temperature;
    }

    @GetMapping("/log")
    public void logff() {
        log.info("dadsagdadf");
    }


    @PostMapping("/rrr")
    public BaseResponse get(ddd d) {
        return BaseResponse.SUCCESS;
    }

    @lombok.Data
    public static class ddd {
        private long a;
    }
}
