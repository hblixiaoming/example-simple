package com.example;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
@EnableDiscoveryClient
public class ExampleConfigApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ExampleConfigApplication.class).web(true).run(args);
    }
}
