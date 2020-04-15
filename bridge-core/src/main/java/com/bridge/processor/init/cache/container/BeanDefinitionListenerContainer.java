package com.bridge.processor.init.cache.container;

import com.bridge.domain.BeanDefinition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Jay
 * @version v1.0
 * @description 需要被监听的bean
 * @date 2018-12-27 15:21
 */
@Slf4j
public class BeanDefinitionListenerContainer {


    /**
     * 持有被监听的bean的容器
     */
    private static Map<String, List<BeanDefinition>> listenerBeanContainer = new ConcurrentHashMap<>();


    /**
     * 放入监听的容器内
     *
     * @param key
     * @param beanContainer
     * @return false表示存在，不加入；true表示不存在，加入
     */
    public static boolean putToListenerBeanContainer(String key, BeanDefinition beanContainer) {
        List<BeanDefinition> beanContainerList = listenerBeanContainer.get(key);
        if (CollectionUtils.isEmpty(beanContainerList)) {
            beanContainerList = new ArrayList<>();
            beanContainerList.add(beanContainer);
            listenerBeanContainer.put(key, beanContainerList);
        } else {
            // 存在相同的就不重复加入了
            for (BeanDefinition item : beanContainerList) {
                if (item.getBeanName().equals(beanContainer.getBeanName())
                        && item.getPropertyName().equals(beanContainer.getPropertyName())) {
                    return false;
                }
            }
            beanContainerList.add(beanContainer);
        }
        return true;
    }


    /**
     * key是否在本系统中使用
     *
     * @param configKey
     * @return
     */
    public static boolean isConfigKeyInContainer(String configKey) {
        return listenerBeanContainer.containsKey(configKey);
    }

    /**
     * 获取使用了key的bean
     *
     * @param configKey
     * @return
     */
    public static List<BeanDefinition> getBeanDefinition(String configKey) {
        return listenerBeanContainer.get(configKey);
    }


    /**
     * 获取所有的key
     *
     * @return
     */
    public static Set<String> getAllConfigKey() {
        return listenerBeanContainer.keySet();
    }
}
