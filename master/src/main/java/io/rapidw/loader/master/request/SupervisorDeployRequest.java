package io.rapidw.loader.master.request;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
public class SupervisorDeployRequest {

    private String host;
    @Max(65535)
    @Min(1)
    private int port;
    private String username;
    private String password;
    private String path;
}
