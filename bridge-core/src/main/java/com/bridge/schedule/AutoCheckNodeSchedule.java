package com.bridge.schedule;

import com.bridge.enums.EnvEnum;
import com.bridge.log.LogHandler;
import com.bridge.zookeeper.watcher.RegisterNodeTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;


import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Jay
 * @version v1.0
 * @description 使用线程池周期性的检查临时节点的订阅情况, 默认为每个小时执行一次
 * @date 2019-03-27 17:50
 */
@Slf4j
public class AutoCheckNodeSchedule {

    /**
     * 延迟时间
     */
    private static final Integer INITIAL_DELAY = 10;


    /**
     * 执行周期
     */
    private static final Integer PERIOD = 50;


    /**
     * 创建一个周期性执行任务的线程池，设置为守护线程，核心池大小为1
     */
    private static ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1,
            new BasicThreadFactory.Builder().namingPattern("bridge-schedule-pool-%d").daemon(true).build());


    /**
     * 执行检查临时节点的订阅情况的任务,服务启动10分钟后开始执行，
     * 以后每次延迟INITIAL_DELAY分钟间隔PERIOD分钟执行一次
     *
     * @param envEnum
     */
    public static void startAutoCheckNodeSchedule(EnvEnum envEnum) {
        executorService.scheduleAtFixedRate(() -> {
                    try {
                        RegisterNodeTask.doRegisterTask(envEnum);
                    } catch (Exception e) {
                        log.warn("执行检查临时节点的订阅情况的任务执行失败，等待下次重试，异常信息为：", e);
                    }
                }
                , INITIAL_DELAY, PERIOD, TimeUnit.MINUTES);
        LogHandler.info("开启服务订阅情况检测的任务，该任务将在服务启动10分钟后开始第一次的执行，以后每次间隔50分钟执行一次 ...");
    }

    /**
     * 关闭线程池
     */
    public static void shutdownCheckNodeSchedule() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
