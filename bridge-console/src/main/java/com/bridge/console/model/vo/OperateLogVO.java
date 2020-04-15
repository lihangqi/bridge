package com.bridge.console.model.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author Jay
 * @version v1.0
 * @description 请添加类描述
 * @date 2019-03-12 13:28
 */
@Data
public class OperateLogVO {

    /**
     * <pre>
     *
     * 表字段 : br_config_operate_log.id
     * </pre>
     */
    private Integer id;

    /**
     * <pre>
     * 创建时间
     * 表字段 : br_config_operate_log.gmt_create
     * </pre>
     */
    private Date gmtCreate;


    /**
     * <pre>
     * 应用Id
     * 表字段 : br_config_operate_log.app_id
     * </pre>
     */
    private Integer appId;


    /**
     * 应用名称
     */
    private String appName;

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
     * 操作类型
     */
    private String operateTypeStr;
}
