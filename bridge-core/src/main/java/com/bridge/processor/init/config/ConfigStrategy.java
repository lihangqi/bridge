package com.bridge.processor.init.config;


/**
 * @author Jay
 * @version v1.0
 * @description 这里提供不同的策略去加载
 * @date 2019-01-14 11:13
 */
public interface ConfigStrategy<T> {


    /**
     * 准备配置项读取的条件
     *
     * @param t
     */
    void onConfigPrepared(T t);

}
