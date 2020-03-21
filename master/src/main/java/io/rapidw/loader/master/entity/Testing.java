package io.rapidw.loader.master.entity;

import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class Testing {

    private boolean running;
    private Instant startTime;
    private Instant finishTime;
    private List<Integer> occupiedSupervisors;
    private int rpsMax;
    private int rpsMin;
    private int rpsMean;
    private int rpsP99;
    private int responseTimeMax;
    private int responseTimeMin;
    private int responseTimeMean;
    private int responseTimeP99;
    private int rpsLimit;
    private int durationLimit;
    private int perAgentTotalLimit;
}
