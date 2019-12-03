package com.example.schedule.quartz;

import com.example.domain.quartz.NewQrtzScheduleJob;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 计划任务执行处 无状态
 * 
 */
public class QuartzJobFactory implements Job {

    Logger logger = LoggerFactory.getLogger(QuartzJobFactory.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        NewQrtzScheduleJob scheduleJob = (NewQrtzScheduleJob) context.getMergedJobDataMap().get("scheduleJob");
        TaskUtils.invokMethod(scheduleJob);
    }
}