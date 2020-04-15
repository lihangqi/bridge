package com.bridge.processor.init.cache;


/**
 * @author Jay
 * @version v1.0
 * @description 缓存策略
 * @date 2019-01-14 14:36
 */
public interface LoadPropertiesCache<T, D> {


    /**
     * 缓存初始化
     *
     * @param t
     */
    void onCacheInit(T t);


    /**
     * 缓存刷新
     *
     * @param d
     */
    void onCacheRefresh(D d);
}
