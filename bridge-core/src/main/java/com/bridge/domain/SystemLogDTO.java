package com.bridge.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Jay
 * @version v1.0
 * @description 日志
 * @date 2019-08-30 15:31
 */
@Data
public class SystemLogDTO implements Serializable {

    private static final long serialVersionUID = 5689621719732978497L;

    /**
     * 环境类型
     */
    private Integer envId;

    /**
     * 日志时间
     */
    private String logRecordTime;


    /**
     * 日志级别{@link com.bridge.enums.LogLevelEnum}
     */
    private Integer logLevel;


    /**
     * 日志内容
     */
    private String logContent;


    /**
     * 客户端ip
     */
    private String ip;


    /**
     * 系统编码
     */
    private String appCode;

}
