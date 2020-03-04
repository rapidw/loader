package io.rapidw.loader.supervisor;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "supervisor")
public class AppConfig {

    private GrpcServer grpcServer;

    /**
     * 获取kvm可用的cpu核数量，从而合理设置线程池数量
     */
    public static int availableProcessors = Runtime.getRuntime().availableProcessors();

    @Data
    public static class GrpcServer {
        private String host;
        private int port;
    }
}
