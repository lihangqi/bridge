package com.bridge.console.model.vo;

import lombok.Data;

/**
 * @author Jay
 * @version v1.0
 * @description app列表
 * @date 2019-01-28 14:49
 */
@Data
public class ConfigAppVO {


    /**
     * 应用id
     */
    private Integer appId;


    /**
     * 应用名称
     */
    private String appName;


    /**
     * 系统编码
     */
    private String appCode;

}
