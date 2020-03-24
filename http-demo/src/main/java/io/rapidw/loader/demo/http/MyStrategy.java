package io.rapidw.loader.demo.http;

import io.rapidw.loader.api.TestStats;
import io.rapidw.loader.api.TestStrategy;

import java.util.List;

public class MyStrategy implements TestStrategy {

    @Override
    public void init(String strategyParamsBytes) {

    }

    @Override
    public List<byte[]> generateAgentConfig(int agentCount, int perAgentTotal) {
        return null;
    }

    @Override
    public void beforeStart() {

    }

    @Override
    public boolean afterFinish(TestStats testStats) {
        return false;
    }
}
