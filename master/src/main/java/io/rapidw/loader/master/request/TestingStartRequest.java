package io.rapidw.loader.master.request;

import lombok.Data;

@Data
public class TestingStartRequest {
    private Integer agentCount;

    // RPS限制
    private Integer rpsLimit;
    // 时长限制
    private Integer durationLimit;
    // 总数限制
    private Integer perAgentTotalLimit;
}
