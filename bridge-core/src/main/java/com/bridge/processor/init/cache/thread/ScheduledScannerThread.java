package com.bridge.processor.init.cache.thread;

import com.bridge.domain.BridgeConfig;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Jay
 * @version v1.0
 * @description 开启一个线程用于动态读取配置文件
 * @date 2019-01-15 18:35
 */
@Deprecated
@Slf4j
public class ScheduledScannerThread extends Thread {


    private volatile boolean isEnable = true;


    private BridgeConfig bridgeConfig;


    /**
     * 读取文件间隔时间 10s
     */
    private static final Long SCAN_TIME = 10000L;

    private static class InstanceHolder {
        private static ScheduledScannerThread scheduledScannerThread = new ScheduledScannerThread();
    }

    public static ScheduledScannerThread getInstance() {
        return InstanceHolder.scheduledScannerThread;
    }

    private ScheduledScannerThread() {

    }

    /**
     * 启动线程，这里设置为是守护线程，在主线程停止时，自动停止
     *
     * @param bridgeConfig
     */
    public void runThread(BridgeConfig bridgeConfig) {
        this.bridgeConfig = bridgeConfig;
        this.setDaemon(true);
        this.start();
    }

    @Override
    public void run() {
        while (isEnable) {
            try {
                Thread.sleep(SCAN_TIME);
                // TODO
                log.debug(">>>>>>>>>>>>> 正在检测配置项是否需要更新 ...");
            } catch (InterruptedException e) {
                log.error("读取配置文件的线程出现了一个异常，现在停止，异常原因为:", e);
                this.destroyThread();
            }
        }
    }

    /**
     * 关闭线程
     */
    public void destroyThread() {
        if (isEnable) {
            isEnable = false;
            try {
                this.interrupt();
            } catch (Exception e) {
            }
        }
    }
}
