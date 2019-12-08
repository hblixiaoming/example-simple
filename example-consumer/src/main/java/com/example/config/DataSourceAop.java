package com.example.config;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 策略分析数据源切换aop
 */
@Aspect
@Component
public class DataSourceAop {
    private static Logger logger = LoggerFactory.getLogger(DataSourceAop.class);

    @Before("execution(* com.example.dao.order.*.*(..))")
    public void setOrderDateSourceType() {
        DataSourceContextHolder.order();
        logger.info("dataSource切换到：order");
    }

    @Before("execution(* com.example.dao.user.*.*(..))")
    public void setUserDataSourceType() {
        DataSourceContextHolder.user();
        logger.info("dataSource切换到：user");
    }
}
