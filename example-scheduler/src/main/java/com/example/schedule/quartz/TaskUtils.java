package com.example.schedule.quartz;

import com.alibaba.fastjson.JSONObject;
import com.example.dao.quartz.NewQrtzScheduleJobMapper;
import com.example.domain.quartz.NewQrtzScheduleJob;
import com.example.util.DateUtil;
import com.example.util.SpringUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

/**
 * 任务工具类
 *
 * */
public class TaskUtils {

    public final static Logger log = LoggerFactory.getLogger(TaskUtils.class); // NOSONAR

    private TaskUtils() {

    }

    /**
     * 通过反射调用scheduleJob中定义的方法
     * 
     * @param scheduleJob
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void invokMethod(NewQrtzScheduleJob scheduleJob) {
        MDC.put("tracker_id", UUID.randomUUID().toString());
        Object object = null;
        if (StringUtils.isNotBlank(scheduleJob.getSpringId())) {
            object = SpringUtils.getBean(scheduleJob.getSpringId());
        }
        if (object == null) {
            log.error("任务:{}未启动成功，请检查是否配置正确！！！", scheduleJob.getJobName());
            return;
        }
        if (object instanceof JobContent && null != scheduleJob.getExtParams()) {
            try {
                JSONObject jsonObject = JSONObject.parseObject(scheduleJob.getExtParams());
                ((JobContent) object).setExtJosnObject(jsonObject);
            } catch (Exception e) {
                log.error("任务:{}参数绑定错误，请检查是否配置正确！！！,error", scheduleJob.getJobName(), e);
                return;
            }
        }
        Class clazz = object.getClass();
        Method method = null;
        try {
            method = clazz.getDeclaredMethod(scheduleJob.getMethodName());
        } catch (NoSuchMethodException e) {
            log.error("任务:{}未启动成功，请检查是否配置正确！！！,error:", scheduleJob.getJobName(), e);
            return;
        } catch (SecurityException e) {
            log.error("方法代理错误SecurityException,error:", e);
            return;
        }
        if (method != null) {
            NewQrtzScheduleJobMapper newQrtzScheduleJobMapper = SpringUtils.getBean("newQrtzScheduleJobMapper");
            NewQrtzScheduleJob record = newQrtzScheduleJobMapper.selectByPrimaryKey(scheduleJob.getId());
            if (null != record.getIsRuning() && record.getIsRuning() == 1) {
                log.error("有一个实例已经在运行这个定时任务,不再再次运行，jobName={}", record.getJobName());
                return;
            }
            try {
                record.setIsRuning(1);
                try {
                    record.setRunServerIp(InetAddress.getLocalHost().toString());
                } catch (UnknownHostException e) {
                    log.error("获取服务器ip异常");
                }
                newQrtzScheduleJobMapper.updateByPrimaryKeySelective(record);
                log.info("{}--- 任务:[{}]-[{}]开始执行", DateUtil.getCurrentTimeStr(), scheduleJob.getMethodName(),
                        scheduleJob.getDescription());
                long start = System.currentTimeMillis();
                MDC.put("tracker_id", record.getSpringId() + "-" + UUID.randomUUID());
                method.invoke(object);
                long end = System.currentTimeMillis();
                record.setIsRuning(0);
                record.setRunTime(end - start);
                record.setUpdateTime(DateUtil.getCurrentTime());
                newQrtzScheduleJobMapper.updateByPrimaryKeySelective(record);
            } catch (IllegalAccessException e) {
                log.error("方法代理错误IllegalAccessException,error:", e);
                record.setIsRuning(0);
                record.setUpdateTime(DateUtil.getCurrentTime());
                newQrtzScheduleJobMapper.updateByPrimaryKeySelective(record);
                return;
            } catch (IllegalArgumentException e) {
                log.error("方法代理错误IllegalArgumentException,error:", e);
                record.setIsRuning(0);
                record.setUpdateTime(DateUtil.getCurrentTime());
                newQrtzScheduleJobMapper.updateByPrimaryKeySelective(record);
                return;
            } catch (Throwable e) {
                log.error("方法代理错误InvocationTargetException,error:", e);
                record.setIsRuning(0);
                record.setUpdateTime(DateUtil.getCurrentTime());
                newQrtzScheduleJobMapper.updateByPrimaryKeySelective(record);
                return;
            }
        }
        log.info("{}--- 任务:[{}]-[{}]执行成功", DateUtil.getCurrentTimeStr(), scheduleJob.getMethodName(),
                scheduleJob.getDescription());
    }

    public static void main(String[] args) throws IOException {
        InetAddress inetAddress;// 声明InetAddress对象
        try {
            inetAddress = InetAddress.getLocalHost();// 实例化InetAddress对象，返回本地主机
            String hostName = inetAddress.getHostName();// 获取本地主机名
            String canonicalHostName = inetAddress.getCanonicalHostName();// 获取此
                                                                          // IP地址的完全限定域名
            byte[] address = inetAddress.getAddress();// 获取原始IP地址
            int a = 0;
            if (address[3] < 0) {
                a = address[3] + 256;
            }
            String hostAddress = inetAddress.getHostAddress();// 获取本地主机的IP地址
            boolean reachable = inetAddress.isReachable(2000);// 获取布尔类型，看是否能到达此IP地址
            System.out.println(inetAddress.toString());
            System.out.println("主机名为：" + hostName);// 输出本地主机名
            System.out.println("此IP地址的完全限定域名：" + canonicalHostName);// 输出此IP地址的完全限定域名
            System.out.println("原始IP地址为：" + address[0] + "." + address[1] + "." + address[2] + "." + a);// 输出本地主机的原始IP地址
            System.out.println("IP地址为：" + hostAddress);// 输出本地主机的IP地址
            System.out.println("是否能到达此IP地址：" + reachable);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
