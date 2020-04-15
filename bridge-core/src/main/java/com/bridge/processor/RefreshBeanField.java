package com.bridge.processor;


import com.bridge.domain.BeanDefinition;
import com.bridge.enums.BridgeErrorEnum;
import com.bridge.exception.BridgeProcessFailException;
import com.bridge.log.LogHandler;
import com.bridge.utils.PropUtils;
import com.bridge.utils.ProxyUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Modifier;

import static com.bridge.domain.Constants.LOAD_FORMAT;
import static com.bridge.domain.Constants.PRINT_LOG_FORMAT;

/**
 * @author Jay
 * @version v1.0
 * @description 重载属性值
 * @date 2018-12-27 15:51
 */
@Slf4j
public class RefreshBeanField {

    private RefreshBeanField() {
        throw new BridgeProcessFailException(BridgeErrorEnum.NOT_INSTANTIATION_ERROR);
    }

    private static BeanFactory thisBeanFactory;


    public static void setBeanFactory(BeanFactory beanFactory) {
        thisBeanFactory = beanFactory;
    }

    /**
     * 通过反射刷新bean的属性
     *
     * @param beanDefinition
     * @param value
     */
    public static void refresh(BeanDefinition beanDefinition, final String value) {
        Object bean = thisBeanFactory.getBean(beanDefinition.getBeanName());
        if (bean == null) {
            return;
        }
        String beanName = beanDefinition.getBeanName();
        String propertyName = beanDefinition.getPropertyName();
        String key = beanDefinition.getKey();
        // 这里需要处理被aop代理的情况
        Object targetBean = ProxyUtils.getTarget(bean);
        ReflectionUtils.doWithFields(targetBean.getClass(), field -> {
            if (beanDefinition.getPropertyName().equals(field.getName())) {
                try {
                    // 静态属性无法注入
                    if (Modifier.isStatic(field.getModifiers())) {
                        throw new BridgeProcessFailException(BridgeErrorEnum.STATIC_INJECT_ERROR);
                    }
                    Object valueAfterConvert = PropUtils.convertValue(field.getType(), value);
                    field.setAccessible(Boolean.TRUE);
                    field.set(targetBean, valueAfterConvert);
                    log.info(PRINT_LOG_FORMAT, beanName, propertyName, key, value);
                } catch (Exception e) {
                    throw new BridgeProcessFailException(BridgeErrorEnum.UNKNOWN_ERROR.getCode(), e.getMessage());
                }
                // 发送日志至控制台
                LogHandler.info(String.format(LOAD_FORMAT, beanName, propertyName, key, value));
            }
        });

    }
}
