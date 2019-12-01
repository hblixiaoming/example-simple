package com.example;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringCloudApplication
@EnableTransactionManagement
@EnableFeignClients
public class ExampleConsumerApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ExampleConsumerApplication.class).web(true).run(args);
    }
}
