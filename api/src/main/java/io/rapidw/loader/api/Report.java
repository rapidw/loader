package io.rapidw.loader.api;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class Report {
    public enum Type {
        SUCCESS,
        ERROR,
        TIMEOUT,
        FINISH
    }

    private Instant startTime;
    private Instant finishTime;
    private Type type;
}
