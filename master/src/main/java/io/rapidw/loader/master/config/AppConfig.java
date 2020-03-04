package io.rapidw.loader.master.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "master")
@Data
public class AppConfig {

    private GrpcServer grpcServer;

    @Data
    public static class GrpcServer {
        private int port;
    }
}
