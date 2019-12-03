package com.example.schedule.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.example.dao.quartz.NewQrtzScheduleJobMapper;
import com.example.dao.quartz.NewQrtzScheduleLogMapper;
import com.example.domain.quartz.NewQrtzScheduleJob;
import com.example.domain.quartz.NewQrtzScheduleJobExample;
import com.example.domain.quartz.NewQrtzScheduleLog;
import com.example.schedule.quartz.QuartzJobFactory;
import com.example.schedule.quartz.QuartzJobFactoryDisallowConcurrentExecution;
import com.example.schedule.service.JobTaskService;
import com.example.schedule.util.DateUtil;
import com.example.schedule.util.SpringUtils;
import com.example.vo.AddJobReqVO;
import com.example.vo.Result;
import com.example.vo.UpdateJobParamsReqVO;
import org.apache.commons.lang.StringUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Service
public class JobTaskServiceImpl implements JobTaskService {
    public final Logger log = LoggerFactory.getLogger(JobTaskService.class);

    private final static String GROUPNAME = "job";
    @Autowired
    private Scheduler scheduler;
    @Autowired
    private NewQrtzScheduleJobMapper newQrtzScheduleJobMapper;
    @Autowired
    private NewQrtzScheduleLogMapper newQrtzScheduleLogMapper;
    @Value("${spring.cloud.config.label}")
    private String env;

    @PostConstruct
    public void init() throws Exception {
        // 这里获取任务信息数据
        NewQrtzScheduleJobExample example = new NewQrtzScheduleJobExample();
        example.createCriteria().andJobStatusEqualTo(NewQrtzScheduleJob.STATUS_RUNNING);
        List<NewQrtzScheduleJob> jobList = newQrtzScheduleJobMapper.selectByExample(example);
        for (NewQrtzScheduleJob job : jobList) {
            if ("production".equals(env)) {
                if (job.getIsRuning() == 1) {
                    job.setIsRuning(0);
                    job.setUpdateTime(DateUtil.getCurrentTime());
                    newQrtzScheduleJobMapper.updateByPrimaryKeySelective(job);
                }
            }
            addJob(job);
        }
    }

    @PreDestroy
    public void destroy() throws Exception {
        String nowdate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        log.error("{}===={}======容器销毁了=======", nowdate, InetAddress.getLocalHost().getHostAddress());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Result<Void> addJob(AddJobReqVO addJobReqVO) {
        Object obj = null;
        try {
            if (StringUtils.isNotBlank(addJobReqVO.getSpringId())) {
                obj = SpringUtils.getBean(addJobReqVO.getSpringId());
            }
        } catch (Exception e) {
            log.error("根据SpringId：{}搜寻目标类错误：{}", addJobReqVO.getSpringId(), e);
        }
        if (obj == null) {
            return Result.fail("未找到目标类" + addJobReqVO.getSpringId());
        }

        Class clazz = obj.getClass();
        Method method = null;
        try {
            method = clazz.getMethod(addJobReqVO.getMethodName(), null);
        } catch (Exception e) {
            log.error("根据SpringId：{}的任务方法{}搜寻目标类方法错误：{}", addJobReqVO.getSpringId(), addJobReqVO.getMethodName(), e);
        }
        if (method == null) {
            return Result.fail("未找到目标方法" + addJobReqVO.getMethodName());
        }

        try {
            CronScheduleBuilder.cronSchedule(addJobReqVO.getCronExpression());
        } catch (Exception e) {
            return Result.fail("cron表达式有误，不能被解析!" + addJobReqVO.getCronExpression());
        }

        if (StringUtils.isNotBlank(addJobReqVO.getExtParams())) {
            try {
                JSON.parseObject(addJobReqVO.getExtParams());
            } catch (Exception e1) {
                return Result.fail("任务参数需要是json格式" + addJobReqVO.getMethodName());
            }
        }
        NewQrtzScheduleJob newQrtzScheduleJob = new NewQrtzScheduleJob();
        BeanUtils.copyProperties(addJobReqVO, newQrtzScheduleJob);
        Date dt = new Date();
        newQrtzScheduleJob.setCreateTime(dt);
        newQrtzScheduleJob.setUpdateTime(dt);
        newQrtzScheduleJobMapper.insert(newQrtzScheduleJob);
        try {
            addJob(newQrtzScheduleJob);
        } catch (SchedulerException e) {
            log.error("{}add job 失败", newQrtzScheduleJob.getMethodName(), e);
            return Result.fail(newQrtzScheduleJob.getMethodName() + "add job 失败" + e.getMessage());
        }
        return Result.SUCCESSED;
    }

    @Override
    public Result<Void> updateJobParams(UpdateJobParamsReqVO job) {

        NewQrtzScheduleJob newQrtzScheduleJob = newQrtzScheduleJobMapper.selectByPrimaryKey(job.getJobId());
        if (newQrtzScheduleJob == null || NewQrtzScheduleJob.STATUS_RUNNING == newQrtzScheduleJob.getJobStatus()) {
            return Result.fail("任务正在运行或不存在，请确保任务存在并停止任务，保存成功后重新启动");
        }
        if (StringUtils.isNotBlank(job.getExtParam())) {
            try {
                JSON.parseObject(job.getExtParam());
            } catch (Exception e1) {
                return Result.fail("参数需要时json格式");
            }
        }
        newQrtzScheduleJob.setExtParams(job.getExtParam());
        NewQrtzScheduleLog log = new NewQrtzScheduleLog();
        log.setMethodName(newQrtzScheduleJob.getMethodName());
        log.setSpringId(newQrtzScheduleJob.getSpringId());
        String extparam_before = newQrtzScheduleJob.getExtParams();
        log.setChangeBefore(extparam_before);
        log.setChangeAfrer(job.getExtParam());
        log.setOperaterDate(new Date());
        log.setOperaterType("修改参数");
        log.setRealName(null);
        log.setUserId(null);
        newQrtzScheduleJobMapper.updateByPrimaryKeySelective(newQrtzScheduleJob);
        insertScheduleLog(log);
        return Result.SUCCESSED;
    }

    @Override
    public Result<Void> updateJobCron(Long id, String cron) {
        try {
            CronScheduleBuilder.cronSchedule(cron);
        } catch (Exception e) {
            return Result.fail("cron表达式有误，不能被解析！");
        }
        NewQrtzScheduleJob newQrtzScheduleJob = newQrtzScheduleJobMapper.selectByPrimaryKey(id);
        String cron_before = newQrtzScheduleJob.getCronExpression();
        newQrtzScheduleJob.setCronExpression(cron);
        if (NewQrtzScheduleJob.STATUS_RUNNING == newQrtzScheduleJob.getJobStatus()) {
            try {
                updateJobCron(newQrtzScheduleJob);
            } catch (SchedulerException e) {
                log.error("{}cron修改失败", newQrtzScheduleJob.getMethodName(), e);
                return Result.fail(newQrtzScheduleJob.getMethodName() + "cron修改失败" + e.getMessage());
            }
        }
        newQrtzScheduleJobMapper.updateByPrimaryKeySelective(newQrtzScheduleJob);
        NewQrtzScheduleLog log = new NewQrtzScheduleLog();
        log.setMethodName(newQrtzScheduleJob.getMethodName());
        log.setSpringId(newQrtzScheduleJob.getSpringId());
        log.setChangeBefore(cron_before);
        log.setChangeAfrer(cron);
        log.setOperaterDate(DateUtil.getCurrentDate());
        log.setOperaterType("修改cron");
        log.setRealName(null);
        log.setUserId(null);
        insertScheduleLog(log);
        return Result.SUCCESSED;
    }

    @Override
    public Result<Void> StopJobStatus(Long id) {
        NewQrtzScheduleJob newQrtzScheduleJob = newQrtzScheduleJobMapper.selectByPrimaryKey(id);
        newQrtzScheduleJob.setJobStatus(NewQrtzScheduleJob.STATUS_NOT_RUNNING);
        try {
            deleteJob(newQrtzScheduleJob);
        } catch (SchedulerException e) {
            log.error("{}停止失败", newQrtzScheduleJob.getMethodName(), e);
            return Result.fail(newQrtzScheduleJob.getMethodName() + "停止失败" + e.getMessage());

        }
        newQrtzScheduleJobMapper.updateByPrimaryKeySelective(newQrtzScheduleJob);
        NewQrtzScheduleLog log = new NewQrtzScheduleLog();
        log.setMethodName(newQrtzScheduleJob.getMethodName());
        log.setSpringId(newQrtzScheduleJob.getSpringId());
        log.setOperaterDate(new Timestamp(new Date().getTime()));
        log.setOperaterType("停止");
        log.setRealName(null);
        log.setUserId(null);
        insertScheduleLog(log);
        return Result.SUCCESSED;
    }

    @Override
    public Result<Void> StartJobStatus(Long id) {
        NewQrtzScheduleJob newQrtzScheduleJob = newQrtzScheduleJobMapper.selectByPrimaryKey(id);
        newQrtzScheduleJob.setJobStatus(NewQrtzScheduleJob.STATUS_RUNNING);
        try {
            addJob(newQrtzScheduleJob);
        } catch (SchedulerException e) {
            log.error("{}启动失败", newQrtzScheduleJob.getMethodName());
            return Result.fail(newQrtzScheduleJob.getMethodName() + "启动失败" + e.getMessage());
        }
        newQrtzScheduleJobMapper.updateByPrimaryKeySelective(newQrtzScheduleJob);
        NewQrtzScheduleLog log = new NewQrtzScheduleLog();
        log.setMethodName(newQrtzScheduleJob.getMethodName());
        log.setSpringId(newQrtzScheduleJob.getSpringId());
        log.setOperaterDate(new Timestamp(new Date().getTime()));
        log.setOperaterType("启动");
        log.setRealName(null);
        log.setUserId(null);
        insertScheduleLog(log);
        return Result.SUCCESSED;
    }

    @Override
    public Result<Void> runOnce(Long id) {
        log.info("runOnce receive request, id: {}", id);
        NewQrtzScheduleJob newQrtzScheduleJob = newQrtzScheduleJobMapper.selectByPrimaryKey(id);
        try {
            if (0 == newQrtzScheduleJob.getJobStatus()) {
                newQrtzScheduleJob.setJobStatus(NewQrtzScheduleJob.STATUS_RUNNING);
                addJob(newQrtzScheduleJob);
                newQrtzScheduleJobMapper.updateByPrimaryKeySelective(newQrtzScheduleJob);
            }
            runAJobNow(newQrtzScheduleJob);
        } catch (SchedulerException e) {
            log.error("{}立即执行失败", newQrtzScheduleJob.getMethodName());
            return Result.fail(newQrtzScheduleJob.getMethodName() + "立即执行失败" + e.getMessage());
        }
        NewQrtzScheduleLog log = new NewQrtzScheduleLog();
        log.setMethodName(newQrtzScheduleJob.getMethodName());
        log.setSpringId(newQrtzScheduleJob.getSpringId());
        log.setOperaterDate(new Timestamp(new Date().getTime()));
        log.setOperaterType("立即执行一次");
        log.setRealName(null);
        log.setUserId(null);
        insertScheduleLog(log);
        return Result.SUCCESSED;
    }

    private void insertScheduleLog(NewQrtzScheduleLog record) {
        try {
            newQrtzScheduleLogMapper.insert(record);
        } catch (Exception e) {
            log.error("insertScheduleLog 异常：param:{},error:{}",
                    JSON.toJSONString(record, SerializerFeature.WriteMapNullValue), e);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addJob(NewQrtzScheduleJob job) throws SchedulerException {

        if (job == null || NewQrtzScheduleJob.STATUS_RUNNING != job.getJobStatus()) {
            return;
        }

        String jobGroup = GROUPNAME + job.getId();
        TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobName(), jobGroup);

        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);

        // 不存在，创建一个
        if (null == trigger) {

            Class clazz = NewQrtzScheduleJob.CONCURRENT_IS == job.getIsConcurrent()
                    ? QuartzJobFactoryDisallowConcurrentExecution.class
                    : QuartzJobFactory.class;
            JobDetail jobDetail = JobBuilder.newJob(clazz).withIdentity(job.getJobName(), jobGroup).build();
            jobDetail.getJobDataMap().put("scheduleJob", job);
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression())
                    .withMisfireHandlingInstructionDoNothing();
            trigger = TriggerBuilder.newTrigger().withIdentity(job.getJobName(), jobGroup).withSchedule(scheduleBuilder)
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);
        } else {
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression())
                    .withMisfireHandlingInstructionDoNothing();
            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
            scheduler.rescheduleJob(triggerKey, trigger);

        }
    }

    /**
     * 更新job时间表达式
     * 
     * @param scheduleJob
     * @throws SchedulerException
     */
    public void updateJobCron(NewQrtzScheduleJob scheduleJob) throws SchedulerException {
        String jobGroup = GROUPNAME + scheduleJob.getId();
        TriggerKey triggerKey = TriggerKey.triggerKey(scheduleJob.getJobName(), jobGroup);
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleJob.getCronExpression())
                .withMisfireHandlingInstructionDoNothing();
        trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
        scheduler.rescheduleJob(triggerKey, trigger);

    }

    /**
     * 立即执行job
     * 
     * @param scheduleJob
     * @throws SchedulerException
     */
    public void runAJobNow(NewQrtzScheduleJob scheduleJob) throws SchedulerException {
        String jobGroup = GROUPNAME + scheduleJob.getId();
        JobKey jobKey = JobKey.jobKey(scheduleJob.getJobName(), jobGroup);
        scheduler.triggerJob(jobKey);
    }

    /**
     * 恢复一个job
     * 
     * @param scheduleJob
     * @throws SchedulerException
     */
    public void resumeJob(NewQrtzScheduleJob scheduleJob) throws SchedulerException {
        String jobGroup = GROUPNAME + scheduleJob.getId();
        JobKey jobKey = JobKey.jobKey(scheduleJob.getJobName(), jobGroup);
        scheduler.resumeJob(jobKey);
    }

    /**
     * 删除一个job
     * 
     * @param scheduleJob
     * @throws SchedulerException
     */
    public void deleteJob(NewQrtzScheduleJob scheduleJob) throws SchedulerException {
        String jobGroup = GROUPNAME + scheduleJob.getId();
        JobKey jobKey = JobKey.jobKey(scheduleJob.getJobName(), jobGroup);
        scheduler.deleteJob(jobKey);
    }

    /**
     * 暂停一个job
     * 
     * @param scheduleJob
     * @throws SchedulerException
     */
    public void pauseJob(NewQrtzScheduleJob scheduleJob) throws SchedulerException {
        String jobGroup = GROUPNAME + scheduleJob.getId();
        JobKey jobKey = JobKey.jobKey(scheduleJob.getJobName(), jobGroup);
        scheduler.pauseJob(jobKey);
    }

}
