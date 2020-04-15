package com.bridge.processor.init.config;

import com.bridge.domain.BridgeConfig;

/**
 * @author Jay
 * @version v1.0
 * @description 根据不同的策略去初始化配置
 * @date 2019-01-14 16:45
 */
public class ConfigManager {

    private ConfigStrategy<BridgeConfig> configStrategy;

    private BridgeConfig bridgeConfig;

    private static class InstanceHolder {
        private static ConfigManager configManager = new ConfigManager();
    }

    public static ConfigManager getInstance() {
        return InstanceHolder.configManager;
    }

    private ConfigManager() {

    }

    /**
     * 初始化
     *
     * @param configStrategy
     * @param bridgeConfig
     */
    public ConfigManager init(ConfigStrategy<BridgeConfig> configStrategy, BridgeConfig bridgeConfig) {
        this.configStrategy = configStrategy;
        this.bridgeConfig = bridgeConfig;
        return this;
    }

    /**
     * 根据不同的策略去初始化
     */
    public void onConfigPrepared() {
        configStrategy.onConfigPrepared(bridgeConfig);
    }

}
