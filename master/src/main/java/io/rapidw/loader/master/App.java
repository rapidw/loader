package io.rapidw.loader.master;

import io.rapidw.loader.master.config.AppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppConfig.class)
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
