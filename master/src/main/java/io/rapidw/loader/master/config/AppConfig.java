package io.rapidw.loader.master.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "master")
@Data
public class AppConfig {

    private Jre jre;
    private GrpcServer grpcServer;

    @Data
    public static class GrpcServer {
        private String host;
        private int port;
    }


    @Data
    public static class Jre {
        private String filePath;
        private String folderName;
    }
}
