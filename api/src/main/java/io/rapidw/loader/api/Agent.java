package io.rapidw.loader.api;

public interface Agent {

    /**
     *
     * @param agentParamsBytes 用户指定的配置，每个agent是相同的
     * @param agentConfigBytes TestStrategy生成的配置，每个agent是不同的
     */
    void config(byte[] agentParamsBytes, byte[] agentConfigBytes);

    /**
     * 开始一轮测试，此方法不能被阻塞
     * @param roundReporter 汇报器
     */
    void roundStart(RoundReporter roundReporter);

    /**
     * 停止所有测试
     */
    void stop();

    /**
     * 重新初始化
     */
    void clean();
}
