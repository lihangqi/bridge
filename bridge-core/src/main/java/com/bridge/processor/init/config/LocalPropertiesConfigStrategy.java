package com.bridge.processor.init.config;

import com.bridge.domain.BridgeConfig;
import com.bridge.download.DownLoadService;
import com.bridge.processor.BridgePropertySource;
import com.bridge.processor.init.cache.container.LocalCacheHolder;
import com.bridge.zookeeper.data.ConfigKeyNodeData;

import java.util.List;

/**
 * @author Jay
 * @version v1.0
 * @description 请添加类描述
 * @date 2019-11-13 14:04
 */
public class LocalPropertiesConfigStrategy implements ConfigStrategy<BridgeConfig> {


    /**
     * 加载本地配置项
     *
     * @param bridgeConfig
     */
    @Override
    public void onConfigPrepared(BridgeConfig bridgeConfig) {
        List<ConfigKeyNodeData> configKeyNodeDataList = DownLoadService.loadProperties(bridgeConfig);
        // 写入数据至缓存池中
        LocalCacheHolder.setConfigKeyData2Cache(configKeyNodeDataList, false);
        // 构建Properties用于原生的@Value注解或xml占位符${}的配置项源
        BridgePropertySource.getInstance().buildProperties(LocalCacheHolder.getCacheHolder());
    }
}
