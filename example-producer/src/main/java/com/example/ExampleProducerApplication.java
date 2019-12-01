package com.example;

import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.OAuth2AutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@EnableAutoConfiguration(exclude = { SecurityAutoConfiguration.class, OAuth2AutoConfiguration.class,
        MybatisAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@EnableAsync
@ServletComponentScan
@SpringCloudApplication
@EnableTransactionManagement
@EnableFeignClients
public class ExampleProducerApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ExampleProducerApplication.class).web(true).run(args);
    }
}
