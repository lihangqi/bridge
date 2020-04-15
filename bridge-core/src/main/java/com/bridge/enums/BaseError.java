package com.bridge.enums;

/**
 * @description: 错误提示对象接口
 * @author: Jay
 * @date: 2018-03-28 14:22
 * @version: V1.0
 */
public interface BaseError {

    /**
     * 错误码
     * @return 返回错误码
     */
    int getCode();

    /**
     * 返回错误信息
     * @return 错误信息
     */
    String getMessage();

}
