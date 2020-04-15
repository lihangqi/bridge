package com.bridge.console.model.entity;

import java.util.Date;

import lombok.Data;

/**
 * @author Jay
 * @version v1.0
 * @description app定义表
 * @date 2019-01-21 21:01
 */
@Data
public class AppDefDO {
    /**
     * <pre>
     *
     * 表字段 : br_app_def.id
     * </pre>
     */
    private Integer id;

    /**
     * <pre>
     * 创建人
     * 表字段 : br_app_def.creator
     * </pre>
     */
    private Integer creator;

    /**
     * <pre>
     * 创建时间
     * 表字段 : br_app_def.gmt_create
     * </pre>
     */
    private Date gmtCreate;

    /**
     * <pre>
     * 修改人
     * 表字段 : br_app_def.modifier
     * </pre>
     */
    private Integer modifier;

    /**
     * <pre>
     * 修改时间
     * 表字段 : br_app_def.gmt_modified
     * </pre>
     */
    private Date gmtModified;

    /**
     * <pre>
     * 0:未删除，1：已删除
     * 表字段 : br_app_def.is_deleted
     * </pre>
     */
    private Integer isDeleted;

    /**
     * <pre>
     * 启用状态 1:是 0:否
     * 表字段 : br_app_def.enabled_state
     * </pre>
     */
    private Integer enabledState;

    /**
     * <pre>
     * 系统编号
     * 表字段 : br_app_def.app_code
     * </pre>
     */
    private String appCode;

    /**
     * <pre>
     * 系统名称
     * 表字段 : br_app_def.app_name
     * </pre>
     */
    private String appName;

    /**
     * <pre>
     * 系统负责人
     * 表字段 : br_app_def.app_owner
     * </pre>
     */
    private Integer appOwner;

    /**
     * <pre>
     * 所属团队
     * 表字段 : br_app_def.team_id
     * </pre>
     */
    private Integer teamId;

    /**
     * <pre>
     * 系统描述
     * 表字段 : br_app_def.app_des
     * </pre>
     */
    private String appDes;
}