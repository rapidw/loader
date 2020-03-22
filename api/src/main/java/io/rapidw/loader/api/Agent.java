package io.rapidw.loader.api;

public interface Agent {

    /**
     *
     * @param agentParamsBytes 用户指定的配置，每个agent是相同的
     * @param agentConfigBytes TestStrategy生成的配置，每个agent是不同的
     */
    void config(byte[] agentParamsBytes, byte[] agentConfigBytes);

    /**
     * start a new round, this method must not be blocked
     * @param roundReporter reporter
     */
    void roundStart(RoundReporter roundReporter);

    void stop(StopCallback stopCallback);

    /**
     * 重新初始化
     */
    void clean();
}
