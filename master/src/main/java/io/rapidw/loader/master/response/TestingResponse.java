package io.rapidw.loader.master.response;

import java.time.Instant;
import java.util.List;

public class TestingResponse {
    private String strategyParams;
    private String agentParams;
    private List<Integer> occupiedSupervisors;

    private int rpsLimit;
    private int durationLimit;
    private int perAgentTotalLimit;

    private boolean running;
    private Instant startTime;
    private Instant finishTime;

    private int rpsMax;
    private int rpsMin;
    private int rpsMean;
    private int rpsP99;
    private int responseTimeMax;
    private int responseTimeMin;
    private int responseTimeMean;
    private int responseTimeP99;
}
