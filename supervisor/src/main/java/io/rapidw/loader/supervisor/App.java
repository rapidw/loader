package io.rapidw.loader.supervisor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({SupervisorConfig.class, MasterConfig.class})
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
