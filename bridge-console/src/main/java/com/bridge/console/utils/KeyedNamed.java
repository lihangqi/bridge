package com.bridge.console.utils;


/**
 * @description: 用于枚举key-value映射描述，方便统一。
 * 多用于enum 的枚举类实现这个接口，并且个enum是非异常枚举
 * @author: Jay
 * @date: 2019-01-23 14:35
 * @version: V1.0
 */
public interface KeyedNamed{
    /**
     * key
     *
     * @return 返回KEY
     */
    int getKey();

    /**
     * 描述 、名称
     *
     * @return 描述 、名称
     */
    String getName();
}
