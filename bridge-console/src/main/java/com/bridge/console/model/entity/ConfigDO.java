package com.bridge.console.model.entity;

import java.util.Date;

import lombok.Data;

/**
 * @author Jay
 * @version v1.0
 * @description k/v表
 * @date 2019-01-29 11:50
 */
@Data
public class ConfigDO {

    /**
     * <pre>
     *
     * 表字段 : br_config.id
     * </pre>
     */
    private Integer id;

    /**
     * <pre>
     * 创建人
     * 表字段 : br_config.creator
     * </pre>
     */
    private Integer creator;

    /**
     * <pre>
     * 创建时间
     * 表字段 : br_config.gmt_create
     * </pre>
     */
    private Date gmtCreate;

    /**
     * <pre>
     * 修改人
     * 表字段 : br_config.modifier
     * </pre>
     */
    private Integer modifier;

    /**
     * <pre>
     * 修改时间
     * 表字段 : br_config.gmt_modified
     * </pre>
     */
    private Date gmtModified;

    /**
     * <pre>
     * 0:未删除，1：已删除
     * 表字段 : br_config.is_deleted
     * </pre>
     */
    private Integer isDeleted;

    /**
     * <pre>
     * 应用Id
     * 表字段 : br_config.app_id
     * </pre>
     */
    private Integer appId;

    /**
     * <pre>
     * 键
     * 表字段 : br_config.config_key
     * </pre>
     */
    private String configKey;

    /**
     * <pre>
     * 值
     * 表字段 : br_config.config_value
     * </pre>
     */
    private String configValue;

    /**
     * <pre>
     * 预备值
     * 表字段 : br_config.pre_config_value
     * </pre>
     */
    private String preConfigValue;

    /**
     * <pre>
     * 版本号
     * 表字段 : br_config.key_version
     * </pre>
     */
    private String keyVersion;

    /**
     * <pre>
     * 所属文件
     * 表字段 : br_config.key_type
     * </pre>
     */
    private String keyType;

    /**
     * <pre>
     * 下发状态 0:未下发 1:已下发
     * 表字段 : br_config.key_status
     * </pre>
     */
    private Integer keyStatus;


    /**
     * <pre>
     * 环境 0:开发环境 1：测试环境 2:预发布环境 3:生产环境
     * 表字段 : br_config.env_id
     * </pre>
     */
    private Integer envId;

    /**
     * <pre>
     * 键描述
     * 表字段 : br_config.key_des
     * </pre>
     */
    private String keyDes;
}