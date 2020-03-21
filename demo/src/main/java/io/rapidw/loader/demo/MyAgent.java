package io.rapidw.loader.demo;

import io.rapidw.loader.api.Agent;
import io.rapidw.loader.api.RoundReporter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class MyAgent implements Agent {

    @Override
    public void config(byte[] agentParamsBytes, byte[] agentConfigBytes) {
        log.info("agent config");
    }

    @Override
    public void roundStart(RoundReporter roundReporter) {
        log.info("agent round start");
        new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                TimeUnit.SECONDS.sleep(1);
                roundReporter.success();
            }
        }).start();
    }

    @Override
    public void stop() {

    }

    @Override
    public void clean() {

    }
}
