package com.bridge.console.model.entity;

import lombok.Data;

/**
 * @author Jay
 * @version v1.0
 * @description 系统日志表
 * @date 2019-01-29 11:50
 */
@Data
public class SystemLogDO {
    /**
     * <pre>
     * 
     * 表字段 : br_sys_log.id
     * </pre>
     * 
     */
    private Integer id;

    /**
     * <pre>
     * 0:未删除，1：已删除
     * 表字段 : br_sys_log.is_deleted
     * </pre>
     * 
     */
    private Integer isDeleted;

    /**
     * <pre>
     * 所属环境 0:开发环境 1：测试环境 2:预发布环境 3:生产环境 4:所有环境
     * 表字段 : br_sys_log.env_id
     * </pre>
     * 
     */
    private Integer envId;

    /**
     * <pre>
     * 日志时间
     * 表字段 : br_sys_log.log_record_time
     * </pre>
     * 
     */
    private String logRecordTime;

    /**
     * <pre>
     * 日志级别 0:DEBUG 1:INFO 2:WARN 3:ERROR
     * 表字段 : br_sys_log.log_level
     * </pre>
     * 
     */
    private Integer logLevel;

    /**
     * <pre>
     * 系统ip
     * 表字段 : br_sys_log.client_ip
     * </pre>
     * 
     */
    private String clientIp;

    /**
     * <pre>
     * 系统编码
     * 表字段 : br_sys_log.app_code
     * </pre>
     * 
     */
    private String appCode;

    /**
     * <pre>
     * 系统名称
     * 表字段 : br_sys_log.app_name
     * </pre>
     * 
     */
    private String appName;

    /**
     * <pre>
     * 日志内容
     * 表字段 : br_sys_log.log_content
     * </pre>
     * 
     */
    private String logContent;

}