package io.rapidw.loader.master.controller;

import io.rapidw.loader.master.config.ApiController;
import io.rapidw.loader.master.exception.AppException;
import io.rapidw.loader.master.exception.AppStatus;
import io.rapidw.loader.master.request.SupervisorDeployRequest;
import io.rapidw.loader.master.request.SupervisorRemoveRequest;
import io.rapidw.loader.master.response.BaseResponse;
import io.rapidw.loader.master.response.PagedResponse;
import io.rapidw.loader.master.response.SupervisorInfo;
import io.rapidw.loader.master.service.SupervisorService;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@ApiController
public class SupervisorController {

    private final SupervisorService supervisorService;

    public SupervisorController(SupervisorService supervisorService) {
        this.supervisorService = supervisorService;
    }

    @PostMapping("/supervisors")
    public BaseResponse deploySupervisor(@Valid @RequestBody SupervisorDeployRequest supervisorDeployRequest) {
        if (!InetAddressValidator.getInstance().isValid(supervisorDeployRequest.getHost())) {
            throw new AppException(AppStatus.BAD_REQUEST, "invalid ip address");
        }
        supervisorService.deploySupervisor(supervisorDeployRequest);
        return BaseResponse.SUCCESS;
    }

    @GetMapping("/supervisors")
    public PagedResponse<SupervisorInfo> getSupervisors() {
        return PagedResponse.of(supervisorService.getAllSupervisors());
    }

    @DeleteMapping("/supervisors/{supervisor_id}")
    public BaseResponse delete(@PathVariable("supervisor_id") int id) {
        supervisorService.removeSupervisor(id);
        return BaseResponse.SUCCESS;
    }

    @DeleteMapping("/supervisors")
    public BaseResponse deleteAll(@Valid @RequestBody SupervisorRemoveRequest supervisorRemoveRequest) {
        supervisorRemoveRequest.getIds().forEach(supervisorService::removeSupervisor);
        return BaseResponse.SUCCESS;
    }
}
