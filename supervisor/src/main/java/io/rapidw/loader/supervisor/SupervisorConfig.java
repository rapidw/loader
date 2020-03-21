package io.rapidw.loader.supervisor;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "supervisor")
public class SupervisorConfig {
    private String path;
}
