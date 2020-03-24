package io.rapidw.loader.api;

import java.time.Instant;

public interface RoundReporter {
    void success();
    void success(Instant startTime);
    void error();
    void timeout();
}
