package com.bridge.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Jay
 * @version v1.0
 * @description 请添加类描述
 * @date 2019-01-17 17:02
 */
@Data
public class ConfigKv implements Serializable {

    private static final long serialVersionUID = 2920099020451877709L;

    /**
     * 键
     */
    private String key;

    /**
     * 值
     */
    private String value;


    /**
     * 版本号
     */
    private String version;
}
