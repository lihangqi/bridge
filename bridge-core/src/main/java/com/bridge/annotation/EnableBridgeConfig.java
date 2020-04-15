package com.bridge.annotation;

import com.bridge.register.BridgeConfigRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Jay
 * @version v1.0
 * @description 通过注解开启配置中心
 * @date 2019-10-31 15:59
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(BridgeConfigRegistrar.class)
public @interface EnableBridgeConfig {


    /**
     * 前缀
     */
    String PREFIX = "${";

    /**
     * 后缀
     */
    String SUFFIX  = "}";

    /**
     * 服务地址的占位符
     */
    String SPRING_BRIDGE_SERVER_URL = "spring.bridge.server-url";
    String SERVER_URL_PLACEHOLDER = PREFIX + SPRING_BRIDGE_SERVER_URL + SUFFIX ;

    /**
     * 系统编码的占位符
     */
    String SPRING_BRIDGE_APP_CODE = "spring.bridge.app-code";
    String APP_CODE_PLACEHOLDER = PREFIX + SPRING_BRIDGE_APP_CODE + SUFFIX ;

    /**
     * 环境的占位符
     */
    String SPRING_BRIDGE_ENV_ENUM = "spring.bridge.env-enum";
    String ENV_ENUM_PLACEHOLDER = PREFIX + SPRING_BRIDGE_ENV_ENUM + SUFFIX ;


    /**
     * 系统名称的占位符
     */
    String SPRING_BRIDGE_APP_NAME = "spring.bridge.app-name";
    String APP_NAME_PLACEHOLDER = PREFIX + SPRING_BRIDGE_APP_NAME + SUFFIX ;


    /**
     * 全局配置
     *
     * @return
     */
    BridgeConfigProperties bridgeConfigProperties() default @BridgeConfigProperties(
            appCode = APP_CODE_PLACEHOLDER,
            serverUrl = SERVER_URL_PLACEHOLDER,
            envEnum = ENV_ENUM_PLACEHOLDER,
            appName = APP_NAME_PLACEHOLDER
    );


}
