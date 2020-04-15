package com.bridge.annotation;


import com.bridge.listener.PropertiesChangeListener;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author Jay
 * @version v1.0
 * @description 标识监听的key发生变化的bean, 使用该注解的时候需要同时实现接口{@link PropertiesChangeListener}
 * @date 2019-02-19 14:39
 */
@Component
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BridgeValueChangedListener {

    /**
     * 需要监听的key,如果不传则默认监听所有的key的变化
     *
     * @return
     */
    String[] key() default "";
}
