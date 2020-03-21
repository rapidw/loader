package io.rapidw.loader.supervisor;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "master")
public class MasterConfig {
        private String host;
        private int port;
}
