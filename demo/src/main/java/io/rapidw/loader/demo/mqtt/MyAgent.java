package io.rapidw.loader.demo.mqtt;

import io.rapidw.loader.api.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
public class MyAgent implements Agent {
    private volatile boolean running;
    private BlockingQueue<Task> taskQueue;
    private Queue<Report> reportQueue;
    private StopCallback stopCallback;

    @Override
    public void config(byte[] agentParamsBytes, byte[] agentConfigBytes) {
        log.info("agent config");
    }

//    @Override
//    @SneakyThrows
//    public void start(RoundReporter roundReporter) {
//        this.running = true;
//        while (running) {
//            Task task = this.taskQueue.take();
//            if (task.getType() == Task.Type.STOP) {
//                log.info("agent stop received");
//                this.running = false;
//            }
//            if (task.getType() == Task.Type.RUN) {
//                log.info("agent round start");
//                new Thread(() -> {
//                    Instant startTime = Instant.now();
//                    TimeUnit.SECONDS.sleep(1);
//                    log.info("agent success");
//                    reportQueue.add(
//                        Report.builder()
//                            .startTime(startTime)
//                            .finishTime(Instant.now())
//                            .type(Report.Type.SUCCESS)
//                            .build()
//                    );
//                    reportQueue.add(
//                        Report.builder()
//                            .type(Report.Type.FINISH)
//                            .build()
//                    );
//                });
//            }
//        }
//    }

    @Override
    public void roundStart(RoundReporter roundReporter) {
        log.info("agent round start");
        new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                TimeUnit.SECONDS.sleep(1);
                log.info("agent success");
                roundReporter.success();
                if (stopCallback != null) {
                    log.info("agent call stop callback");
                    stopCallback.stopped();
                }
            }
        }).start();
    }

    @Override
    public void stop(StopCallback stopCallback) {
        log.info("agent stop received");
    }

    @Override
    public void clean() {

    }
}
