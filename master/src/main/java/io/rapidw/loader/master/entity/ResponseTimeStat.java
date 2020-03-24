package io.rapidw.loader.master.entity;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResponseTimeStat {

    @Getter
    private long min;
    @Getter
    private long max;
    @Getter
    private double avg;
    private int count;
    private double total;
    private volatile boolean firstRun = true;

    public void add(long value) {
        log.info("new responsetime value: {}", value);
        if (firstRun) {
            max = value;
            min = value;
            firstRun = false;
        }
        if (value > max) {
            max = value;
        }
        if (value < min) {
            min = value;
        }

        this.count++;
        this.total += value;
        this.avg = this.total / this.count;
        log.info("count {} total {} min {} max {} avg {}", this.count, this.total, this.min, this.max, this.avg);
    }
}
