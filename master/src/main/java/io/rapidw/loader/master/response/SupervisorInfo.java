package io.rapidw.loader.master.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SupervisorInfo {

    private int id;
    private String host;
    private int port;
    private String path;
}
