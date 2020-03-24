package io.rapidw.loader.demo.http;

import io.rapidw.loader.api.Agent;
import io.rapidw.loader.api.RoundReporter;
import io.rapidw.loader.api.StopCallback;
import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.*;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.asynchttpclient.Dsl.asyncHttpClient;

@Slf4j
public class MyAgent implements Agent {
    private AsyncHttpClient asyncHttpClient;
    private AtomicInteger requestCount = new AtomicInteger();

    @Override
    public void config(byte[] agentParamsBytes, byte[] agentConfigBytes) {


        asyncHttpClient = asyncHttpClient();
    }

    @Override
    public void roundStart(RoundReporter roundReporter) {
        int i = requestCount.incrementAndGet();
        log.info("------------- agent request count: {}", i);
        asyncHttpClient.prepareGet("http://localhost:10080/ok").execute(new AsyncCompletionHandler<String>() {
            @Override
            public String onCompleted(Response response) throws Exception {
                log.info("------------- agent request count resp: {}", i);
                roundReporter.success();
                return "ok";
            }

            @Override
            public void onThrowable(Throwable t) {
                log.error("-------------------------------agent error", t);
                if (t instanceof TimeoutException) {
                    roundReporter.timeout();
                } else {
                    roundReporter.error();
                }
            }
        });

    }

    @Override
    public void stop(StopCallback stopCallback) {

    }

    @Override
    public void clean() {

    }
}
