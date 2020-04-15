package com.bridge.annotation;

import java.lang.annotation.*;

/**
 * @author Jay
 * @version v1.0
 * @description 使用该注解配置值后，可以动态配置,需要注意的是该注解只能作用在spring的bean内
 * @date 2018-12-26 14:59
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BridgeValue {

    /**
     * value
     *
     * @return
     */
    String value();

    /**
     * 是否自动刷新配置项
     *
     * @return
     */
    boolean autoRefreshed() default false;

}
