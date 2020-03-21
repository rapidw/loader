package io.rapidw.loader.master.request;

import io.rapidw.loader.master.utils.validation.AtLeastOneNotNull;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@AtLeastOneNotNull(fieldNames = {"rpsLimit", "durationLimit", "perAgentTotalLimit"})
public class TestingConfigRequest {
    @NotNull
    @Size(min = 1)
    private List<Integer> supervisorIds;
    private int rpsLimit;
    private int durationLimit;
    private int perAgentTotalLimit;
}
