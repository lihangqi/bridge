package com.bridge.schedule;

import com.bridge.domain.BridgeConfig;
import com.bridge.processor.init.BridgeEnvironment;
import com.bridge.zookeeper.watcher.RegisterNodeTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Jay
 * @version v1.0
 * @description 自动尝试重连控制台和zk
 * @date 2019-11-14 13:17
 */
@Slf4j
public class AutoReconnectZookeeperSchedule {

    /**
     * 延迟时间
     */
    private static final Integer INITIAL_DELAY = 1;


    /**
     * 执行周期
     */
    private static final Integer PERIOD = 1;


    /**
     * 创建一个周期性执行任务的线程池，设置为守护线程，核心池大小为1
     */
    private static ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1,
            new BasicThreadFactory.Builder().namingPattern("bridge-reconnect-schedule-pool-%d").daemon(true).build());


    /**
     * 自动尝试重连控制台、zk,并进行差异对比
     *
     * @param bridgeConfig
     */
    public static void startReconnectSchedule(BridgeConfig bridgeConfig) {
        executorService.scheduleAtFixedRate(() -> {
                    try {
                        BridgeEnvironment.useZookeeperInit(bridgeConfig);
                        RegisterNodeTask.doRegisterTask(bridgeConfig.getEnvEnum());
                        executorService.shutdown();
                        // 这里只要成功连接上了就不需要再执行这个任务了
                        log.info("成功连接到控制台，切换为配置中心模式... ");
                    } catch (Exception e) {
                        log.warn("连接控制台失败，等待下次重试，异常信息为：", e);
                    }
                },
                INITIAL_DELAY, PERIOD, TimeUnit.MINUTES);

    }


    /**
     * 关闭线程池
     */
    public static void shutdownReconnectSchedule() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }

}
