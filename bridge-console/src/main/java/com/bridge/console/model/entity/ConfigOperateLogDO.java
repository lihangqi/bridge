package com.bridge.console.model.entity;

import java.util.Date;

import lombok.Data;

/**
 * @author Jay
 * @version v1.0
 * @description k/v操作日志表
 * @date 2019-01-29 11:50
 */
@Data
public class ConfigOperateLogDO {
    /**
     * <pre>
     *
     * 表字段 : br_config_operate_log.id
     * </pre>
     */
    private Integer id;

    /**
     * <pre>
     * 创建人
     * 表字段 : br_config_operate_log.creator
     * </pre>
     */
    private Integer creator;

    /**
     * <pre>
     * 创建时间
     * 表字段 : br_config_operate_log.gmt_create
     * </pre>
     */
    private Date gmtCreate;

    /**
     * <pre>
     * 修改人
     * 表字段 : br_config_operate_log.modifier
     * </pre>
     */
    private Integer modifier;

    /**
     * <pre>
     * 修改时间
     * 表字段 : br_config_operate_log.gmt_modified
     * </pre>
     */
    private Date gmtModified;

    /**
     * <pre>
     * 0:未删除，1：已删除
     * 表字段 : br_config_operate_log.is_deleted
     * </pre>
     */
    private Integer isDeleted;

    /**
     * <pre>
     * 应用Id
     * 表字段 : br_config_operate_log.app_id
     * </pre>
     */
    private Integer appId;

    /**
     * <pre>
     * 所属环境 0:开发环境 1：测试环境 2:预发布环境 3:生产环境
     * 表字段 : br_config_operate_log.env_id
     * </pre>
     */
    private Integer envId;

    /**
     * <pre>
     * 操作人Id
     * 表字段 : br_config_operate_log.operate_id
     * </pre>
     */
    private Integer operateId;

    /**
     * <pre>
     * 操作人姓名
     * 表字段 : br_config_operate_log.operate_name
     * </pre>
     */
    private String operateName;

    /**
     * <pre>
     * 键
     * 表字段 : br_config_operate_log.config_key
     * </pre>
     */
    private String configKey;

    /**
     * <pre>
     * 操作前的值
     * 表字段 : br_config_operate_log.value_before
     * </pre>
     */
    private String valueBefore;

    /**
     * <pre>
     * 操作前的版本号
     * 表字段 : br_config_operate_log.version_before
     * </pre>
     */
    private String versionBefore;

    /**
     * <pre>
     * 操作后的值
     * 表字段 : br_config_operate_log.value_after
     * </pre>
     */
    private String valueAfter;

    /**
     * <pre>
     * 操作后的版本号
     * 表字段 : br_config_operate_log.version_after
     * </pre>
     */
    private String versionAfter;

    /**
     * <pre>
     * 操作类型 0:新增 1:修改 2:删除
     * 表字段 : br_config_operate_log.operate_type
     * </pre>
     */
    private Integer operateType;


    /**
     * <pre>
     * 描述
     * 表字段 : br_config_operate_log.key_des
     * </pre>
     */
    private String keyDes;
}