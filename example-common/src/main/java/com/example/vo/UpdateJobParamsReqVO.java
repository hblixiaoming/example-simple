package com.example.vo;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class UpdateJobParamsReqVO implements Serializable {
    private static final long serialVersionUID = 9002492076281816953L;
    @NotNull(message = "任务id不能为空")
    private Long jobId;
    private String extParam;

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getExtParam() {
        return extParam;
    }

    public void setExtParam(String extParam) {
        this.extParam = extParam;
    }

}
