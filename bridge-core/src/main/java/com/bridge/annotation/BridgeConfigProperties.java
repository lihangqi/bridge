package com.bridge.annotation;


import java.lang.annotation.*;

/**
 * @author Jay
 * @version v1.0
 * @description 请添加类描述
 * @date 2019-10-31 16:24
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BridgeConfigProperties {


    /**
     * 服务地址
     */
    String SERVER_URL = "serverUrl";

    /**
     * 系统编码
     */
    String APP_CODE = "appCode";

    /**
     * 环境
     */
    String ENV_ENUM = "envEnum";


    /**
     * 系统名称
     */
    String APP_NAME = "appName";


    /**
     * 系统编码
     *
     * @return
     */
    String appCode();


    /**
     * 服务地址
     *
     * @return
     */
    String serverUrl();


    /**
     * 环境
     *
     * @return
     */
    String envEnum();


    /**
     * 系统名称
     *
     * @return
     */
    String appName();
}
