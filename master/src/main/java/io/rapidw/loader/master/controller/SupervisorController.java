package io.rapidw.loader.master.controller;

import io.rapidw.loader.master.config.ApiController;
import io.rapidw.loader.master.request.SupervisorDeployRequest;
import io.rapidw.loader.master.response.BaseResponse;
import io.rapidw.loader.master.service.SupervisorService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@ApiController
public class SupervisorController {

    private final SupervisorService supervisorService;

    public SupervisorController(SupervisorService supervisorService) {
        this.supervisorService = supervisorService;
    }

    @PostMapping("/supervisors")
    public BaseResponse deploySupervisor(@Valid @RequestBody SupervisorDeployRequest supervisorDeployRequest) {
        supervisorService.deploySupervisor(supervisorDeployRequest);
        return BaseResponse.SUCCESS;
    }
}
