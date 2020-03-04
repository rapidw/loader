package io.rapidw.loader.master.request;

import lombok.Data;

import java.util.Map;

@Data
public class TestingOuterRequest {

    private TestingStartRequest master;
    private Map<String, Object> agent;
    private Map<String, Object> strategy;
}
