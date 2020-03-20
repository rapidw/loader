package io.rapidw.loader.master.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.rapidw.loader.master.config.ApiController;
import io.rapidw.loader.master.request.TestingConfigRequest;
import io.rapidw.loader.master.service.TestingService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

@ApiController
public class TestingController {
    private final TestingService testingService;
    private final ObjectMapper objectMapper;

    public TestingController(TestingService testingService, ObjectMapper objectMapper) {
        this.testingService = testingService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/start")
    public void start(@Valid @RequestPart("config") TestingConfigRequest testingConfigRequest,
                      @RequestPart("masterOptions") String masterOptions,
                      @RequestPart("agentOptions") String agentOptions,
                      @RequestPart("agentConfig") MultipartFile agentConfig,
                      @RequestPart("agentJar") MultipartFile agentJar) throws IOException {

//        if (testingOuterRequest.getAgent() == null)
//            throw new AppException(AppStatus.BAD_REQUEST, "agent section should not null");
//        if (testingOuterRequest.getStrategy() == null)
//            throw new AppException(AppStatus.BAD_REQUEST, "strategy section should not null");
//
//        testingService.start(testingOuterRequest.getMaster(),
//            objectMapper.writeValueAsBytes(testingOuterRequest.getAgent()),
//            objectMapper.writeValueAsBytes(testingOuterRequest.getStrategy()),
//            jarFile.getBytes());
    }
}
