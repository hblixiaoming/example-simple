package com.example.schedule.quartz;

import com.example.domain.quartz.NewQrtzScheduleJob;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 若一个方法一次执行不完下次轮转时则等待改方法执行完后才执行下一次操作
 * 
 * @author zengsongbin
 */
@DisallowConcurrentExecution
public class QuartzJobFactoryDisallowConcurrentExecution implements Job {

    Logger logger = LoggerFactory.getLogger(QuartzJobFactoryDisallowConcurrentExecution.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        NewQrtzScheduleJob scheduleJob = (NewQrtzScheduleJob) context.getMergedJobDataMap().get("scheduleJob");
        TaskUtils.invokMethod(scheduleJob);
    }
}