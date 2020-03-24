package io.rapidw.loader.master.entity;

import lombok.Getter;

import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class RpsStat {

    private AtomicInteger totalCount = new AtomicInteger();
    private AtomicInteger successCount = new AtomicInteger();
    private AtomicInteger errorCount = new AtomicInteger();
    private AtomicInteger timeoutCount = new AtomicInteger();

    public void increaseSuccessCount() {
        successCount.incrementAndGet();
        totalCount.incrementAndGet();
    }

    public void increaseErrorCount() {
        errorCount.incrementAndGet();
        totalCount.incrementAndGet();
    }

    public void increaseTimeoutCount() {
        timeoutCount.incrementAndGet();
        totalCount.incrementAndGet();
    }
}
