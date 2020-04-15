package com.bridge.utils;

import com.bridge.enums.BridgeErrorEnum;
import com.bridge.exception.BridgeProcessFailException;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.support.AopUtils;

import java.lang.reflect.Field;

/**
 * @author Jay
 * @version v1.0
 * @description 代理相关的工具类
 * @date 2019-11-21 14:00
 */
public class ProxyUtils {


    /**
     * 获取 目标对象
     *
     * @param proxy
     * @return
     */
    public static Object getTarget(Object proxy) {
        try {
            if (!AopUtils.isAopProxy(proxy)) {
                return proxy;
            }
            if (AopUtils.isJdkDynamicProxy(proxy)) {
                proxy = getJdkDynamicProxyTargetObject(proxy);
            }
            if (AopUtils.isCglibProxy(proxy)) {
                proxy = getCglibProxyTargetObject(proxy);
            }
        } catch (Exception e) {
            throw new BridgeProcessFailException(BridgeErrorEnum.PROXY_PARSE_ERROR);
        }
        return getTarget(proxy);
    }


    /**
     * cglib动态代理的对象
     *
     * @param proxy
     * @return
     * @throws Exception
     */
    private static Object getCglibProxyTargetObject(Object proxy) throws Exception {
        Field field = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
        field.setAccessible(true);
        Object dynamicAdvisedInterceptor = field.get(proxy);
        Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised");
        advised.setAccessible(true);
        return ((AdvisedSupport) advised.get(dynamicAdvisedInterceptor)).getTargetSource().getTarget();
    }


    /**
     * jdk动态代理的对象
     *
     * @param proxy
     * @return
     * @throws Exception
     */
    private static Object getJdkDynamicProxyTargetObject(Object proxy) throws Exception {
        Field field = proxy.getClass().getSuperclass().getDeclaredField("h");
        field.setAccessible(true);
        AopProxy aopProxy = (AopProxy) field.get(proxy);
        Field advised = aopProxy.getClass().getDeclaredField("advised");
        advised.setAccessible(true);
        return ((AdvisedSupport) advised.get(aopProxy)).getTargetSource().getTarget();
    }

}
