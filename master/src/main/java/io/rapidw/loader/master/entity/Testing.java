package io.rapidw.loader.master.entity;

import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class Testing {

    private String strategyParams;
    private String agentParams;
    private List<Integer> occupiedSupervisors;

    private int rpsLimit;
    private int durationLimit;
    private int perAgentTotalLimit;

    private byte[] jarBytes;

    private boolean running;
    private Instant startTime;
    private Instant finishTime;

    private int rpsMax;
    private int rpsMin;
    private int rpsAvg;
    private int rpsP99;
    private int responseTimeMax;
    private int responseTimeMin;
    private int responseTimeAvg;
    private int responseTimeP99;

}
