package com.example;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@ServletComponentScan
@SpringCloudApplication
@EnableTransactionManagement
@EnableFeignClients
public class ExampleSchedulerApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ExampleSchedulerApplication.class).web(true).run(args);
    }
}
