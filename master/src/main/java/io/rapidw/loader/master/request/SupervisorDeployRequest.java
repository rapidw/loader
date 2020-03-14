package io.rapidw.loader.master.request;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class SupervisorDeployRequest {

    @Length(min = 1)
    @NotNull
    private String host;
    @Max(65535)
    @Min(1)
    private int port;
    @Length(min = 1)
    @NotNull
    private String username;
    @Length(min = 1)
    @NotNull
    private String password;
    private String path;
}
