package com.bridge.utils;

import com.bridge.processor.BridgeConfigBeanPostProcessor;
import com.bridge.processor.DefaultConfigBeanPostProcess;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * @author Jay
 * @version v1.0
 * @description bean的注册
 * @date 2019-10-30 18:24
 */
public class BridgeBeanUtils {

    public static final String PLACEHOLDER_CONFIGURER_BEAN_NAME = "propertySourcesPlaceholderConfigurer";

    /**
     * 注册 {@link BridgeConfigBeanPostProcessor}
     *
     * @param registry
     */
    public static void registerBridgeConfigBeanPostProcessor(BeanDefinitionRegistry registry) {
        registerBean(registry, BridgeConfigBeanPostProcessor.BEAN_NAME, BridgeConfigBeanPostProcessor.class);
    }

    /**
     * 注册 {@link DefaultConfigBeanPostProcess}
     *
     * @param registry
     */
    public static void registerDefaultConfigBeanPostProcess(BeanDefinitionRegistry registry) {
        registerBean(registry, DefaultConfigBeanPostProcess.BEAN_NAME, DefaultConfigBeanPostProcess.class);
    }


    /**
     * 注册 {@link PropertySourcesPlaceholderConfigurer}
     *
     * @param registry
     */
    public static void registerPropertySourcesPlaceholderConfigurer(BeanDefinitionRegistry registry) {
        registerBean(registry, PLACEHOLDER_CONFIGURER_BEAN_NAME, PropertySourcesPlaceholderConfigurer.class);
    }


    /**
     * 注册bean，如果不存在的话
     *
     * @param registry
     * @param beanName
     * @param beanClass
     */
    public static void registerBean(BeanDefinitionRegistry registry, String beanName, Class<?> beanClass) {
        if (!registry.containsBeanDefinition(beanName)) {
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(beanClass);
            beanDefinitionBuilder.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
            registry.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());
        }
    }


    /**
     * 注册单例的bean
     *
     * @param registry
     * @param beanName
     * @param singletonObject
     */
    public static void registerSingleton(BeanDefinitionRegistry registry, String beanName, Object singletonObject) {
        SingletonBeanRegistry beanRegistry = null;
        if (registry instanceof SingletonBeanRegistry) {
            beanRegistry = (SingletonBeanRegistry) registry;
        } else if (registry instanceof AbstractApplicationContext) {
            beanRegistry = ((AbstractApplicationContext) registry).getBeanFactory();
        }
        if (beanRegistry != null) {
            if (!beanRegistry.containsSingleton(beanName)) {
                beanRegistry.registerSingleton(beanName, singletonObject);
            }
        }
    }

}
