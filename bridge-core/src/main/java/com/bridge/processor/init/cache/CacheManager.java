package com.bridge.processor.init.cache;

import com.bridge.domain.BridgeConfig;
import com.bridge.zookeeper.data.ConfigKeyNodeData;

/**
 * @author Jay
 * @version v1.0
 * @description 缓存管理
 * @date 2019-01-14 14:49
 */
public class CacheManager {

    private LoadPropertiesCache<BridgeConfig, ConfigKeyNodeData> loadPropertiesCache;

    private BridgeConfig bridgeConfig;

    private static class InstanceHolder {
        private static CacheManager cacheManager = new CacheManager();
    }

    public static CacheManager getInstance() {
        return InstanceHolder.cacheManager;
    }

    private CacheManager() {

    }

    /**
     * 初始化
     *
     * @param loadPropertiesCache
     * @param bridgeConfig
     */
    public CacheManager init(LoadPropertiesCache loadPropertiesCache, BridgeConfig bridgeConfig) {
        this.loadPropertiesCache = loadPropertiesCache;
        this.bridgeConfig = bridgeConfig;
        return this;
    }

    /**
     * 缓存初始化
     */
    public void onCacheInit() {
        loadPropertiesCache.onCacheInit(bridgeConfig);
    }

    /**
     * 刷新缓存
     */
    public void onCacheRefresh(ConfigKeyNodeData configKeyNodeData) {
        loadPropertiesCache.onCacheRefresh(configKeyNodeData);
    }

}
