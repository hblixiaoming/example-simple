package com.example.vo;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class UpdateJobCronReqVO implements Serializable {
    private static final long serialVersionUID = 4036109948744233699L;
    @NotNull(message = "任务id不能为空")
    private Long jobId;
    @NotBlank(message = "cron 表达式")
    private String cron;

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

}
