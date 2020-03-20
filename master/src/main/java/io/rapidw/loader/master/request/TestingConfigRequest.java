package io.rapidw.loader.master.request;

import lombok.Data;

import java.util.List;

@Data
public class TestingConfigRequest {
    private List<Integer> supervisorsIds;
    // RPS限制
    private Integer throughputLimit;
    // 时长限制
    private Integer durationLimit;
    // 总数限制
    private Integer perAgentTotalLimit;
}
