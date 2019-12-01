package com.example;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class ExampleEurekaApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ExampleEurekaApplication.class).web(true).run(args);
    }
}
