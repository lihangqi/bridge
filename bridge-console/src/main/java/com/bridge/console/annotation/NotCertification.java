package com.bridge.console.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Jay
 * @version v1.0
 * @description 当使用该注解时，表示并不需要进行登陆认证
 * @date 2018-03-07 20:38
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface NotCertification {

}
