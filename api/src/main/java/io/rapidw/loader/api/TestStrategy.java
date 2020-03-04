package io.rapidw.loader.api;

import java.util.List;

public interface TestStrategy {

    /**
     * 1.生成context
     * @param strategyConfigBytes 数据
     */
    void init(byte[] strategyConfigBytes);

    /**
     * 生成agent设置
     * @param agentCount 个数
     * @param perAgentTotal 个数
     * @return Agent Config
     */
    List<byte[]> generateAgentConfig(int agentCount, int perAgentTotal);

    /**
     * 测试开始前
     */
    void beforeStart();

    /**
     * @param testStats 统计结果
     * @return true if will restart at beforeStart()
     */
    boolean afterFinish(TestStats testStats);
}
