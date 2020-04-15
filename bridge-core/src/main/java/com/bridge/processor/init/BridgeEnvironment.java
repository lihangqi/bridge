package com.bridge.processor.init;

import com.bridge.log.LogHandler;
import com.bridge.processor.init.cache.CacheManager;
import com.bridge.processor.init.cache.LoadPropertiesFromServer;
import com.bridge.domain.BridgeConfig;
import com.bridge.listener.PropertiesChangeListenerHolder;
import com.bridge.processor.init.cache.container.LocalCacheHolder;
import com.bridge.processor.init.config.ConfigManager;
import com.bridge.processor.init.config.LocalPropertiesConfigStrategy;
import com.bridge.processor.init.config.ZookeeperConfigStrategy;
import com.bridge.schedule.AutoCheckNodeSchedule;
import com.bridge.schedule.AutoReconnectZookeeperSchedule;
import com.bridge.zookeeper.ZookeeperClient;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Jay
 * @version v1.0
 * @description 环境的控制
 * @date 2019-01-14 11:18
 */
@Slf4j
public class BridgeEnvironment {


    private static ZookeeperConfigStrategy zookeeperConfigStrategy = new ZookeeperConfigStrategy();


    private static LoadPropertiesFromServer loadPropertiesFromServer = new LoadPropertiesFromServer();


    private static LocalPropertiesConfigStrategy localPropertiesConfigStrategy = new LocalPropertiesConfigStrategy();


    public static boolean CAN_REGISTER_ZK_NODE = true;


    /**
     * 初始化
     *
     * @param bridgeConfig
     */
    public static void init(BridgeConfig bridgeConfig) {
        // 初始化日志配置
        LogHandler.init(bridgeConfig);
        try {
            // 通过zk初始化本地缓存池
            useZookeeperInit(bridgeConfig);
        } catch (Exception e) {
            log.info("Failed to start service using configuration center mode, now switch to local mode...");
            // 通过本地初始化
            CAN_REGISTER_ZK_NODE = false;
            useLocalFileInit(bridgeConfig);
            // 激活重试任务，一旦连接控制台成功，就进行本地差异对比，重新刷新配置项
            AutoReconnectZookeeperSchedule.startReconnectSchedule(bridgeConfig);
        }

    }


    /**
     * 使用zk初始化
     *
     * @param bridgeConfig
     */
    public static void useZookeeperInit(BridgeConfig bridgeConfig) {
        ConfigManager.getInstance().init(zookeeperConfigStrategy, bridgeConfig).onConfigPrepared();
        CacheManager.getInstance().init(loadPropertiesFromServer, bridgeConfig).onCacheInit();
    }


    /**
     * 使用本地文件初始化
     *
     * @param bridgeConfig
     */
    public static void useLocalFileInit(BridgeConfig bridgeConfig) {
        ConfigManager.getInstance().init(localPropertiesConfigStrategy, bridgeConfig).onConfigPrepared();
    }


    /**
     * 关闭资源
     */
    public static void destroy() {
        // 关闭zk连接
        ZookeeperClient.getInstance().closeZooKeeperConnection();
        // 清空本地缓存
        LocalCacheHolder.clearLocalCacheHolder();
        // 清空监听器
        PropertiesChangeListenerHolder.clearPropertiesChangeListener();
        // 关闭重连控制台和zk任务
        AutoReconnectZookeeperSchedule.shutdownReconnectSchedule();
        // 关闭检查临时节点的订阅情况任务
        AutoCheckNodeSchedule.shutdownCheckNodeSchedule();
    }


}
