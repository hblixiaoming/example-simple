package com.example.config;

/**
 * 数据源切换
 */
public class DataSourceContextHolder {

    private static final ThreadLocal<String> local = new ThreadLocal<>();

    private DataSourceContextHolder() {

    }

    public static ThreadLocal<String> getLocal() {
        return local;
    }

    public static void order() {
        local.set(String.valueOf(DataSourceType.order.getType()));
    }


    public static void user() {
        local.set(String.valueOf(DataSourceType.user.getType()));
    }

    public static void quartz() {
        local.set(String.valueOf(DataSourceType.quartz.getType()));
    }


    public static String getJdbcType() {
        return local.get();
    }
}
