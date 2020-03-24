package io.rapidw.loader.master.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.rapidw.loader.master.config.ApiController;
import io.rapidw.loader.master.entity.Testing;
import io.rapidw.loader.master.request.TestingConfigRequest;
import io.rapidw.loader.master.response.BaseResponse;
import io.rapidw.loader.master.response.DataResponse;
import io.rapidw.loader.master.service.TestingService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

@ApiController
@Validated
public class TestingController {
    private final TestingService testingService;
    private final ObjectMapper objectMapper;

    public TestingController(TestingService testingService, ObjectMapper objectMapper) {
        this.testingService = testingService;
        this.objectMapper = objectMapper;
    }

    @PostMapping(value = "/testing")
    public BaseResponse start(@Valid @RequestPart("config") TestingConfigRequest testingConfigRequest,
                              @RequestPart(name = "strategyParams", required = false) String strategyParams,
                              @RequestPart(name = "agentParams", required = false) String agentParams,
                              @RequestPart("jar") MultipartFile jar) throws IOException {

        testingService.start(testingConfigRequest, strategyParams, agentParams, jar.getBytes());
        return BaseResponse.SUCCESS;
    }

    @GetMapping("/testing")
    public DataResponse<Testing> get() {
        return DataResponse.of(testingService.get());
    }
}
