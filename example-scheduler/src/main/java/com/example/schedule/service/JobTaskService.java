package com.example.schedule.service;

import com.example.vo.AddJobReqVO;
import com.example.vo.Result;
import com.example.vo.UpdateJobParamsReqVO;

public interface JobTaskService {

    Result<Void> addJob(AddJobReqVO addJobReqVO);

    Result<Void> updateJobParams(UpdateJobParamsReqVO job);

    Result<Void> updateJobCron(Long id, String cron);

    Result<Void> StopJobStatus(Long id);

    Result<Void> StartJobStatus(Long id);

    Result<Void> runOnce(Long id);
}
