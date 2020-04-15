package com.bridge.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Jay
 * @version v1.0
 * @description k/v和bean的定义
 * @date 2019-02-14 16:29
 */
@Data
public class BeanDefinition implements Serializable {

    private static final long serialVersionUID = -6986505072127495872L;

    /**
     * bean的名称
     */
    private String beanName;

    /**
     * bean的属性名称
     */
    private String propertyName;

    /**
     * bean的属性名称对应的配置文件的key
     */
    private String key;

    /**
     * 是否需要自动更新，对应的注解
     */
    private boolean autoRefreshed;

    /**
     * 构造方法
     *
     * @param beanName
     * @param propertyName
     * @param key
     * @param autoRefreshed
     */
    public BeanDefinition(String beanName, String propertyName, String key, boolean autoRefreshed) {
        this.beanName = beanName;
        this.propertyName = propertyName;
        this.key = key;
        this.autoRefreshed = autoRefreshed;
    }
}
