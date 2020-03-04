package io.rapidw.loader.demo;

import io.rapidw.loader.api.TestStats;
import io.rapidw.loader.api.TestStrategy;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class MyTestStrategy implements TestStrategy {
    @Override
    public void init(byte[] strategyConfigBytes) {

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
