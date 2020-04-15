package com.bridge.listener;

import com.bridge.processor.RefreshBeanField;
import com.bridge.domain.BeanDefinition;
import com.bridge.processor.init.cache.container.BeanDefinitionListenerContainer;
import com.bridge.zookeeper.data.ConfigKeyNodeData;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author Jay
 * @version v1.0
 * @description 当值发生变化的监听的默认实现
 * @date 2019-02-25 10:53
 */
public class DefaultPropertiesChangeListenerImpl implements PropertiesChangeListener {

    /**
     * 当值发生变化的监听
     *
     * @param configKeyNodeData
     */
    @Override
    public void onPropertiesChanged(ConfigKeyNodeData configKeyNodeData) {
        if (configKeyNodeData == null || StringUtils.isEmpty(configKeyNodeData.getKey())) {
            return;
        }
        List<BeanDefinition> beanDefinitionList = BeanDefinitionListenerContainer.getBeanDefinition(configKeyNodeData.getKey());
        if (CollectionUtils.isEmpty(beanDefinitionList)) {
            return;
        }
        beanDefinitionList.forEach(beanDefinition -> {
                    if (beanDefinition != null && beanDefinition.isAutoRefreshed()) {
                        RefreshBeanField.refresh(beanDefinition, configKeyNodeData.getValue());
                    }
                }
        );

    }


    /**
     * 当值发生变化前
     *
     * @param configKeyNodeData
     */
    @Override
    public void onBeforePropertiesChanged(ConfigKeyNodeData configKeyNodeData) {

    }
}
