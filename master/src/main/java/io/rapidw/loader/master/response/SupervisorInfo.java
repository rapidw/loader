package io.rapidw.loader.master.response;

import io.rapidw.loader.master.entity.Supervisor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SupervisorInfo {

    private int id;
    private String host;
    private String path;
    private Supervisor.Status status;
}
