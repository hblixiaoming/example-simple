package com.example.schedule.controller;

import com.example.schedule.service.JobTaskService;
import com.example.vo.AddJobReqVO;
import com.example.vo.Result;
import com.example.vo.UpdateJobCronReqVO;
import com.example.vo.UpdateJobParamsReqVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JobTaskController {
    @Autowired
    private JobTaskService jobTaskService;

    @RequestMapping(path = "/api/v1/job/addJob", method = RequestMethod.POST)
    public Result<Void> addJob(@Validated @RequestBody AddJobReqVO addJobReqVO,
            BindingResult bindingresult) {
        if (bindingresult.hasErrors()) {
            String msg = bindingresult.getAllErrors().get(0).getDefaultMessage();
            throw new RuntimeException(msg);
        }
        return jobTaskService.addJob(addJobReqVO);
    }

    @RequestMapping(path = "/api/v1/job/updateJobParams", method = RequestMethod.POST)
    public Result<Void> updateJobParams(
            @Validated @RequestBody UpdateJobParamsReqVO job,
            BindingResult bindingresult) {
        if (bindingresult.hasErrors()) {
            String msg = bindingresult.getAllErrors().get(0).getDefaultMessage();
            throw new RuntimeException(msg);
        }
        return jobTaskService.updateJobParams(job);
    }

    @RequestMapping(path = "/api/v1/job/updateJobCron/{id}/{cron}", method = RequestMethod.POST)
    public Result<Void> updateJobCron(@RequestParam(value = "brandId", required = false, defaultValue = "0") Integer brandId,
             @PathVariable Long id,
            @PathVariable String cron) {
        return jobTaskService.updateJobCron(id, cron);
    }

    @RequestMapping(path = "/api/v1/job/updateJobCron", method = RequestMethod.POST)
    public Result<Void> updateJobCron2(
            @Validated @RequestBody UpdateJobCronReqVO job,
            BindingResult bindingresult) {
        if (bindingresult.hasErrors()) {
            String msg = bindingresult.getAllErrors().get(0).getDefaultMessage();
            throw new RuntimeException(msg);
        }
        return jobTaskService.updateJobCron(job.getJobId(), job.getCron());
    }

    @RequestMapping(path = "/api/v1/job/stopJobStatus/{id}", method = RequestMethod.POST)
    public Result<Void> stopJobStatus(@RequestParam(value = "brandId", required = false, defaultValue = "0") Integer brandId,
             @PathVariable Long id) {
        return jobTaskService.StopJobStatus(id);
    }

    @RequestMapping(path = "/api/v1/job/startJobStatus/{id}", method = RequestMethod.POST)
    public Result<Void> startJobStatus(@RequestParam(value = "brandId", required = false, defaultValue = "0") Integer brandId,
            @PathVariable Long id) {
        return jobTaskService.StartJobStatus(id);
    }

    @RequestMapping(path = "/api/v1/job/runOnce/{id}", method = RequestMethod.POST)
    public Result<Void> runOnce(@RequestParam(value = "brandId", required = false, defaultValue = "0") Integer brandId,
            @PathVariable Long id) {
        return jobTaskService.runOnce(id);
    }

}
