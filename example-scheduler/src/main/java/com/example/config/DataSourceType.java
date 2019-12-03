package com.example.config;

/**
 * 数据源类型枚举
 * */
public enum DataSourceType {
    order("order", "订单库"),

    user("user", "用户库"),

    quartz("quartz", "任务库");

    private String type;
    private String name;

    DataSourceType(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

}
