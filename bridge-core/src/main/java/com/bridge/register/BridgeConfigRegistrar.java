package com.bridge.register;

import com.bridge.annotation.EnableBridgeConfig;
import com.bridge.utils.PropUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Properties;

import static com.bridge.domain.Constants.BRIDGE_CONFIG_PROPERTIES_BEAN_NAME;
import static com.bridge.utils.BridgeBeanUtils.*;
import static org.springframework.core.annotation.AnnotationAttributes.fromMap;

/**
 * @author Jay
 * @version v1.0
 * @description 注册一些bean  {@link com.bridge.annotation.EnableBridgeConfig}
 * @date 2019-10-31 16:02
 */
public class BridgeConfigRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    private Environment environment;


    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes attributes = fromMap(metadata.getAnnotationAttributes(EnableBridgeConfig.class.getName()));
        if (attributes != null) {
            // 避免重复注册，以第一个扫描到的注解为准
            if (((DefaultListableBeanFactory) registry).containsBean(BRIDGE_CONFIG_PROPERTIES_BEAN_NAME)) {
                return;
            }
            AnnotationAttributes bridgeConfigProperties = attributes.getAnnotation(BRIDGE_CONFIG_PROPERTIES_BEAN_NAME);
            Properties properties = PropUtils.resolveByPropertyResolver(bridgeConfigProperties, environment);
            PropUtils.checkProperties(properties);
            // 这里是将全局的配置注册成一个单例的bean
            registerSingleton(registry, BRIDGE_CONFIG_PROPERTIES_BEAN_NAME, properties);
        }
        registerBridgeConfigBeanPostProcessor(registry);
        registerPropertySourcesPlaceholderConfigurer(registry);
        registerDefaultConfigBeanPostProcess(registry);
    }


}
