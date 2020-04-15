package com.bridge.zookeeper.watcher;

import com.bridge.enums.BridgeErrorEnum;
import com.bridge.enums.EnvEnum;
import com.bridge.enums.NeedUpdateEnum;
import com.bridge.exception.BridgeProcessFailException;
import com.bridge.processor.init.cache.container.BeanDefinitionListenerContainer;
import com.bridge.processor.init.cache.container.LocalCacheHolder;
import com.bridge.zookeeper.BridgeNodeManager;
import com.bridge.zookeeper.data.ConfigKeyNodeData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Jay
 * @version v1.0
 * @description 根据环境订阅配置项
 * @date 2019-03-27 17:47
 */
@Slf4j
public class RegisterNodeTask {


    /**
     * 根据环境订阅检测配置项注册情况，如果失效则补偿注册，确保数据一致性
     *
     * @param envEnum
     */
    public static synchronized void doRegisterTask(EnvEnum envEnum) {
        ConcurrentHashMap<String, ConfigKeyNodeData> map = LocalCacheHolder.getCacheHolder();
        Set<String> configKeyList = BeanDefinitionListenerContainer.getAllConfigKey();
        // 这里检测的标准需要以使用的key为准，只对使用了配置中心提供的注解和占位符的key进行节点注册
        if (!CollectionUtils.isEmpty(map) && !CollectionUtils.isEmpty(configKeyList)) {
            configKeyList.forEach(configKey -> {
                ConfigKeyNodeData configKeyNodeData = map.get(configKey);
                if (configKeyNodeData == null) {
                    throw new BridgeProcessFailException(BridgeErrorEnum.CACHE_KEY_ERROR);
                }
                BridgeNodeManager.getInstance()
                        .registerMachineNode(configKey, configKeyNodeData, NeedUpdateEnum.NOT_NEED_UPDATE.getKey(), envEnum);
            });
        }
        // 消费者订阅有效性检查
        BridgeNodeManager.getInstance().registerConsumerHost();
    }
}
