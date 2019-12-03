package com.example.config;

import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis扫描接口
 * <p/>
 * MyBatis 版本过低，采用@MapperScan的方式，可能会导致SqlSessionFactoryBean初始化失败，需要强制sudo运行,
 * 改用此方式可指定setSqlSessionFactoryBeanName
 */
@Configuration
@AutoConfigureAfter(SpringDataSourceConfig.class)
public class MyBatisMapperScannerConfig {

    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
        mapperScannerConfigurer.setBasePackage(
                "com.example.dao.*");
        return mapperScannerConfigurer;
    }
}