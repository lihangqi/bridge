package com.bridge.console.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Jay
 * @version v1.0
 * @description 请添加类描述
 * @date 2019-01-17 17:02
 */
@Data
public class ConfigKvVO{

    /**
     * 键
     */
    private String key;

    /**
     * 值
     */
    private String value;

}
