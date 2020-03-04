package io.rapidw.loader.api;

public interface RoundReporter {
    void success();
    void error();
    void timeout();
}
