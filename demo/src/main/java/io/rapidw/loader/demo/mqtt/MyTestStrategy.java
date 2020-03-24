package io.rapidw.loader.demo.mqtt;

import io.rapidw.loader.api.TestStats;
import io.rapidw.loader.api.TestStrategy;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;

@Slf4j
public class MyTestStrategy implements TestStrategy {
    @Override
    public void init(String strategyParamsBytes) {

        log.info("init");
    }

    @Override
    public List<byte[]> generateAgentConfig(int agentCount, int perAgentTotal) {
        return Collections.singletonList(new byte[] {0});
    }

    @Override
    public void beforeStart() {

    }

    @Override
    public boolean afterFinish(TestStats testStats) {
        return false;
    }
}
