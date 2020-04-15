package com.bridge.processor;


import com.bridge.zookeeper.data.ConfigKeyNodeData;
import lombok.Getter;
import org.springframework.util.CollectionUtils;

import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Jay
 * @version v1.0
 * @description 构建Properties用于原生的@Value注解或xml占位符${}的配置项源
 * @date 2019-10-30 11:29
 */
public class BridgePropertySource {

    @Getter
    private Properties properties;

    private BridgePropertySource() {

    }

    public static BridgePropertySource getInstance() {
        return InstanceHolder.bridgePropertySource;
    }

    private static class InstanceHolder {
        private static BridgePropertySource bridgePropertySource = new BridgePropertySource();
    }

    /**
     * 构建Properties用于原生的@Value注解或xml占位符${}的配置项源
     *
     * @param concurrentHashMap
     * @return
     */
    public void buildProperties(ConcurrentHashMap<String, ConfigKeyNodeData> concurrentHashMap) {
        if (CollectionUtils.isEmpty(concurrentHashMap)) {
            return;
        }
        Properties properties = new Properties();
        concurrentHashMap.forEach(((s, configKeyNodeData) ->
                properties.put(configKeyNodeData.getKey(), configKeyNodeData.getValue())
        ));
        this.properties = properties;
    }
}
