package com.example.vo;


import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class AddJobReqVO implements Serializable {

    private static final long serialVersionUID = -2157258941156105963L;
    @NotBlank(message = "任务名称不能为空")
    private String jobName;
    @NotBlank(message = "spring bean id 必填")
    private String springId;
    @NotNull(message = "任务状态 必填")
    private Integer jobStatus;
    @NotBlank(message = "cron 表达式必填")
    private String cronExpression;
    @NotBlank(message = "执行方法名表达式必填")
    private String methodName;
    @NotNull(message = "是否同步 必填")
    private Integer isConcurrent;
    private String extParams;
    private String description;

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getSpringId() {
        return springId;
    }

    public void setSpringId(String springId) {
        this.springId = springId;
    }

    public Integer getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(Integer jobStatus) {
        this.jobStatus = jobStatus;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Integer getIsConcurrent() {
        return isConcurrent;
    }

    public void setIsConcurrent(Integer isConcurrent) {
        this.isConcurrent = isConcurrent;
    }

    public String getExtParams() {
        return extParams;
    }

    public void setExtParams(String extParams) {
        this.extParams = extParams;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
