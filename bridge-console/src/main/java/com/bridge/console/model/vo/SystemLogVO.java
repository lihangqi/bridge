package com.bridge.console.model.vo;

import lombok.Data;


/**
 * @author Jay
 * @version v1.0
 * @description 请添加类描述
 * @date 2019-09-04 16:37
 */
@Data
public class SystemLogVO {


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
    private String logLevelStr;


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
     * 系统名称
     */
    private String appName;


    /**
     * 系统编码
     */
    private String appCode;

}
